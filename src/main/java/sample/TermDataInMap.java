package sample;

import java.io.Serializable;

public class TermDataInMap implements Serializable {

    private static final long serialVersionUID = 1L;
    public int numOfDocuments;
    public int pointerToPostingLine;
    public double idf;
    public int totalTf;

    public TermDataInMap(Integer totalTf, Integer numOfDocuments) {
        this.totalTf = totalTf;
        this.numOfDocuments = numOfDocuments;
    }
}
