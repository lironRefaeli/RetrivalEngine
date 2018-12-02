
package sample;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class JSON_reader
{
    public String connectionToApi(String cityName) throws JSONException, IOException {

        JSONObject json = readJsonFromUrl("https://restcountries.eu/rest/v2/capital/" + cityName);
        if (json != null)
        {
            String currencies = json.get("currencies").toString();
            JSONObject jsonCurrencies = new JSONObject(currencies.substring(1,currencies.length()-1));
            String data = json.get("name").toString() + "," + jsonCurrencies.get("code").toString() + "," + json.get("population").toString();
            return data;
        }
        return null;

/*
        CityInMap cityInfo = new CityInMap(json.get("name").toString(), jsonCurrencies.get("code").toString(), json.get("population").toString());
        return cityInfo;
*/
    }

    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is;
        try { is = new URL(url).openStream();}
        catch (FileNotFoundException e)
        {
            return null;
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            jsonText = jsonText.substring(1,jsonText.length()-1);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private String readAll(Reader rd) throws IOException {

        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}

