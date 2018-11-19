package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


    public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {

        int numOfLoops = 91; //number of loops in order to go through all the corpus' folders
        Map<String, TermDataInMap> termsCorpusMap = new HashMap<String, TermDataInMap>(); //a map that includes all the terms in the corpus
        Map<String, DocTermDataInMap> docsCorpusMap = new HashMap<String, DocTermDataInMap>();//a map that includes all the terms in the corpus

        //lists that will save documents content: texts, docsNumbers, cities.
        List<String> listOfTexts = new ArrayList<String>();
        List<String> listOfDocsNumbers = new ArrayList<String>();
        List<String> listOfDocsCities = new ArrayList<String>();

        //object of the class readFile - the input is the corpus location path
        ReadFile readFile = new ReadFile("C:\\Users\\refaeli.liron\\Desktop\\corpus");
        //object of the class readFile - the input is the stopWords documents location path
        Parse parse = new Parse("C:\\Users\\refaeli.liron\\Desktop\\corpus\\StopWords.txt");

        //the number of loop is determined by the numOfLoops parameter
        for (int i = 0; i < numOfLoops; i++) {

            //the lists size is determined by the numOfFolsersToReadFrom parameter
            listOfTexts = readFile.ReadFolder(20);
            listOfDocsNumbers = readFile.getDocNumbersList();
            listOfDocsCities = readFile.getDocCitiesList();

            //loops over every text from one chunk
            for (int j = 0; j < listOfTexts.size(); j++) {

                //for every text we will build temporaryMap in order to save all the terms and their frequency (tf) by Parse object
                Map<String, Integer> temporaryMap = parse.ParsingDocument(listOfTexts.get(j));

                //after parsing the text, we will creating new record in the docs Map
                docsCorpusMap.put(listOfDocsNumbers.get(j), new DocTermDataInMap(returnMaxTf(temporaryMap), temporaryMap.size(), listOfDocsCities.get(j)));

                //loops over one text's terms and merging temporaryMap to termsCorpusMap
                for (String term : temporaryMap.keySet()) {
                    //NBA or GSW
                    if (Parse.IsUpperCase(term)) {
                        if (termsCorpusMap.containsKey(term)) {
                            termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                            termsCorpusMap.get(term).numOfDocuments++;
                        } else
                            termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                    }
                    //liron or first
                    else {
                        //case term is "first" and we already have "FIRST" on the map
                        //save the frequency of "FIRST", remove it from map and add frequency + 1 to "first"
                        if (termsCorpusMap.containsKey(term.toUpperCase())) {
                            TermDataInMap upperCaseTerm = termsCorpusMap.get(term.toUpperCase());
                            termsCorpusMap.remove(term.toUpperCase());
                            termsCorpusMap.put(term, new TermDataInMap(upperCaseTerm.totalTf + temporaryMap.get(term), upperCaseTerm.numOfDocuments + 1));
                        }
                        //we do not have "FIRST" in our map
                        else {
                            //we have "first" in our map
                            if (termsCorpusMap.containsKey(term)) {
                                termsCorpusMap.get(term).totalTf += temporaryMap.get(term);
                                termsCorpusMap.get(term).numOfDocuments++;
                            }
                            //adds "first" to the map
                            else
                                termsCorpusMap.put(term, new TermDataInMap(temporaryMap.get(term), 1));
                        }

                    }
                }
            }
            //launch(args);
        }


    }

    /**
     * the function is calculating the maximum term's frequency (max_tf) in a text
     * @param temporaryMap
     * @return
     */
    static public int returnMaxTf(Map<String, Integer> temporaryMap)
    {
        int maxTf = -1;
        for(int value: temporaryMap.values())
        {
            if(maxTf < value)
                maxTf = value;
        }
        return maxTf;
    }
}
