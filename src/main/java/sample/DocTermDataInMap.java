package sample;

/**
 * this class represents a document data object that is saved in docsInCorpus map
 */
public class DocTermDataInMap {

    public int max_tf;
    private int numOfTerms;
    private double idf;
    public String city;

    public DocTermDataInMap(int max_tf, int numOfTerms, String city) {
        this.max_tf = max_tf;
        this.numOfTerms = numOfTerms;
        this.city = city;
    }

}
