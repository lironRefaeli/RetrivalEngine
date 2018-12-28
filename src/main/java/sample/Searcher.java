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
    Map<String,Map<String,Double>> queryIDToRankedMap;

    public Searcher(Ranker ranker, Parse parser, boolean semanticSelection)
    {
       this.ranker = ranker;
       this.parser = parser;
       this.withSemantic = semanticSelection;


    }

    public void handleQuery(List<Query> queryList) throws IOException {
        queryIDToRankedMap = new TreeMap<>();
        for(int i = 0; i < queryList.size(); i++)
        {
            String titleAndDescription = queryList.get(i).title + " " + queryList.get(i).description;
            Map<String, Integer> queryMap = parser.ParsingQuery(queryList.get(i).title);
            Map<String,Double> rankedDocumentsMap = ranker.RankDocumentsByQuery(queryMap);
            queryIDToRankedMap.put(queryList.get(i).queryID, rankedDocumentsMap);

        }

        System.out.println("Finished Searching");

    }

    public void saveResultFile(String folderPath) throws IOException {
        File file;
        if(withSemantic)
            file = new File(folderPath + "\\ResultFileWithSemantic.txt");
        else
            file = new File(folderPath + "\\ResultFileWithoutSemantic.txt");

        BufferedWriter writer;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF-8"),262144);

        if(queryIDToRankedMap == null)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setTitle("Information Dialog");
            alert.setHeaderText("Please search before saving the Result File" );
            //alert.setContentText("s");
            alert.showAndWait();
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
            System.out.println("Finished writing the result file");
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
