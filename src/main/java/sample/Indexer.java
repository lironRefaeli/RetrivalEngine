package sample;

import org.json.JSONException;

import java.io.*;
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
    static public double NumberOfDocsInCorpus;
    public ReadFile readFile;
    public Parse parser;
    public String pathToDisk;
    public MergeFiles mergeFiles;
    public ExecutorService pool;
    public static Map<String, TermDataInMap> termsCorpusMap; //a map that includes all the terms in the corpus
    public Map<String, DocTermDataInMap> docsCorpusMap; //a map that includes all the terms in the corpus
    public static Map<String, CityInMap> citiesInCorpus = new HashMap<>();
    public static Map<String, CityInMap> citiesInAPI = new HashMap<>();
    public ConcurrentLinkedQueue<String> queueOfTempPostingFiles;
    public static final long startTime = System.nanoTime();
    static String postingFilesPath = "";
    public int IDsOfDocs = 0;
    public Map<Integer,String> docsAndIDs;
    JSON_reader json_reader;
    static boolean hasException = false;


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
        docsAndIDs = new HashMap<>();
        json_reader = new JSON_reader();


    }


    //The main function in this class
    //build the termsInCorpus map after getting every text parsed
    //build the maps docsInCorpus and docsAndIDs.
    public void Play() throws IOException{

        //connecting to API and bringing data about capital cities in the world
        try {
            json_reader.connectionToApi();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //the number of loop is determined by the numOfPostingFiles filed
        for (int i = 0; i < numOfTempPostingFiles; i++) {
            System.out.println("start loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);
            int maxTermFreqPerDoc = 0;
            List<String> listOfTexts = readFile.ReadFolder(8); //list of Documents' texts
            List<String> listOfDocsNumbers = readFile.getDocNumbersList();
            List<String> ListOfCities = readFile.getListOfCities();
            NumberOfDocsInCorpus += listOfDocsNumbers.size();

            if(parser.getStemmer())
                postingFilesPath = pathToDisk + "\\withStemming";
            else
                postingFilesPath = pathToDisk + "\\withoutStemming";
            Map<String, String> postingMap = new TreeMap<>(
                    new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {

                            return o1.toLowerCase().compareTo(o2.toLowerCase());
                        }
                    }
            );
            //loops over every text from one chunk of files
            for (int j = 0; j < listOfTexts.size(); j++) {
                //for every text we will build temporaryMap in order to save all the terms and their frequency (tf) by Parse object
                Map<String, Integer> temporaryMap = parser.ParsingDocument(listOfTexts.get(j), listOfDocsNumbers.get(j));
                //after parsing the text, we will create new record in the docs Map
                docsCorpusMap.put(listOfDocsNumbers.get(j),
                        new DocTermDataInMap(maxTermFreqPerDoc, temporaryMap.size(), ListOfCities.get(j)));
                docsAndIDs.put(IDsOfDocs,listOfDocsNumbers.get(j));
                IDsOfDocs++;
                //loops over one text's terms and merging temporaryMap to termsCorpusMap and to postingMap as well
                for (String term : temporaryMap.keySet()) {
                    //for calculating maxTf
                    if (temporaryMap.get(term) > maxTermFreqPerDoc)
                        maxTermFreqPerDoc = temporaryMap.get(term);

                    boolean termIsUpperCase = Parse.IsUpperCase(term);
                    boolean termIsLowerCase = Parse.IsLowerCase(term);
                    String termUpperCase = term.toUpperCase();
                    String termLowerCase = term.toLowerCase();
                    //terms with upper case letters
                    if (termIsUpperCase) {
                        if (termsCorpusMap.containsKey(termLowerCase)) { //contain that term but with lower case letters
                            termsCorpusMap.get(termLowerCase).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(termLowerCase).numOfDocuments++;
                        }
                        else if (termsCorpusMap.containsKey(term)) { //contain that term
                            //increasing frequency
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            //creating new record in termsCorpusMap
                            termsCorpusMap.put(term, new TermDataInMap(term,temporaryMap.get(term), 1));

                        String postingOldData = "";
                        if(postingMap.containsKey(termLowerCase)) //contain that term but with lower case letters
                        {
                            postingOldData = postingMap.get(termLowerCase);
                            postingMap.put(termLowerCase, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
                        }
                        else
                        {
                            if(postingMap.containsKey(term)) { //contain that term
                                postingOldData = postingMap.get(term);
                            }
                            //creating new record in postingMap
                            postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
                        }

                    }
                    //terms with lower case letters
                    else if (termIsLowerCase) {
                        //case the term is "first" and we already have "FIRST" on the map
                        //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
                        if (termsCorpusMap.containsKey(termUpperCase)) {
                            TermDataInMap dataOfupperCaseTerm = termsCorpusMap.get(termUpperCase);
                            termsCorpusMap.remove(termUpperCase);
                            termsCorpusMap.put(term, new TermDataInMap(term,dataOfupperCaseTerm.totalTf + temporaryMap.get(term),
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
                                termsCorpusMap.put(term, new TermDataInMap(term,temporaryMap.get(term), 1));
                        }

                        String postingOldData = "";
                        //case the term is "first" and we already have "FIRST" on the map
                        if(postingMap.containsKey(termUpperCase))
                        {
                            postingOldData = postingMap.get(termUpperCase);
                            postingMap.remove(termUpperCase);
                            postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");
                        }
                        //we do not have "FIRST" in our map
                        else
                        {    //we have "first" in our map
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
                            termsCorpusMap.put(term, new TermDataInMap(term,temporaryMap.get(term), 1));

                        String postingOldData = "";
                        if(postingMap.containsKey(term))
                            postingOldData = postingMap.get(term);

                        postingMap.put(term, postingOldData + IDsOfDocs + "~" + temporaryMap.get(term) + ",");

                    }

                }//End of looping on temporary map and inserts it's values to corpusMap

            }//End of internal loop - every loop is for the number of docs in that "chunk" of files

            WriteToTempPosting(postingMap, i); //write the posting map to a temporary posting file
            System.out.println("end loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);
            if(hasException)
                return;

        }//End of external loop - every loop is for one chunk of files (probably 8 files)

        //use thread in order to write CorpusTermMap, CorpusDocsMap and CorpusCitiesMap to a file

        try {
            WriteMapsToDisk();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException caught in lambda");
        }



        //After creating all temporary posting time, it's time to merge them to one big temporary file
        try {
            mergeTempPostingFiles();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //called from Play() method, writes the current postingMap into a temp posting file
    private void WriteToTempPosting(Map<String, String> postingMap, int numOftempPostingFile)  {
        //creating posting file and saving it in postingFilesFolder - his name is posting+the number of the loop
        File postinigFilesFolder = new File(postingFilesPath);
        postinigFilesFolder.mkdir();

        File file = new File(postingFilesPath + "\\posting_" + numOftempPostingFile+".txt");
        queueOfTempPostingFiles.add(file.getPath()); //that queue will serve us when merging the temp posting files
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(file),262144);
            for (String term : postingMap.keySet()) {
                //the structure is - "term*docNum~tf,"
                String data = term + "*" + postingMap.get(term);
                writer.write(data);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e)
        {
            hasException = true;

        }

    }

    //merges every two temp posting files using threads, until we get two last temp posting files
    public void mergeTempPostingFiles() throws IOException, InterruptedException {
        while (numOfTempPostingFiles > 2) {
            pool = Executors.newFixedThreadPool(numOfTempPostingFiles / 2 + 1);
            for (int i = 0; i < numOfTempPostingFiles / 2; i++) {
                pool.execute(new MergeFiles(postingFilesPath, this));
            }
            pool.shutdown();
            //wait until all threads in that loop had finished their tasks.
            pool.awaitTermination(1, TimeUnit.DAYS);
            //calculation for dealing with an odd number of posting files
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

    //reads the last two temp posting files, and sends their paths to margeTwoLastFilesAndCreatePermanentPostingFiles method
    public void splitMergedFile() throws IOException
    {
        List<File> twoLastFiles = new ArrayList<>();
        File folder = new File(postingFilesPath);
        //we know we only have two files left on that folder
        for (final File fileEntry : folder.listFiles())
            twoLastFiles.add(fileEntry);
        mergeFiles.margeTwoLastFilesAndCreatePermanentPostingFiles(twoLastFiles.get(0).getPath(), twoLastFiles.get(1).getPath());
        System.out.println("Finished building the Indexer - time: " + (System.nanoTime() - startTime) / 1000000000.0);

    }

    //This function is called as soon as the map termsInCorpus was built entirely
    //creates a file that contain that map as an object in order to reload it
    private void WriteMapsToDisk() throws IOException {

        File dictionaryFile;

        //writing the corpusTermMap to a file as an object
        if(parser.getStemmer())
            dictionaryFile = new File(pathToDisk+"\\CorpusTermsWithStemming");
        else
            dictionaryFile = new File(pathToDisk+"\\CorpusTermsWithoutStemming");

        FileOutputStream fileStream = new FileOutputStream(dictionaryFile);
        ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);

        // Write object to file
        outputStream.writeObject(termsCorpusMap);

        outputStream.close();
        fileStream.close();


        //writing the docsCorpusMap to a file as an object
        if(parser.getStemmer())
            dictionaryFile = new File(pathToDisk+"\\CorpusDocsWithStemming");
        else
            dictionaryFile = new File(pathToDisk+"\\CorpusDocsWithoutStemming");

        fileStream = new FileOutputStream(dictionaryFile);
        outputStream = new ObjectOutputStream(fileStream);

        // Write object to file
        outputStream.writeObject(docsCorpusMap);

        outputStream.close();
        fileStream.close();

        //writing the citiesCorpusMap to a file as an object
        if(parser.getStemmer())
            dictionaryFile = new File(pathToDisk+"\\CorpusCitiesWithStemming");
        else
            dictionaryFile = new File(pathToDisk+"\\CorpusCitiesWithoutStemming");

        fileStream = new FileOutputStream(dictionaryFile);
        outputStream = new ObjectOutputStream(fileStream);

        // Write object to file
        outputStream.writeObject(citiesInCorpus);

        outputStream.close();
        fileStream.close();
    }


    //todo load map
    public void SetTermsInCorpusMap(Map<String,TermDataInMap> loadingCorpusMap, boolean StemmerSelection)
    {
        termsCorpusMap = loadingCorpusMap;
    }

    public boolean CheckIfTermsInCorpusExists()
    {
        if(termsCorpusMap!=null)
            return true;
        return false;
    }


}//end of class Indexer



