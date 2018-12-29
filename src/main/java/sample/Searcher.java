package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

import java.io.*;
import java.util.*;

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
            queryIDToRankedMap.put(queryList.get(i).queryID, rankedDocumentsMap);

        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Finished Searching");
        alert.showAndWait();
    }

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
