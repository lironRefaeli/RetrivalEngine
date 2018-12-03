package sample;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is building the inverted index.
 */
public class Indexer {
    public int numOfTempPostingFiles;
    static public int NumberOfDocsInCorpus;
    public ReadFile readFile;
    public Parse parser;
    public String pathToDisk;
    public MergeFiles mergeFiles;
    public ExecutorService pool;
    public static Map<String, TermDataInMap> termsCorpusMap; //a map that includes all the terms in the corpus
    public Map<String, DocTermDataInMap> docsCorpusMap; //a map that includes all the terms in the corpus
    public static Map<String, CityInMap> citiesInCorpus = new HashMap<>();
    public ConcurrentLinkedQueue<File> queueOfTempPostingFiles;
    public final long startTime = System.nanoTime();


    public Indexer(ReadFile readFile, Parse parser, String pathToDisk)
    {
        NumberOfDocsInCorpus = 0;
        numOfTempPostingFiles = 227;
        this.readFile = readFile;
        this.parser = parser;
        this.pathToDisk = pathToDisk;
        termsCorpusMap = new HashMap<>();
        docsCorpusMap = new HashMap<>();
        queueOfTempPostingFiles = new ConcurrentLinkedQueue();
        mergeFiles = new MergeFiles(pathToDisk, this);
    }


