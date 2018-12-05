package sample;

import java.io.Serializable;

/**
 * this class represents a term data object that is saved in termsInCorpus map
 * it's also serializable beacuse this object is saved to file as an object when we are saving termsInCorpus to dictiony file
 */
public class TermDataInMap implements Serializable {

    private static final long serialVersionUID = 1L;
    public String term;
    public int numOfDocuments;
    public int pointerToPostingLine;
    public double idf;
    public int totalTf;

    public TermDataInMap(String term, Integer totalTf, Integer numOfDocuments) {
        this.term = term;
        this.totalTf = totalTf;
        this.numOfDocuments = numOfDocuments;
    }

    public int getTotalTf()
    {
        return totalTf;
    }

    public int getnumOfDocuments()
    {
        return numOfDocuments;
    }

    public String getTerm()
    {
        return term;
    }
}
