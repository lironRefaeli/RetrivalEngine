package sample;

public class TermDataInMap {

    public int numOfDocuments;
    private int pointerToPostingLine;
    private double idf;
    public int totalTf;


    public TermDataInMap(Integer totalTf, Integer numOfDocuments) {
        this.totalTf = totalTf;
        this.numOfDocuments = numOfDocuments;
    }
}
