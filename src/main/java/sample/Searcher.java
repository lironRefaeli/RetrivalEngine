package sample;
import javafx.scene.control.Alert;
import java.io.*;
import java.util.*;

/**
 * This class responsible for getting a query, find its most 50 ranked documents, show these docs to the user
 * and save them on disk
 */
public class Searcher {

    Ranker ranker;
    Parse parser;
    boolean withSemantic;
    boolean withStemmer;
    Map<String,Map<String,Double>> queryIDToRankedMap;

    public Searcher(Ranker ranker, Parse parser, boolean withSemantic, boolean withStemmer)
    {
       this.ranker = ranker;
       this.parser = parser;
       this.withSemantic = withSemantic;
       this.withStemmer = withStemmer;
    }

    /**
     * This method loops over all the queries the ReadFile have found
     * every query is parsed (also the query description) on the Parse class
     * every query is sent to the Ranker class that returns the most 50 ranked documents for that query search
     * @param queryList a list of Query object
     */
    public void handleQuery(List<Query> queryList) {
        queryIDToRankedMap = new TreeMap<>();
        for(int i = 0; i < queryList.size(); i++)
        {
            Map<String, Integer> queryMap = parser.ParsingQuery(queryList.get(i).title);
            Map<String, Integer> descriptionMap;
            Map<String,Double> rankedDocumentsMap;
            if(queryList.get(i).description != null)
            {
                descriptionMap = parser.ParsingQuery(queryList.get(i).description);
                rankedDocumentsMap = ranker.RankDocumentsByQuery(queryMap, descriptionMap);
            }
            else
            {
                rankedDocumentsMap = ranker.RankDocumentsByQuery(queryMap, null);
            }
            if(rankedDocumentsMap.size() > 0)
                queryIDToRankedMap.put(queryList.get(i).queryID, rankedDocumentsMap);

        }
        if(queryIDToRankedMap.size() > 0)
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Finished Searching");
            alert.showAndWait();
            return;
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No results were found");
            alert.showAndWait();
            return;
        }

    }

    /**
     * saves the result file on the disk
     * @param folderPath gets the folder that the file will be saved in
     */
    public void saveResultFile(String folderPath) throws IOException, NullPointerException {
        File file;
        if(withSemantic && withStemmer)
            file = new File(folderPath + "\\ResultFileWithSemanticAndStemming.txt");
        else if(withSemantic)
             file = new File(folderPath + "\\ResultFileWithSemantic.txt");
        else if(withStemmer)
            file = new File(folderPath + "\\ResultFileWithStemming.txt");
        else
            file = new File(folderPath + "\\ResultFileWithoutSemanticAndStemming.txt");

        BufferedWriter writer;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF-8"),262144);

        if(queryIDToRankedMap == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setTitle("Information Dialog");
            alert.setHeaderText("Please search before saving the Result File" );
            //alert.setContentText("s");
            alert.showAndWait();
            return;
        }

        else
        {
            for(String queryId : queryIDToRankedMap.keySet())
            {
                for(String docNumber : queryIDToRankedMap.get(queryId).keySet())
                {
                    WriteResultToFile(docNumber, queryId, writer);
                }
            }
            writer.close();
            queryIDToRankedMap = null;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("The results file has been saved in the specified folder" );
            alert.showAndWait();
        }

    }

    /**
     * The format of a result file
     */
    private void WriteResultToFile(String docNumber, String queryId, BufferedWriter writer)
    {
        String toPrint = queryId + " 0 " + docNumber + " 1 42.38 mt";
        try
        {
            writer.write(toPrint);
            writer.newLine();
        }
        catch (IOException e) { e.printStackTrace();}
    }


}
