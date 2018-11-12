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

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {


        File f = new File("C:\\Users\\refaeli.liron\\Documents\\הנדסת מערכות מידע\\שנה ג\\סמסטר א\\אחזור\\עזרים\\corpus\\corpus\\FB396001\\FB396001");
        Document document = Jsoup.parse(new String(Files.readAllBytes(f.toPath())));
        Elements elements = document.getElementsByTag("DOC");
        for(Element element : elements)
        {
            String name = element.getElementsByTag("DOCNO").text();
            System.out.println(name);


        }

        //launch(args);
    }
}
