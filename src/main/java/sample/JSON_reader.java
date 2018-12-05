
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
import java.util.HashMap;
import java.util.Map;

public class JSON_reader {
    public void connectionToApi() throws JSONException {
        OkHttpClient myClient = new OkHttpClient();
        String url = ("https://restcountries.eu/rest/v2/all?fielsss=capital;name;population;currency");

        Request request = new Request.Builder().url(url).build();
        Response response = null;
        org.json.simple.parser.JSONParser json = new org.json.simple.parser.JSONParser();
        try {

            response = myClient.newCall(request).execute();


        } catch (IOException e) {
            e.printStackTrace();
        }

        Object object = null;
        try {
            try {
                object = json.parse(response.body().string());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (object != null) {
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
                String pop = divideNumbers(Double.parseDouble(population));
                Indexer.citiesInAPI.put(capital.toUpperCase(), new CityInMap(country, coin, pop));
                System.out.println("hi");

            }
        }


    }


    private String divideNumbers(Double pop) {
        if (pop >= 1000000000) {
            pop = pop / 1000000000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "B";
        } else if (pop >= 1000000) {
            pop = pop / 1000000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "M";
        } else if (pop >= 1000) {
            pop = pop / 1000;
            pop = (double) Math.round(pop * 100);
            pop = pop / 100;
            return Double.toString(pop) + "K";
        } else {
            return Double.toString(pop);
        }
    }
}

