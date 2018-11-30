package sample;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class is building the inverted index.
 */
public class Indexer {
    ReadFile readFile;
    Parse parser;
    String pathToDisk;
    Map<String, TermDataInMap> termsCorpusMap; //a map that includes all the terms in the corpus
    Map<String, DocTermDataInMap> docsCorpusMap; //a map that includes all the terms in the corpus
    MergeFiles mergeFiles;
    int numOfChunks;
    ExecutorService pool;
    ConcurrentLinkedQueue<File> queueOfTempPostingFiles;
    final long startTime = System.nanoTime();


    public Indexer(ReadFile readFile, Parse parser, String pathToDisk) {
        numOfChunks = 227;
        this.readFile = readFile;
        this.parser = parser;
        this.pathToDisk = pathToDisk;
        termsCorpusMap = new HashMap<String, TermDataInMap>();
        docsCorpusMap = new HashMap<String, DocTermDataInMap>();
        try {
            mergeFiles = new MergeFiles(pathToDisk, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool = Executors.newFixedThreadPool(20);
        queueOfTempPostingFiles = new ConcurrentLinkedQueue();
    }


    public void Play() throws IOException {

        //number of loops in order to go through all the corpus' folders


        //the number of loop is determined by the numOfChunks parameter
        for (int i = 0; i < numOfChunks; i++) {
            System.out.println("start loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);

            //lists that will save documents content: texts, docsNumbers, cities.
            List<String> listOfTexts = readFile.ReadFolder(8);
            List<String> listOfDocsNumbers = readFile.getDocNumbersList();
            List<String> listOfDocsCities = readFile.getDocCitiesList();
            Map<String, String> postingMap = new TreeMap<String, String>();// a map that includes posting data about the chunk of files

            //loops over every text from one chunk
            for (int j = 0; j < listOfTexts.size(); j++) {


                //for every text we will build temporaryMap in order to save all the terms and their frequency (tf) by Parse object
                Map<String, Integer> temporaryMap = parser.ParsingDocument(listOfTexts.get(j));

                //after parsing the text, we will creating new record in the docs Map
                //docsCorpusMap.put(listOfDocsNumbers.get(j), new DocTermDataInMap(returnMaxTf(temporaryMap), temporaryMap.size(), listOfDocsCities.get(j)));
                //loops over one text's terms and merging temporaryMap to termsCorpusMap
                for (String term : temporaryMap.keySet()) {
                    //NBA or GSW
                    if (Parse.IsUpperCase(term)) {
                        if (termsCorpusMap.containsKey(term)) {
                            //increasing frequency
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            //creating new record in termsCorpusMap
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                    }
                    //liron or first
                    else if (Parse.IsLowerCase(term)) {
                        //case term is "first" and we already have "FIRST" on the map
                        //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
                        if (termsCorpusMap.containsKey(term.toUpperCase())) {
                            TermDataInMap upperCaseTerm = termsCorpusMap.get(term.toUpperCase());
                            termsCorpusMap.remove(term.toUpperCase());
                            termsCorpusMap.put(term, new TermDataInMap(upperCaseTerm.totalTf + temporaryMap.get(term), upperCaseTerm.numOfDocuments + 1));
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

                    }
                    //every other thing - like numbers or undefined characters
                    else {
                        if (termsCorpusMap.containsKey(term)) {
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                    }

                }

                //updating the posting map after finishing parsing a text
                for (String term : temporaryMap.keySet()) {
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
            WriteToTempPosting(postingMap, i);
            System.out.println("end loop number: " + i + " time: " + (System.nanoTime() - startTime) / 1000000000.0);
        }
        try {
            mergeTempPostingFiles();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }

    private void WriteToTempPosting(Map<String, String> postingMap, int numOfChunck) throws IOException {
        //creating posting file and saving it in postingFilesFolder - his name is posting+the number of the loop
        File file = new File(pathToDisk + "\\posting_" + numOfChunck);
        queueOfTempPostingFiles.add(file);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));        //adding to the file all the term+posting data from posting map
        for (String term : postingMap.keySet()) {
            //the structure is - "term*docNum~tf,"
            String data = term + "*" + postingMap.get(term);
            writer.write(data);
            writer.newLine();
        }
        //closing the file
        writer.close();
    }

    int numberOfFiles = 227; // = 227

    public void mergeTempPostingFiles() throws IOException, InterruptedException {

        while (numberOfFiles > 1) {
            for (int i = 0; i < numberOfFiles / 2; i++) {

                pool.execute(new MergeFiles(pathToDisk, this));

            }

            pool.awaitTermination(3, TimeUnit.SECONDS);

            System.out.println("num of files that were merged: " + numberOfFiles + " - time: " + (System.nanoTime() - startTime) / 1000000000.0);

            if (numberOfFiles % 2 != 0)
                numberOfFiles = numberOfFiles / 2 + 1;
            else
                numberOfFiles = numberOfFiles / 2;
        }

        System.out.println("done merging all files - time: " + (System.nanoTime() - startTime) / 1000000000.0);

        splitMergedFile();


    }

    public void splitMergedFile() throws IOException {

        File folder = new File("C:\\Users\\refaeli.liron\\IdeaProjects\\RetrivalEngine_LD\\src\\main\\java\\postingFiles");

        if(folder.listFiles().length == 1) {
            for (final File fileEntry : folder.listFiles()) {

                Scanner fileReader = new Scanner(fileEntry);
                String nextLine = "";

                //spliting to symbols
                File symbolFile = new File("C:\\Users\\refaeli.liron\\IdeaProjects\\RetrivalEngine_LD\\src\\main\\java\\postingFiles\\symbols");
                FileWriter fw1 = new FileWriter(symbolFile);
                BufferedWriter bw1 = new BufferedWriter(fw1);

                while (fileReader.hasNext()) {
                    nextLine = fileReader.next();
                    if (!(nextLine.toLowerCase().charAt(0) >= 97 && nextLine.toLowerCase().charAt(0) <= 122)) {
                        bw1.write(nextLine);
                        bw1.newLine();
                    } else {
                        break;
                    }
                }
                bw1.close();

                //spliting to characters
                for (int i = 97; i <= 122; i++) {
                    char firstChar = (char) i;
                    File file = new File("C:\\Users\\refaeli.liron\\IdeaProjects\\RetrivalEngine_LD\\src\\main\\java\\postingFiles\\" + firstChar);
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);

                    if (!nextLine.equals("") && nextLine.toLowerCase().charAt(0) == firstChar) {
                        bw.write(nextLine);
                        bw.newLine();
                    }

                    while (fileReader.hasNext()) {
                        nextLine = fileReader.next();
                        if (nextLine.toLowerCase().charAt(0) == firstChar) {
                            bw.write(nextLine);
                            bw.newLine();
                        } else {
                            break;
                        }
                    }

                    bw.close();
                }
            }
        }

        System.out.println("spliting was done - time: " + (System.nanoTime() - startTime) / 1000000000.0);


    }
}

/*

    /**
     * the function is calculating the maximum term's frequency (max_tf) in a text
     * @param temporaryMap
     * @return

    private int returnMaxTf(Map<String, Integer> temporaryMap)
    {
        int maxTf = -1;
        for(int value: temporaryMap.values())
        {
            if(maxTf < value)
                maxTf = value;
        }
        return maxTf;
    }
  */






