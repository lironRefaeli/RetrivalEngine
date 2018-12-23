package sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * this class represents a document data object that is saved in docsInCorpus map
 */
public class DocTermDataInMap implements Serializable {

    private static final long serialVersionUID = 1L;
    public int max_tf;
    public int numOfTerms;
    public String city;
    public Set<String> headline;
    public Map<String,Double>  entitiesGardes;

    public DocTermDataInMap(int max_tf, Set<String> headline, int numOfTerms, String city, Map<String,Double> entitiesGardes) {
        this.max_tf = max_tf;
        this.headline = headline;
        this.numOfTerms = numOfTerms;
        this.city = city;
        this.entitiesGardes = entitiesGardes;
    }

}
