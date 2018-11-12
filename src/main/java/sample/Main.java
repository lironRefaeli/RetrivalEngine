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
import java.util.List;

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
        List<String> listOfTexts = new ArrayList<String>();
        ReadFile readFile = new ReadFile("C:\\Users\\david\\Desktop\\שנה ד\\סמסטר א\\אחזור\\מנוע\\corpus");
        for(int i = 0; i < numOfLoops; i++)
        {
            listOfTexts = readFile.ReadFolder(20);
            if (i == 90)
             System.out.println(i);
        }


        //launch(args);
    }
}
