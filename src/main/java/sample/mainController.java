package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;

public class mainController {

    @FXML
    private TextField corpusPath;
    @FXML
    private TextField diskPath;



    public void StartEngine(ActionEvent actionEvent) throws IOException {

        String pathToCorpus = "C:\\Users\\david\\Desktop\\Tests\\corpusTest";
        String pathToDisk = "C:\\Users\\david\\Desktop\\Tests\\postingFiles";
        String pathToCitiesAndInformationFile = pathToDisk + "\\CitiesAndInformationFile";
        //String pathToCorpus = corpusPath.getText();
        //String pathToDisk = diskPath.getText();

        ReadFile readFile = new ReadFile(pathToCorpus);
        Parse parser = new Parse(pathToCorpus + "\\stop_words.txt", pathToCitiesAndInformationFile);
        final Indexer indexer = new Indexer(readFile, parser, pathToDisk);
        Thread indexerThread = new Thread(){
            public void run()
            {
                try {
                    indexer.Play();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        indexerThread.start();


        //opens a new window for writing the query after the inverted index and all the other relevant stuff are ready
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root1 = fxmlLoader.load(getClass().getResource("/engineWindow.fxml").openStream());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("engineWindow");
            stage.setScene(new Scene(root1, 500, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
