package sample;

import javafx.scene.control.Alert;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Ranker {

    boolean withStemmer;
    boolean withSemantic;
    String pathToDisk;
    Double averageDocsLength;
    double k;
    double b;
    public static List<String> listCitiesFromUser = new ArrayList<>();
    Map<String, Double> rankedDocumentsMap;


    public Ranker(boolean stemmerSelection, boolean semanticSelection, String pathToDisk)
    {
        withStemmer = stemmerSelection;
        withSemantic = semanticSelection;
        this.pathToDisk = pathToDisk;
        averageDocsLength = clacAverageLength(Indexer.docsCorpusMap);
        k = 1.2;
        b = 0.75;
    }

    /**
     * responsible for finding the 50 most relevant documents for a query
     * @param queryMap contains one query's terms and their frequencies
     * @param descriptionMap contains one description's terms and their frequencies
     * @return a map with 50 documents (keys) and their rank (values)
     */
    public Map<String, Double> RankDocumentsByQuery(Map<String, Integer> queryMap, Map<String, Integer> descriptionMap)
    {
        rankedDocumentsMap = new HashMap<>();
        List<String> wordsQueryList = new ArrayList<>();
        List<String> wordsDescriptionList = new ArrayList<>();
        List<String> wordsAPIList = new ArrayList<>();

        //if user chooses to retrieve with semantic, we will create a list of syn words from the API
        List<String> wordsMlAPI = new ArrayList<>();
        if (withSemantic)
        {
            for (String term : queryMap.keySet())
            {
                //the syn API
                wordsAPIList.addAll(JSON_reader.connectionToSynApi(term));
                //the ml API - words that have same meaning
                wordsMlAPI.addAll(JSON_reader.connectionToMLApi(term));
                for(int i = 0; i < wordsMlAPI.size(); i++)
                {
                    if(!wordsAPIList.contains(wordsMlAPI.get(i)))
                        wordsAPIList.add(wordsMlAPI.get(i));
                }
            }
        }

        //list that contains all the query's terms
        for (String term : queryMap.keySet())
            wordsQueryList.add(term);

        //list that contains all the description's terms
        if(descriptionMap != null)
        {
            for (String term : descriptionMap.keySet())
                wordsDescriptionList.add(term);
            //have the description's words impact on the ranks of the documents
            ClacScoreOfTermsInDescription(wordsDescriptionList);
        }
        //have the query's words impact on the ranks of the documents
        ClacScoreOfTermsInQuery(wordsQueryList);
        //have the API's words impact on the ranks of the documents
        if(withSemantic)
             ClacScoreOfTermsInAPI(wordsAPIList);

        //This section is responsible to create a sorted map (by rank) that contains only the most 50 ranked documents
        Map<String, Double> sortedRankedDocumentMap = new LinkedHashMap<>();
        List<Map.Entry<String, Double>> list = new ArrayList<>(rankedDocumentsMap.entrySet());
        list.sort(Map.Entry.comparingByValue());

        if (rankedDocumentsMap.size() < 50) {
            for (int i = list.size() - 1; i >= 0; i--)
                sortedRankedDocumentMap.put(list.get(i).getKey(), list.get(i).getValue());
        } else {
            for (int i = list.size() - 50; i < list.size(); i++)
                sortedRankedDocumentMap.put(list.get(i).getKey(), list.get(i).getValue());
        }

        return sortedRankedDocumentMap;
    }

    private void ClacScoreOfTermsInDescription(List<String> wordsDescriptionList)
    {
        //loop that runs over every term in the query
        for (int j = 0; j < wordsDescriptionList.size(); j++)
        {
            //check if the term is on the corpus. if not, throw it away
            String term = wordsDescriptionList.get(j);
            String termLowerCase = term.toLowerCase();
            String termUpperCase = term.toUpperCase();
            if (Indexer.termsCorpusMap.containsKey(termLowerCase))
                term = termLowerCase;
            else if (Indexer.termsCorpusMap.containsKey(termUpperCase))
                term = termUpperCase;
            else
                continue;

            //bring the posting file that contains that term
            int pointerToPostingLine = Indexer.termsCorpusMap.get(term).pointerToPostingLine;
            File postingFile;
            if (withStemmer)
                postingFile = new File(pathToDisk + "\\withStemming\\" + term.charAt(0) + ".txt");
            else
                postingFile = new File(pathToDisk + "\\withoutStemming\\" + term.charAt(0) + ".txt");

            if (postingFile != null) {
                //read the line of that term from the posting files (reads docs numbers and frequency in doc)
                String line;
                try (BufferedReader br = new BufferedReader(new FileReader(postingFile)))
                {
                    for (int i = 1; i < pointerToPostingLine; i++)
                        br.readLine();
                    line = br.readLine();
                }
                catch (IOException e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not find the posting file of the letter " + term.charAt(0));
                    alert.showAndWait();
                    return;
                }


                //split the posting file by the next chars
                List<String> docsAndFreqInLine = new ArrayList(Arrays.asList(line.split("~|\\*|,")));
                List<String> headlineOfDoc;
                String docNumber;

                //this loop run over all the docs the term is included and term's frequency
                for (int i = 1; i < docsAndFreqInLine.size(); i += 2) {
                    String docID = docsAndFreqInLine.get(i);
                    docNumber = Indexer.docsAndIDs.get(Integer.parseInt(docID));

                    //check if the doc is relevant for the cities that the user chose(if he chose)
                    if (listCitiesFromUser.size() > 0) {
                        if (!checkIfDocIsReleventToCities(docNumber))
                            continue;

                    }

                    int frequencyInDoc = Integer.parseInt(docsAndFreqInLine.get(i + 1));
                    int numOfDocuments = Indexer.termsCorpusMap.get(term).numOfDocuments;
                    int docSize = Indexer.docsCorpusMap.get(docNumber).numOfTerms;


                    //the BM25 equation
                    double firstPart = Math.log10(1 / ((numOfDocuments + 0.5) / (Indexer.docsCorpusMap.size() - numOfDocuments + 0.5)));
                    double secondPart = ((k + 1) * frequencyInDoc) / (frequencyInDoc + k * ((1 - b) + b * (docSize / averageDocsLength)));
                    double scoreQueryAndDoc = firstPart * secondPart;
                    scoreQueryAndDoc = 0.01 * scoreQueryAndDoc;


                    //for words that are on the headline of the doc
                    headlineOfDoc = Indexer.docsCorpusMap.get(docNumber).headline;
                    if(headlineOfDoc != null)
                    {
                        if (headlineOfDoc.contains(termLowerCase) || headlineOfDoc.contains(termUpperCase))
                            scoreQueryAndDoc = 1.05 * scoreQueryAndDoc;
                    }

                    //if the document already was ranked before, take its previous rank and add it to the new rank
                    double prevRank = 0.0;
                    if (rankedDocumentsMap.containsKey(docNumber))
                        prevRank = rankedDocumentsMap.get(docNumber);

                    rankedDocumentsMap.put(docNumber, prevRank + scoreQueryAndDoc);

                }

            }

        }
    }

    private void ClacScoreOfTermsInAPI(List<String> wordsAPIList)
    {
        //loop that runs over every term in the query
        for (int j = 0; j < wordsAPIList.size(); j++)
        {
            //check if the term is on the corpus. if not, throw it away
            String term = wordsAPIList.get(j);
            String termLowerCase = term.toLowerCase();
            String termUpperCase = term.toUpperCase();
            if (Indexer.termsCorpusMap.containsKey(termLowerCase))
                term = termLowerCase;
            else if (Indexer.termsCorpusMap.containsKey(termUpperCase))
                term = termUpperCase;
            else
                continue;

            //brings the posting file that contains that term
            int pointerToPostingLine = Indexer.termsCorpusMap.get(term).pointerToPostingLine;
            File postingFile;
            if (withStemmer)
                postingFile = new File(pathToDisk + "\\withStemming\\" + term.charAt(0) + ".txt");
            else
                postingFile = new File(pathToDisk + "\\withoutStemming\\" + term.charAt(0) + ".txt");

            if (postingFile != null) {
                //read the line of that term from the posting files (reads docs numbers and frequency in doc)
                String line;
                try (BufferedReader br = new BufferedReader(new FileReader(postingFile)))
                {
                    for (int i = 1; i < pointerToPostingLine; i++)
                        br.readLine();
                    line = br.readLine();
                }
                catch (IOException e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not find the posting file of the letter " + term.charAt(0));
                    alert.showAndWait();
                    return;
                }

                //split the posting file by the next chars
                List<String> docsAndFreqInLine = new ArrayList(Arrays.asList(line.split("~|\\*|,")));
                List<String> headlineOfDoc;
                String docNumber;

                //this loop run over all the docs the term is included and term's frequency
                for (int i = 1; i < docsAndFreqInLine.size(); i += 2) {
                    String docID = docsAndFreqInLine.get(i);
                    docNumber = Indexer.docsAndIDs.get(Integer.parseInt(docID));

                    //check if the doc is relevant for the cities that the user chose(if he chose)
                    if (listCitiesFromUser.size() > 0)
                    {
                        if (!checkIfDocIsReleventToCities(docNumber))
                            continue;
                    }

                    int frequencyInDoc = Integer.parseInt(docsAndFreqInLine.get(i + 1));
                    int numOfDocuments = Indexer.termsCorpusMap.get(term).numOfDocuments;
                    int docSize = Indexer.docsCorpusMap.get(docNumber).numOfTerms;


                    //the BM25 equation
                    double firstPart = Math.log10(1 / ((numOfDocuments + 0.5) / (Indexer.docsCorpusMap.size() - numOfDocuments + 0.5)));
                    double secondPart = ((k + 1) * frequencyInDoc) / (frequencyInDoc + k * ((1 - b) + b * (docSize / averageDocsLength)));
                    double scoreQueryAndDoc = firstPart * secondPart;
                    scoreQueryAndDoc = 0.3 * scoreQueryAndDoc;


                    //for words that are on the headline of the doc
                    headlineOfDoc = Indexer.docsCorpusMap.get(docNumber).headline;
                    if(headlineOfDoc != null)
                    {
                        if (headlineOfDoc.contains(termLowerCase) || headlineOfDoc.contains(termUpperCase))
                            scoreQueryAndDoc = 1.05 * scoreQueryAndDoc;
                    }

                    //if the document already was ranked before, take its previous rank and add it to the new rank
                    double prevRank = 0.0;
                    if (rankedDocumentsMap.containsKey(docNumber))
                        prevRank = rankedDocumentsMap.get(docNumber);

                    rankedDocumentsMap.put(docNumber, prevRank + scoreQueryAndDoc);

                }

            }

        }
    }

    private void ClacScoreOfTermsInQuery(List<String> wordsQueryList)
    {
        //loop that runs over ecery term in the query
        for (int j = 0; j < wordsQueryList.size(); j++)
        {
            //check if the term is on the corpus. if not, throw it away
            String term = wordsQueryList.get(j);
            String termLowerCase = term.toLowerCase();
            String termUpperCase = term.toUpperCase();
            if (Indexer.termsCorpusMap.containsKey(termLowerCase))
                term = termLowerCase;
            else if (Indexer.termsCorpusMap.containsKey(termUpperCase))
                term = termUpperCase;
            else
                continue;

            //brings the posting file that contains that term
            int pointerToPostingLine = Indexer.termsCorpusMap.get(term).pointerToPostingLine;
            File postingFile;
            if (withStemmer)
                postingFile = new File(pathToDisk + "\\withStemming\\" + term.charAt(0) + ".txt");
            else
                postingFile = new File(pathToDisk + "\\withoutStemming\\" + term.charAt(0) + ".txt");

            if (postingFile != null) {
                //read the line of that term from the posting files (reads docs numbers and frequency in doc)
                String line;
                try (BufferedReader br = new BufferedReader(new FileReader(postingFile)))
                {
                    for (int i = 1; i < pointerToPostingLine; i++)
                        br.readLine();
                    line = br.readLine();
                }
                catch (IOException e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not find the posting file of the letter " + term.charAt(0));
                    alert.showAndWait();
                    return;
                }

                //split the posting file by the next chars
                List<String> docsAndFreqInLine = new ArrayList(Arrays.asList(line.split("~|\\*|,")));
                List<String> headlineOfDoc;
                String docNumber;

                //this loop run over all the docs the term is included and term's frequency
                for (int i = 1; i < docsAndFreqInLine.size(); i += 2) {
                    String docID = docsAndFreqInLine.get(i);
                    docNumber = Indexer.docsAndIDs.get(Integer.parseInt(docID));

                    //check if the doc is relevant for the cities that the user chose(if he chose)
                    if (listCitiesFromUser.size() > 0) {
                        if (!checkIfDocIsReleventToCities(docNumber))
                            continue;

                    }

                    int frequencyInDoc = Integer.parseInt(docsAndFreqInLine.get(i + 1));
                    int numOfDocuments = Indexer.termsCorpusMap.get(term).numOfDocuments;
                    int docSize = Indexer.docsCorpusMap.get(docNumber).numOfTerms;


                    //the BM25 equation
                    double firstPart = Math.log10(1 / ((numOfDocuments + 0.5) / (Indexer.docsCorpusMap.size() - numOfDocuments + 0.5)));
                    double secondPart = ((k + 1) * frequencyInDoc) / (frequencyInDoc + k * ((1 - b) + b * (docSize / averageDocsLength)));
                    double scoreQueryAndDoc = firstPart * secondPart;


                    //for words that are on the headline of the doc
                    headlineOfDoc = Indexer.docsCorpusMap.get(docNumber).headline;
                    if(headlineOfDoc != null)
                    {
                        if (headlineOfDoc.contains(termLowerCase) || headlineOfDoc.contains(termUpperCase))
                            scoreQueryAndDoc = 1.05 * scoreQueryAndDoc;
                    }

                    //if the document already was ranked before, take its previous rank and add it to the new rank
                    double prevRank = 0.0;
                    if (rankedDocumentsMap.containsKey(docNumber))
                        prevRank = rankedDocumentsMap.get(docNumber);

                    rankedDocumentsMap.put(docNumber, prevRank + scoreQueryAndDoc);

                }

            }

        }
    }

    /**
     * gets a document number, and return a boolean that says whether if the document contains
     * that city name in its text or in its FP=104 tag
     */
    private boolean checkIfDocIsReleventToCities(String docNumber)
    {

        //checking if one of the cities in the doc's FP=104 tag
        if(listCitiesFromUser.contains(Indexer.docsCorpusMap.get(docNumber).city))
            return true;

        //chcking if the doc contains one of the cities in it's text
        for(int i = 0; i < listCitiesFromUser.size(); i++)
        {
            if(Indexer.citiesInCorpus.get(listCitiesFromUser.get(i)) == null)
                continue;

            if(Indexer.citiesInCorpus.get(listCitiesFromUser.get(i)).placementsInDocs.containsKey(docNumber))
                return true;
        }

        return false;
    }

    /**
     * calculates the average length of document in the given corpus
     */
    private Double clacAverageLength(Map<String,DocTermDataInMap> docsCorpusMap)
    {
        Double sumOfLnegths = 0.0;
        for (String docNumber : docsCorpusMap.keySet())
        {
            sumOfLnegths += docsCorpusMap.get(docNumber).numOfTerms;
        }
        return sumOfLnegths/docsCorpusMap.size();
    }


}
