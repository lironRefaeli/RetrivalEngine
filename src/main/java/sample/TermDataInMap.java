package sample;

public class TermDataInMap {

    public int numOfDocuments;
    private int pointerToPostingLine;
    private double idf;
    public int totalTf;


    public TermDataInMap(Integer tfOfFirstDocument) {
        totalTf = tfOfFirstDocument;
        numOfDocuments = 1;
    }
}
