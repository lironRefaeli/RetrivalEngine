package sample;

public class Searcher {

    Ranker ranker;
    Parse parser;

    public Searcher(Ranker ranker, Parse parser)
    {
       this.ranker = ranker;
       this.parser = parser;
    }

    public void handleQuery(String query)
    {

    }
}