    public void Play() throws IOException {
        //the number of loop is determined by the numOfChunks parameter
        for (int i = 0; i < numOfTempPostingFiles; i++) {
            System.out.println("start loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);
            int maxTermFreqPerDoc = 0;
            List<String> listOfTexts = readFile.ReadFolder(8); //list of Documents' texts
            List<String> listOfDocsNumbers = readFile.getDocNumbersList();
            List<String> ListOfCities = readFile.getListOfCities();
            NumberOfDocsInCorpus += listOfDocsNumbers.size();
            //CreateCitiesAndInformationFile(); //function for creating the cities and information file if needed

            Map<String, String> postingMap = new TreeMap<>(
                    new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {

                            return o1.toLowerCase().compareTo(o2.toLowerCase());
                        }
                    }
            );
            //loops over every text from one chunk
            for (int j = 0; j < listOfTexts.size(); j++) {
                //for every text we will build temporaryMap in order to save all the terms and their frequency (tf) by Parse object
                Map<String, Integer> temporaryMap = parser.ParsingDocument(listOfTexts.get(j), listOfDocsNumbers.get(j));
                //after parsing the text, we will creating new record in the docs Map
                docsCorpusMap.put(listOfDocsNumbers.get(j),
                        new DocTermDataInMap(maxTermFreqPerDoc, temporaryMap.size(), ListOfCities.get(j)));

                //loops over one text's terms and merging temporaryMap to termsCorpusMap
                for (String term : temporaryMap.keySet()) {
                    //for calculating maxTf
                    if (temporaryMap.get(term) > maxTermFreqPerDoc)
                        maxTermFreqPerDoc = temporaryMap.get(term);

                    boolean termIsUpperCase = Parse.IsUpperCase(term);
                    boolean termIsLowerCase = Parse.IsLowerCase(term);
                    String termUpperCase = term.toUpperCase();
                    String termLowerCase = term.toLowerCase();
                    //NBA or GSW
                    if (termIsUpperCase) {
                        if (termsCorpusMap.containsKey(termLowerCase)) {
                            termsCorpusMap.get(termLowerCase).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(termLowerCase).numOfDocuments++;
                        }
                        else if (termsCorpusMap.containsKey(term)) {
                            //increasing frequency
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            //creating new record in termsCorpusMap
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));

                        String postingOldData = "";
                        if(postingMap.containsKey(termLowerCase))
                        {
                            postingOldData = postingMap.get(termLowerCase);
                            postingMap.put(termLowerCase, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            if(postingMap.containsKey(term)) {
                                postingOldData = postingMap.get(term);
                            }

                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }

                    }
                    //liron or first
                    else if (termIsLowerCase) {
                        //case term is "first" and we already have "FIRST" on the map
                        //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
                        if (termsCorpusMap.containsKey(termUpperCase)) {
                            TermDataInMap dataOfupperCaseTerm = termsCorpusMap.get(termUpperCase);
                            termsCorpusMap.remove(termUpperCase);
                            termsCorpusMap.put(term, new TermDataInMap(dataOfupperCaseTerm.totalTf + temporaryMap.get(term),
                                    dataOfupperCaseTerm.numOfDocuments++));
                        }
                        //we do not have "FIRST" in our map
                        else {
                            //we have "first" in our map
                            if (termsCorpusMap.containsKey(term)) {
                                termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                                termsCorpusMap.get(term).numOfDocuments++;
                            }
                            //adds "first" to the map
                            else
                                termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                        }

                        String postingOldData = "";
                        if(postingMap.containsKey(termUpperCase))
                        {
                            postingOldData = postingMap.get(termUpperCase);
                            postingMap.remove(termUpperCase);
                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            if(postingMap.containsKey(term))
                                postingOldData = postingMap.get(term);

                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }

                    }
                    //every other thing - like numbers or undefined characters
                    else {
                        if (termsCorpusMap.containsKey(term)) {
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));

                        String postingOldData = "";
                        if(postingMap.containsKey(term))
                            postingOldData = postingMap.get(term);

                        postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");


                    }

                /*
                    if(termsCorpusMap.containsKey(term.toLowerCase()))
                    {
                        //checking if posting map contains the term
                        if (!postingMap.containsKey(term.toLowerCase()))
                        {
                            //creating new record
                            postingMap.put(term.toLowerCase(), listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            //deleting old record and creating new one
                            String postingOldData = postingMap.get(term.toLowerCase());
                            postingMap.remove(term.toLowerCase());
                            postingMap.put(term.toLowerCase(), postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                    }
                    else
                    {
                        //checking if posting map contains the term
                        if (!postingMap.containsKey(term)) {
                            //creating new record
                            postingMap.put(term, listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        } else {
                            //deleting old record and creating new one
                            String postingOldData = postingMap.get(term);
                            postingMap.remove(term);
                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                    }



                    //first time from that term in this chunk
                    if (!postingMap.containsKey(termLowerCase))
                        postingMap.put(termLowerCase, listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        //deleting old record and creating new one
                    else {
                        String postingOldData = postingMap.get(termLowerCase);
                        postingMap.put(termLowerCase, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                    }

                    */


                }//End of looping on temporary map and inserts it's values to corpusMap



                /*
                    if(termsCorpusMap.containsKey(term.toLowerCase()))
                    {
                        //checking if posting map contains the term
                        if (!postingMap.containsKey(term.toLowerCase())) {
                            //creating new record
                            postingMap.put(term.toLowerCase(), listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        } else {
                            //deleting old record and creating new one
                            String postingOldData = postingMap.get(term.toLowerCase());
                            postingMap.remove(term.toLowerCase());
                            postingMap.put(term.toLowerCase(), postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                    }
                    else
                    {
                        //checking if posting map contains the term
                        if (!postingMap.containsKey(term)) {
                            //creating new record
                            postingMap.put(term, listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        } else {
                            //deleting old record and creating new one
                            String postingOldData = postingMap.get(term);
                            postingMap.remove(term);
                            postingMap.put(term, postingOldData + listOfDocsNumbers.get(j) + "~" + temporaryMap.get(term) + ",");
                        }
                    }
                }
                */


            }//End of internal loop - every loop is for one text

            WriteToTempPosting(postingMap, i); //write the posting map to a temporary posting file
            System.out.println("end loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);

        }//End of external loop - every loop is for one chunk of files (probably 8 files)

        //After creating all temporary posting time, it's time to merge them to one big temporary file
        try {
            mergeTempPostingFiles();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void WriteToTempPosting(Map<String, String> postingMap, int numOftempPostingFile) throws IOException {
        //creating posting file and saving it in postingFilesFolder - his name is posting+the number of the loop
        File file = new File(pathToDisk + "\\posting_" + numOftempPostingFile);
        queueOfTempPostingFiles.add(file);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (String term : postingMap.keySet()) {
            //the structure is - "term*docNum~tf,"
            String data = term + "*" + postingMap.get(term);
            writer.write(data);
            writer.newLine();
        }
        writer.close();
    }

    public void mergeTempPostingFiles() throws IOException, InterruptedException {
        while (numOfTempPostingFiles > 2) {
            pool = Executors.newFixedThreadPool(numOfTempPostingFiles / 2 + 1);
            for (int i = 0; i < numOfTempPostingFiles / 2; i++) {
                pool.execute(new MergeFiles(pathToDisk, this));

            }
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.DAYS);
            if (numOfTempPostingFiles % 2 != 0)
                numOfTempPostingFiles = numOfTempPostingFiles / 2 + 1;
            else
                numOfTempPostingFiles = numOfTempPostingFiles / 2;
        }
        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("done merging all files - time: " + (System.nanoTime() - startTime) / 1000000000.0);
        splitMergedFile();


    }

    public void splitMergedFile() throws IOException
    {
        List<File> twoLastFiles = new ArrayList<>();
        File folder = new File(pathToDisk);
        //we know we only have two files left on that folder
        for (final File fileEntry : folder.listFiles())
            twoLastFiles.add(fileEntry);
        mergeFiles.margeTwoLastFilesAndCreatePermanentPostingFiles(twoLastFiles.get(0), twoLastFiles.get(1));
        System.out.println("Finished building the Indexer - time: " + (System.nanoTime() - startTime) / 1000000000.0);
    }
}

