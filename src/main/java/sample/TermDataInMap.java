package sample;

import java.io.Serializable;

public class TermDataInMap implements Serializable {

    public int numOfDocuments;
    public int pointerToPostingLine;
    public double idf;
    public int totalTf;
    public String placementsInCorpus;


    public TermDataInMap(Integer totalTf, Integer numOfDocuments) {
        this.totalTf = totalTf;
        this.numOfDocuments = numOfDocuments;
    }
}
