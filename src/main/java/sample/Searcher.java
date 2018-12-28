package sample;

import java.io.*;
import java.util.*;

public class Searcher {

    Ranker ranker;
    Parse parser;
    boolean withSemantic;

    public Searcher(Ranker ranker, Parse parser, boolean semanticSelection)
    {
       this.ranker = ranker;
       this.parser = parser;
       this.withSemantic = semanticSelection;
    }

    public void handleQuery(List<Query> queryList) throws IOException {

        File file;
        if(withSemantic)
            file = new File(ranker.pathToDisk + "\\ResultFileWithSemantic.txt");
        else
            file = new File(ranker.pathToDisk + "\\ResultFileWithoutSemantic.txt");

        BufferedWriter writer;
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()),"UTF-8"),262144);
        for(int i = 0; i < queryList.size(); i++)
        {
            String titleAndDescription = queryList.get(i).title + " " + queryList.get(i).description;
            Map<String, Integer> queryMap = parser.ParsingQuery(queryList.get(i).title);
            Map<String,Double> rankedDocumentsMap = ranker.RankDocumentsByQuery(queryMap);
            for(String docNumber : rankedDocumentsMap.keySet())
            {
                WriteResultToFile(docNumber, queryList.get(i), writer);
            }
        }
        writer.close();
        System.out.println("Finished writing the result file");
    }

    private void WriteResultToFile(String docNumber, Query currQuery, BufferedWriter writer)
    {
        String toPrint = currQuery.queryID + " 0 " + docNumber + " 1 42.38 mt";
        try
        {
            writer.write(toPrint);
            writer.newLine();
        }
        catch (IOException e) { e.printStackTrace();}
    }


}
