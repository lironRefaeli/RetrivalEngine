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
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {

        int numOfLoops = 91;
        Map<String,TermDataInMap> termsCorpusMap = new HashMap<String, TermDataInMap>();
        List<String> listOfTexts = new ArrayList<String>();

        ReadFile readFile = new ReadFile("C:\\Users\\david\\Desktop\\corpus");
        Parse parse = new Parse("C:\\Users\\david\\Desktop\\StopWords.txt");

        for(int i = 0; i < numOfLoops; i++)
        {
            listOfTexts = readFile.ReadFolder(20);
            for(int j = 0; j < listOfTexts.size(); j++)
            {
                Map<String,Integer> temporaryMap = parse.ParsingDocument(listOfTexts.get(j));
                for(String termKey :temporaryMap.keySet())
                {
                    if(termsCorpusMap.containsKey(termKey)) {
                        termsCorpusMap.get(termKey).numOfDocuments++;
                        termsCorpusMap.get(termKey).totalTf += temporaryMap.get(termKey);
                    }
                    else
                        termsCorpusMap.put(termKey, new TermDataInMap(temporaryMap.get(termKey)));
                }
            }
        }




        //launch(args);
    }
}
