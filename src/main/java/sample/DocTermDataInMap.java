package sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * this class represents a document data object that is saved in docsInCorpus map
 */
public class DocTermDataInMap implements Serializable {

    private static final long serialVersionUID = 1L;
    public int max_tf;
    public int numOfTerms;
    public String city;
    public Map<String,Double>  entitiesGardes;

    public DocTermDataInMap(int max_tf, int numOfTerms, String city, Map<String,Double> entitiesGardes) {
        this.max_tf = max_tf;
        this.numOfTerms = numOfTerms;
        this.city = city;
        this.entitiesGardes = entitiesGardes;
    }

}
