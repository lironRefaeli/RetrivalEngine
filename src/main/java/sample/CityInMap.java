package sample;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class represents a city data object that is saved in citiesInCorpus map
 */
public class CityInMap implements Serializable {

    private static final long serialVersionUID = 1L;
    public String countryName;
    public String coinName;
    public String sizeOfPopulation;
    public Map<String, List<Integer>> placementsInDocs;

    public CityInMap(String countryName, String coinName, String sizeOfPopulation) {

        this.countryName = countryName;
        this.coinName = coinName;
        this.sizeOfPopulation = sizeOfPopulation;
        placementsInDocs = new HashMap<>();
    }
}
