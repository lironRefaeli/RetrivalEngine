package sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Searcher {

    Ranker ranker;
    Parse parser;

    public Searcher(Ranker ranker, Parse parser)
    {
       this.ranker = ranker;
       this.parser = parser;
    }

    public void handleQuery(List<Query> queryList) throws IOException {
        for(int i = 0; i < queryList.size(); i++)
        {
            Map<String, Integer> queryMap = parser.ParsingQuery(queryList.get(i).title);
            Map<String,Double> rankedDocumentsMap = ranker.RankDocumentsByQuery(queryMap);
        }
    }


}
