package sample;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Ranker {

    boolean withStemmer;
    boolean withSemantic = true;
    String pathToDisk;
    Double averageDocsLength;
    double k;
    double b;
    public static List<String> listCitiesFromUser = new ArrayList<>();


    public Ranker(boolean stemmerSelection, String pathToDisk)
    {
        withStemmer = stemmerSelection;
        this.pathToDisk = pathToDisk;
        averageDocsLength = clacAverageLength(Indexer.docsCorpusMap);
        k = 1.2;
        b = 0.75;
    }


    public Map<String,Double> RankDocumentsByQuery(Map<String,Integer> queryMap) throws IOException {
        Map<String, Double> rankedDocumentsMap = new HashMap<>();
        List<String> wordsQueryList = new ArrayList<>();

        if(withSemantic)
        {
            for (String term : queryMap.keySet())
            {
                wordsQueryList.addAll(JSON_reader.connectionToSynApi(term));
            }
        }
        else
        {
            for (String term : queryMap.keySet())
            {
                wordsQueryList.add(term);
            }
        }

        Double termIDF;
        int pointerToPostingLine;
        for (int j = 0; j < wordsQueryList.size(); j++)
        {
            String term = wordsQueryList.get(j);
            String termLowerCase = term.toLowerCase();
            String termUpperCase = term.toUpperCase();
            if(Indexer.termsCorpusMap.containsKey(termLowerCase))
                term = termLowerCase;
            else if(Indexer.termsCorpusMap.containsKey(termUpperCase))
                term = termUpperCase;
            else
                continue;


           termIDF = Indexer.termsCorpusMap.get(term).idf;
           pointerToPostingLine = Indexer.termsCorpusMap.get(term).pointerToPostingLine;
           File postingFile;
           if (withStemmer)
                postingFile = new File(pathToDisk + "\\withStemming\\" + term.charAt(0) + ".txt");
           else
                postingFile = new File(pathToDisk + "\\withoutStemming\\" + term.charAt(0) + ".txt");

           if(postingFile != null)
           {
               String line;
               try (BufferedReader br = new BufferedReader(new FileReader(postingFile))) {
                   for (int i = 1; i < pointerToPostingLine; i++)
                       br.readLine();
                   line = br.readLine();
               }

               List<String>  docsAndFreqInLine = new ArrayList(Arrays.asList(line.split("~|\\*|,")));
               Set<String> headlineOfDoc;
               String docNumber;
               for(int i = 1; i < docsAndFreqInLine.size(); i+=2)
               {
                    String docID = docsAndFreqInLine.get(i);
                    docNumber = Indexer.docsAndIDs.get(Integer.parseInt(docID));

                    if(listCitiesFromUser.size() != 0 && !checkIfDocIsReleventToCities(docNumber))
                        continue;

                    int frequencyInDoc = Integer.parseInt(docsAndFreqInLine.get(i+1));
                    int df = Indexer.termsCorpusMap.get(term).numOfDocuments;
                    int docSize = Indexer.docsCorpusMap.get(docNumber).numOfTerms;

                    double firstPart = Math.log10(1/((df + 0.5) / (Indexer.docsCorpusMap.size()-df + 0.5)));
                    double secondPart = ((k + 1) * frequencyInDoc) / (frequencyInDoc + k * ((1 - b) + b * (docSize/averageDocsLength)));
                    double scoreQueryAndDoc = firstPart * secondPart;


                    /*
                    //for words that are on the headline of the doc
                    headlineOfDoc = Indexer.docsCorpusMap.get(docNumber).headline;
                    if(headlineOfDoc.contains(termLowerCase) || headlineOfDoc.contains(termUpperCase))
                        scoreQueryAndDoc = 1.2 * scoreQueryAndDoc;
                    */

                    double prevRank = 0.0;
                    if(rankedDocumentsMap.containsKey(docNumber))
                       prevRank = rankedDocumentsMap.get(docNumber);

                    rankedDocumentsMap.put(docNumber, prevRank + scoreQueryAndDoc);

               }

           }

        }
            Map<String, Double> sortedRankedDocumentMap = new LinkedHashMap<>();
            List<Map.Entry<String, Double>> list = new ArrayList<>(rankedDocumentsMap.entrySet());
            list.sort(Map.Entry.comparingByValue());

            if(rankedDocumentsMap.size() < 50)
            {
                for(int i = list.size()-1; i >= 0; i--)
                    sortedRankedDocumentMap.put(list.get(i).getKey(), list.get(i).getValue());
            }
            else
            {
                for(int i = list.size()-50; i < list.size(); i++)
                    sortedRankedDocumentMap.put(list.get(i).getKey(), list.get(i).getValue());
            }

        return sortedRankedDocumentMap;
    }

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
