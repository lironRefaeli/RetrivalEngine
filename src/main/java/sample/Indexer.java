package sample;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

/**
 * This class is building the inverted index.
 */
public class Indexer {
    public int numOfTempPostingFiles;
    static public int NumberOfDocsInCorpus;
    public int IDsOfDocs = 0;
    public ReadFile readFile;
    public Parse parser;
    public String pathToDisk;
    public MergeFiles mergeFiles;
    public ExecutorService pool;
    public static Map<String, TermDataInMap> termsCorpusMap; //a map that includes all the terms in the corpus
    public Map<String, DocTermDataInMap> docsCorpusMap; //a map that includes all the terms in the corpus
    public static Map<String, CityInMap> citiesInCorpus = new HashMap<>();
    public ConcurrentLinkedQueue<File> queueOfTempPostingFiles = new ConcurrentLinkedQueue<>();
    public Map<Integer,String> docsAndIDs;
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
        mergeFiles = new MergeFiles(pathToDisk, this);
        docsAndIDs = new HashMap<>();
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
            //todo section 6
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

               //todo section 9
                /*
                File outFile = new File("C:\\Users\\david\\Desktop\\WordsInTempMap");
                File outFile2 = new File("C:\\Users\\david\\Desktop\\FrequencyInTermMap");
                FileWriter fw = new FileWriter(outFile);
                BufferedWriter bw = new BufferedWriter(fw);
                FileWriter fw2 = new FileWriter(outFile2);
                BufferedWriter bw2 = new BufferedWriter(fw2);

                if(listOfDocsNumbers.get(j).equals("FBIS-3366"))
                {
                    for (String term : temporaryMap.keySet())
                    {
                        bw.write(term);
                        bw.newLine();
                        bw2.write(temporaryMap.get(term));
                        bw2.newLine();
                    }
                }
                bw.close();
                bw2.close();
                */
                //after parsing the text, we will creating new record in the docs Map
                docsCorpusMap.put(listOfDocsNumbers.get(j),
                        new DocTermDataInMap(maxTermFreqPerDoc, temporaryMap.size(), ListOfCities.get(j)));

                docsAndIDs.put(IDsOfDocs,listOfDocsNumbers.get(j));
                IDsOfDocs++;

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
                            postingMap.put(termLowerCase, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            if(postingMap.containsKey(term)) {
                                postingOldData = postingMap.get(term);
                            }

                            postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
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
                            postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            if(postingMap.containsKey(term))
                                postingOldData = postingMap.get(term);

                            postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
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

                        postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");


                    }

                }//End of looping on temporary map and inserts it's values to corpusMap


            }//End of internal loop - every loop is for one text

            WriteToTempPosting(postingMap, i); //write the posting map to a temporary posting file
            System.out.println("end loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);

        }//End of external loop - every loop is for one chunk of files (probably 8 files)


        File outFile = new File("C:\\Users\\david\\Desktop\\AllJunkWords");
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        for (String term : termsCorpusMap.keySet())
        {
            if(termsCorpusMap.get(term).totalTf == 1)
            {
                bw.write(term);
                bw.newLine();
            }
        }
        bw.close();
        fw.close();



        Parse.stopWordsList = null;
        Parse.wordsToDeleteSet = null;
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(file),262144);
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


        //Printing the IDF terms.
        //Printing corpus size
        File outFile = new File("C:\\Users\\david\\Desktop\\AllIDFWords");
        File outFile2 = new File("C:\\Users\\david\\Desktop\\termWithTF1");
        //File outFile3 = new File("C:\\Users\\david\\Desktop\\FrequencyOfTermsInCorpus");
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        FileWriter fw2 = new FileWriter(outFile2);
        BufferedWriter bw2 = new BufferedWriter(fw2);
        //FileWriter fw3 = new FileWriter(outFile3);
        //BufferedWriter bw3 = new BufferedWriter(fw3);
        int counterOfNumbersInCorpus = 0;
        for (String term : termsCorpusMap.keySet())
        {
            //bw2.write(term);
            //bw2.newLine();
            //todo section 7 and 8
            //bw3.write(termsCorpusMap.get(term).totalTf);
           // bw3.newLine();

            //todo 3
            //if(Parse.IsNumeric(term))
              //  counterOfNumbersInCorpus++;
        }
        //todo souts 1-3
        //System.out.println("the number of Numbers - section 3:" + counterOfNumbersInCorpus);
        //System.out.println("Printing the number of terms in corpus - section 1 or 2:" + termsCorpusMap.size() + "the status of stemming:" + parser.useStemmer);
       // bw3.close();
        bw2.close();
        bw.close();
        fw.close();
        fw2.close();
       // fw3.close();

        //todo section 6
        /*
        int maxSizeOfPositions = 0;
        String docNumber = "";
        String cityName = "";
        for (String city : citiesInCorpus.keySet())
        {
            for(String docNum : citiesInCorpus.get(city).placementsInDocs.keySet())
            {
                if(citiesInCorpus.get(city).placementsInDocs.get(docNum).size() > maxSizeOfPositions)
                {
                    maxSizeOfPositions = citiesInCorpus.get(city).placementsInDocs.get(docNum).size();
                    docNumber = docNum;
                    cityName = city;
                }
            }
        }
        System.out.println("Section 6: " + cityName + " " + docNumber + " " );
        for(int i = 0; i < citiesInCorpus.get(cityName).placementsInDocs.get(docNumber).size(); i++)
            System.out.println(citiesInCorpus.get(cityName).placementsInDocs.get(docNumber).get(i));
            */

    }

    //Will be used only for creating new CitiesAndInformationFile

    //Sections 4 and 5
    // todo section 4-5
    private void CreateCitiesAndInformationFile() throws IOException {
        File outFile = new File("C:\\Users\\david\\Desktop\\citiesAndState");
        FileWriter fw = new FileWriter(outFile);
        BufferedWriter bw = new BufferedWriter(fw);
        List<String> listOfAllCitiesInCorpus = new ArrayList<>();
        try {
            listOfAllCitiesInCorpus = readFile.ReadAllCitiesFromCorpusForCreatingInfoFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < listOfAllCitiesInCorpus.size(); i++)
        {
            bw.write(listOfAllCitiesInCorpus.get(i));
            bw.newLine();
        }
        bw.close();
        fw.close();


    }



        //todo dont delete this function!
        /*
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
        */



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



