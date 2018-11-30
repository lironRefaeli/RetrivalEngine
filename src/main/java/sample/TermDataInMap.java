package sample;

public class TermDataInMap {

    public int numOfDocuments;
    public int pointerToPostingLine;
    public double idf;
    public int totalTf;


    public TermDataInMap(Integer totalTf, Integer numOfDocuments) {
        this.totalTf = totalTf;
        this.numOfDocuments = numOfDocuments;
    }
}