        /*
        String nextLineInFile = "";
        //spliting to symbols
        File symbolFile = new File("C:\\Users\\refaeli.liron\\IdeaProjects\\RetrivalEngine_LD\\src\\main\\java\\postingFiles\\symbols");
        FileWriter fw1 = new FileWriter(symbolFile);
        BufferedWriter bw1 = new BufferedWriter(fw1);


        if (folder.listFiles().length == 1) {
            for (final File fileEntry : folder.listFiles()) {

                Scanner fileReader = new Scanner(fileEntry);

                while (fileReader.hasNextLine()) {
                    nextLineInFile = fileReader.nextLine();
                    if (!(nextLineInFile.toLowerCase().charAt(0) >= 97 && nextLineInFile.toLowerCase().charAt(0) <= 122)) {
                        bw1.write(nextLineInFile);
                        splitedLine = nextLineInFile.split("\\*");
                        termsCorpusMap.get(splitedLine[0]).pointerToPostingLine = lineCounter;
                        termsCorpusMap.get(splitedLine[0]).idf = Math.log10(NumberOfDocsInCorpus/(termsCorpusMap.get(splitedLine[0]).numOfDocuments));
                        lineCounter++;
                        bw1.newLine();
                    } else {
                        break;
                    }
                }
                bw1.close();
                lineCounter = 1;

                //spliting to characters
                for (int i = 97; i <= 122; i++) {
                    char firstChar = (char) i;
                    File file = new File("C:\\Users\\refaeli.liron\\IdeaProjects\\RetrivalEngine_LD\\src\\main\\java\\postingFiles\\" + firstChar);
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);

                    if (!nextLineInFile.equals("") && nextLineInFile.toLowerCase().charAt(0) == firstChar) {
                        bw.write(nextLineInFile);
                        splitedLine = nextLineInFile.split("\\*");
                        termsCorpusMap.get(splitedLine[0]).pointerToPostingLine = lineCounter;
                        termsCorpusMap.get(splitedLine[0]).idf = Math.log10(NumberOfDocsInCorpus/(termsCorpusMap.get(splitedLine[0]).numOfDocuments));
                        lineCounter++;
                        bw.newLine();
                    }

                    while (fileReader.hasNextLine()) {
                        nextLineInFile = fileReader.nextLine();
                        if (nextLineInFile.toLowerCase().charAt(0) == firstChar) {
                            bw.write(nextLineInFile);
                            splitedLine = nextLineInFile.split("\\*");
                            try{
                                termsCorpusMap.get(splitedLine[0]).pointerToPostingLine = lineCounter;
                            }
                            catch(NullPointerException e)
                            {
                                System.out.println(splitedLine[0]);
                            }
                            termsCorpusMap.get(splitedLine[0]).idf = Math.log10(NumberOfDocsInCorpus/(termsCorpusMap.get(splitedLine[0]).numOfDocuments));
                            lineCounter++;
                            bw.newLine();
                        } else {
                            break;
                        }
                    }

                    bw.close();
                    lineCounter = 1;
                }


                fileReader.close();
                fileEntry.delete();


            }
        }
    }



    //Will be used only for creating new CitiesAndInformationFile
    private void CreateCitiesAndInformationFile() throws IOException {
        List<String> listOfAllCitiesInCorpus = new ArrayList<>();
        try {
            listOfAllCitiesInCorpus = readFile.ReadAllCitiesFromCorpusForCreatingInfoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSON_reader Jread = new JSON_reader();
        Map<String,String> ContainsAllCitiesAndInformation = new HashMap<>();
        for(int k = 0; k < listOfAllCitiesInCorpus.size(); k++) {
            try {
                if(!listOfAllCitiesInCorpus.get(k).equals(""))
                {
                    String data = Jread.connectionToApi(listOfAllCitiesInCorpus.get(k));
                    if(data == null)
                        continue;
                    ContainsAllCitiesAndInformation.put(listOfAllCitiesInCorpus.get(k), data);
                }
            } catch (JSONException e) {
               continue;
            }
        }
        WriteCitiesAndInformationMapToFile(ContainsAllCitiesAndInformation);


    }

    //Will be used only for creating new CitiesAndInformationFile
    private void WriteCitiesAndInformationMapToFile(Map<String,String> containsAllCitiesAndInformation) throws IOException {
        File file = new File(pathToDisk + "\\CitiesAndInformationFile");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for ( String key : containsAllCitiesAndInformation.keySet() ) {
            String oneLine = key + "*" + containsAllCitiesAndInformation.get(key);
            writer.write(oneLine);
            writer.newLine();
        }
        writer.close();
    }


}



/*

        while (numberOfFiles > 1) {
            for (int i = 0; i < numberOfFiles / 2; i++) {

                pool.execute(new MergeFiles(pathToDisk, this));

            }


            System.out.println("num of files that were merged: " + numberOfFiles + " - time: " + (System.nanoTime() - startTime) / 1000000000.0);

            if (numberOfFiles % 2 != 0)
                numberOfFiles = numberOfFiles / 2 + 1;
            else
                numberOfFiles = numberOfFiles / 2;
        }
        pool.awaitTermination(10, TimeUnit.SECONDS);
        System.out.println("done merging all files - time: " + (System.nanoTime() - startTime) / 1000000000.0);
        pool.shutdown();
        while (!pool.awaitTermination(24L, TimeUnit.HOURS)) {
            System.out.println("Not yet. Still waiting for termination");
        }
*/












