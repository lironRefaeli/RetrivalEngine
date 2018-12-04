package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class mainController {

    ObservableList<String> languagesBoxOptions = FXCollections.observableArrayList("English","French","Vietnamese","Rusian","Albanian","Polish","Latvian","Lithuanian","Tamil",
            "Indonesian","Arabic","Kirundi","German","Tigrigna","Slovenian","Malay","Cambodia","Spanish","Norwegian","Bengali","Japanse", "Amharic", "Ukrainian", "Czech", "Macedonian", "Chinese", "Italian", "Slovene",
            "Swedish", "Korean", "Danish", "Hungarian" , "Afrikaans", "Turkish", "Kazakh", "Georgian", "Hindi", "Bulgarian", "Hebrew", "Kinyarwanda", "Thai", "International", "Cambodian", "Tagalog"
            ,"Burmese","Urdu", "Spansih", "Swahili", "Belarusian","Persian","Slovak","Malagasy", "Azeri", "Cantonese" , "Portuguese", "Greek", "Russian", "Parentheses" );

    @FXML
    private TextField corpusPath;
    @FXML
    private TextField diskPath;
    @FXML
    private CheckBox stemmerCheckBox;
    @FXML
    private ChoiceBox languagesBox;

    @FXML
    private void initChoiceBox(){

        languagesBox.setItems(languagesBoxOptions);
        languagesBox.setValue("English");
    }

    public void StartEngine(ActionEvent actionEvent) throws IOException {

        //String pathToCorpus = "C:\\Users\\david\\Desktop\\Tests\\corpusTest";
        //String pathToDisk = "C:\\Users\\david\\Desktop\\Tests\\postingFiles";

        //todo decide where to put cities and junk words files
        String pathToCitiesAndInformationFile = "C:\\Users\\david\\Desktop\\Tests\\JunkWordsAndStopWords\\CitiesAndInformationFile";
        String wordToDelete = "C:\\Users\\david\\Desktop\\Tests\\JunkWordsAndStopWords\\WordsToDelete.txt";

        //extracting corpusPath from the UI
        String pathToCorpus = corpusPath.getText();

        //extracting steemer selection from the UI
        boolean stemmerSelection = stemmerCheckBox.isSelected();

        //extracting disk path from the UI and changing it according to stemmer checkbox selection
        String pathToDisk = diskPath.getText();
        if(stemmerSelection)
            pathToDisk += "\\withStemming";
        else
            pathToDisk += "\\withoutStemming";

        File file = new File(pathToDisk);
        file.mkdir();
        ReadFile readFile = new ReadFile(pathToCorpus);
        Parse parser = new Parse(pathToCorpus + "\\stop_words.txt", pathToCitiesAndInformationFile, wordToDelete , stemmerSelection);
        final Indexer indexer = new Indexer(readFile, parser, pathToDisk);
        Thread indexerThread = new Thread() {
            public void run() {
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

    public void BrowseCorpusPath(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        //chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Corpus' Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            corpusPath.setText("" + chooser.getSelectedFile());
            /*
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText("Ooops, yoi didn't chose a corpus path");

            alert.showAndWait();
        }
        */

    }


    public void browseDiskPath(ActionEvent event) {

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose Disk's Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            diskPath.setText("" + chooser.getSelectedFile());
    }

    public void Restart(ActionEvent event) {

        //todo delete dictionary file
        //todo null in paramrtets on memory
        boolean stemmerSelection = stemmerCheckBox.isSelected();

        String pathToDisk = diskPath.getText();

        if(stemmerSelection)
            pathToDisk += "\\withStemming";
        else
            pathToDisk += "\\withoutStemming";

        File postingFilesDirectory = new File(pathToDisk);
        for(File file: postingFilesDirectory.listFiles())
            if (!file.isDirectory())
                file.delete();
    }

    public void ShowDictionary(ActionEvent event) {
    }

    public void LoadDictionary(ActionEvent event) {


    }

}
