package sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is building the inverted index.
 */
public class Indexer {
    ReadFile readFile;
    Parse parser;
    String pathToDisk;
    Map<String, TermDataInMap> termsCorpusMap; //a map that includes all the terms in the corpus
    Map<String, DocTermDataInMap> docsCorpusMap; //a map that includes all the terms in the corpus
    private final Object lock;

    public Indexer(ReadFile readFile, Parse parser, String pathToDisk) {
        this.readFile = readFile;
        this.parser = parser;
        this.pathToDisk = pathToDisk;
        termsCorpusMap = new HashMap<String, TermDataInMap>();
        docsCorpusMap = new HashMap<String, DocTermDataInMap>();
        lock = new Object();
    }


    public void Play() throws IOException {

        final long startTime = System.nanoTime();
        int numOfChunks = 2; //number of loops in order to go through all the corpus' folders


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
                docsCorpusMap.put(listOfDocsNumbers.get(j), new DocTermDataInMap(returnMaxTf(temporaryMap), temporaryMap.size(), listOfDocsCities.get(j)));
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
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 12));
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
            System.out.println("end loop number: "+ i +" time: "+(System.nanoTime()-startTime)/1000000000.0);
        }
    }

    private void WriteToTempPosting(Map<String,String> postingMap, int numOfChunck) throws IOException {
        //creating posting file and saving it in postingFilesFolder - his name is posting+the number of the loop
        File file = new File(pathToDisk + "\\posting_" + numOfChunck);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));        //adding to the file all the term+posting data from posting map
        for (String term : postingMap.keySet())
        {
            //the structure is - "term*docNum~tf,"
            String data = term + "*" + postingMap.get(term);
            writer.write(data);
            writer.newLine();
        }
        //closing the file
        writer.close();
    }

    private TreeMap<String, String> SortByKey(Map<String,String> postingMap)
    {
        // TreeMap to store values of HashMap
        TreeMap<String, String> sorted = new TreeMap();

        // Copy all data from hashMap into TreeMap
        sorted.putAll(postingMap);

        return sorted;
    }


    /**
     * the function is calculating the maximum term's frequency (max_tf) in a text
     * @param temporaryMap
     * @return
     */
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
}



