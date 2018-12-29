
package sample;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
//import org.json.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handels with JSON files and connecting to API in order to bring data about capital cities
 */
public class JSON_reader {



    public static List<String> connectionToSynApi(String term)
    {
        List<String> synWordsResults = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();
        String url = ("https://api.datamuse.com/words?rel_syn=" + term);
        //String url = ("https://api.datamuse.com/words?ml=" + term);
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        org.json.simple.parser.JSONParser json = new org.json.simple.parser.JSONParser();
        try {
            response = httpClient.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            try {
                //the object includes data from json object after parsering it
                object = json.parse(response.body().string());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object != null) {
            String synWord = "";
            Object[] parsed_json = ((JSONArray) object).toArray();
            int counter = 0;
            for (Object O : parsed_json) {
                if(counter < 3) {
                    synWord = (String) ((JSONObject) O).get("word");
                    synWordsResults.add(synWord);
                    counter++;
                }
                else
                    break;
            }
        }
        return synWordsResults;
        }

    public static List<String> connectionToMLApi(String term)
    {
        List<String> synWordsResults = new ArrayList<>();
        OkHttpClient httpClient = new OkHttpClient();
        //String url = ("https://api.datamuse.com/words?rel_syn=" + term);
        String url = ("https://api.datamuse.com/words?ml=" + term);
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        org.json.simple.parser.JSONParser json = new org.json.simple.parser.JSONParser();
        try {
            response = httpClient.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            try {
                //the object includes data from json object after parsering it
                object = json.parse(response.body().string());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object != null) {
            String synWord = "";
            Object[] parsed_json = ((JSONArray) object).toArray();
            int counter = 0;
            for (Object O : parsed_json) {
                if(counter < 3)
                {
                    synWord = (String) ((JSONObject) O).get("word");
                    synWordsResults.add(synWord);
                    counter++;
                }
                else
                    break;
            }
        }
        return synWordsResults;
    }


    public void connectionToCitiesApi() throws JSONException {

        OkHttpClient httpClient = new OkHttpClient();
        //the URL contains API data about capital cities
        String url = ("https://restcountries.eu/rest/v2/all?fielsss=capital;name;population;currency");
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        org.json.simple.parser.JSONParser json = new org.json.simple.parser.JSONParser();
        try {
            response = httpClient.newCall(request).execute();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Object object = null;
        try {
            try {
                //the object includes data from json object after parsering it
                object = json.parse(response.body().string());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object != null) {

            //extracting the specific data from the json object
            String capital = "", country = "", coin = "";
            String population = "";
            Object[] parsed_json = ((JSONArray) object).toArray();
            for (Object O : parsed_json) {
                capital = (String) ((JSONObject) O).get("capital");
                country = (String) ((JSONObject) O).get("name");
                JSONArray theArray = (JSONArray) (((JSONObject) O).get("currencies"));
                population = ((JSONObject) O).get("population").toString();

                for (Object obj : theArray) {
                    coin = (String) ((JSONObject) obj).get("code");
                }
                //handling with the numbers of the population parameter
                population = ParsePopulationNumber(Double.parseDouble(population));
                Indexer.citiesInAPI.put(capital.toUpperCase(), new CityInMap(country, coin, population));

            }
        }
    }
        //the function is getting population's size and convert it
        private String ParsePopulationNumber(Double population){
            if (population >= 1000000000) {
                population = population / 1000000000;
                population = (double) Math.round(population * 100);
                population = population / 100;
                return Double.toString(population) + "B";
            } else if (population >= 1000000) {
                population = population / 1000000;
                population = (double) Math.round(population * 100);
                population = population / 100;
                return Double.toString(population) + "M";
            } else if (population >= 1000) {
                population = population / 1000;
                population = (double) Math.round(population * 100);
                population = population / 100;
                return Double.toString(population) + "K";
            } else {
                return Double.toString(population);
            }

        }
    }

