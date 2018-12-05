package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;

//todo write in readme
//https://stackoverflow.com/questions/29888592/errorjava-javactask-source-release-8-requires-target-release-1-8
public class mainController {

    ReadFile readFile;
    Parse parser;
    Indexer indexer;
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


        if(Indexer.termsCorpusMap == null) {
            //String pathToCorpus = "C:\\Users\\david\\Desktop\\Tests\\corpusTest";
            //String pathToDisk = "C:\\Users\\david\\Desktop\\Tests\\postingFiles";

            //extracting corpusPath from the UI
            String pathToCorpus = corpusPath.getText();

            //extracting steemer selection from the UI
            boolean stemmerSelection = stemmerCheckBox.isSelected();

            //extracting disk path from the UI and changing it according to stemmer checkbox selection
            String pathToDisk = diskPath.getText();
            //todo delete creatinf folder
            readFile = new ReadFile(pathToCorpus);
            parser = new Parse(pathToCorpus + "\\stop_words.txt", stemmerSelection);
            indexer = new Indexer(readFile, parser, pathToDisk);
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
        }

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


        String pathToDisk = diskPath.getText();
        String postingFolderPath;
        String dictionaryFilePath;
        boolean stemmerSelection = stemmerCheckBox.isSelected();

        if(stemmerSelection) {
            postingFolderPath = pathToDisk + "\\withStemming";
            dictionaryFilePath = pathToDisk + "\\dictionaryWithStemming";
        }
        else {
            postingFolderPath = pathToDisk + "\\withoutStemming";
            dictionaryFilePath = pathToDisk + "\\dictionaryWithoutStemming";

        }
        //delete dictionary file
        File dictionaryFile = new File(dictionaryFilePath);
        dictionaryFile.delete();

        //delete posting files
        File postingFilesDirectory = new File(postingFolderPath);
        if(postingFilesDirectory.listFiles() != null) {
            for (File file : postingFilesDirectory.listFiles())
                if (!file.isDirectory())
                    file.delete();
        }

        postingFilesDirectory.delete();

        //null in parameters on memory
        readFile = null;
        parser = null;
        indexer = null;
        corpusPath.setText("");
        diskPath.setText("");
        System.gc(); // calling the garbage collector


        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle("Information Dialog");
        alert.setHeaderText("Restart the engine was succeeded");
        //alert.setContentText("s");
        alert.showAndWait();
    }

    public void ShowDictionary(ActionEvent event) {

        if(Indexer.termsCorpusMap != null)
        {
            TreeMap<String, TermDataInMap> sortingTermsInCorpus = new TreeMap<>();
            sortingTermsInCorpus.putAll(Indexer.termsCorpusMap);

            Scene scene = new Scene(new Group());
            Stage stage = new Stage();
            stage.setTitle("Table View Sample");
            stage.setWidth(300);
            stage.setHeight(500);

            final Label label = new Label("Display Dictionary");
            //label.setFont(new Font("Arial", 20));

            TableView table = new TableView();
            table.setEditable(true);

            TableColumn firstNameCol = new TableColumn("Term");
            TableColumn lastNameCol = new TableColumn("Frequency In Corpus");

            table.getColumns().addAll(firstNameCol, lastNameCol);

            final VBox vbox = new VBox();
            vbox.setSpacing(5);
            //vbox.setPadding(new Insets(10, 0, 0, 10));
            vbox.getChildren().addAll(label, table);

            ((Group) scene.getRoot()).getChildren().addAll(vbox);

            stage.setScene(scene);
            stage.show();


        }
    }

    public void LoadDictionaryFromDisk(ActionEvent event)  {


        boolean stemmerSelection = stemmerCheckBox.isSelected();
        String pathToDisk = diskPath.getText();
        File dictionaryFile;

        if (stemmerSelection)
            dictionaryFile = new File(pathToDisk + "\\dictionaryWithStemming");
        else
            dictionaryFile = new File(pathToDisk + "\\dictionaryWithoutStemming");


        FileInputStream fileStreamer = null;
        try {
            fileStreamer = new FileInputStream(dictionaryFile);
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setTitle("Information Dialog");
            alert.setHeaderText("The dictionary file wasn't found :(");
            //alert.setContentText("s");
            alert.showAndWait();
            return;
        }
        ObjectInputStream objectStreamer = null;
        try {
            objectStreamer = new ObjectInputStream(fileStreamer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read objects
        try {
            Indexer.termsCorpusMap = (Map<String, TermDataInMap>) objectStreamer.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            objectStreamer.close();
            fileStreamer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //alert.setTitle("Information Dialog");
        alert.setHeaderText("Loading the dictionary was succeeded!");
        //alert.setContentText("s");
        alert.showAndWait();






    }

}