package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.*;
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


        /**
         * the function opening the searching window
         * if it's necessary to create inverted-index, readFile parser and index objects are created
         * @param actionEvent
         * @throws IOException
         */
        public void StartEngine(ActionEvent actionEvent) throws IOException {

            //checking if there is a dictionary in the system by checking termsInCorpus map
            if(Indexer.termsCorpusMap == null) {

                //extracting corpusPath from the UI
                String pathToCorpus = corpusPath.getText();

                //extracting stemmer selection from the UI
                boolean stemmerSelection = stemmerCheckBox.isSelected();

                //extracting disk path from the UI and changing it according to stemmer checkbox selection
                String pathToDisk = diskPath.getText();

                //if the user didn't insert one of the paths - display error alert
                if(pathToCorpus.equals("") || pathToDisk.equals(""))
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    //alert.setTitle("Information Dialog");
                    alert.setHeaderText("Please check that you fill all the fields");
                    //alert.setContentText("s");
                    alert.showAndWait();
                    return;
                }
                //starting creating the inverted-index
                try {
                    //creating readFile with the corpus path
                    readFile = new ReadFile(pathToCorpus);
                }
                //if the folder of the corpus wasn't found - diaplay alert
                catch(NullPointerException e)
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    //alert.setTitle("Information Dialog");
                    alert.setHeaderText("The corpus wasn't found. Please enter your corpus' path again");
                    //alert.setContentText("s");
                    alert.showAndWait();
                    return;
                }

                //creating parser with path to stop words file (it's place is in the corpus' folder) and the stemmer selction
                //in order to know if to use stemming
                parser = new Parse(pathToCorpus + "\\stop_words.txt", stemmerSelection);
                //creating index and sending to his constructor readfile object, parser object and disk path
                indexer = new Indexer(readFile, parser, pathToDisk);

                //starting thread that run the index's function play, which creates the inverted index
                Thread indexerThread = new Thread() {
                    public void run() {
                        try {
                            indexer.Play();
                        } catch (IOException e) {

                        }
                    }
                };

                 indexerThread.start();


                try {
                    indexerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //displaying statistics msg with the end of the creating of the inverted index
                if(!Indexer.hasException)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Just for you to know");
                    alert.setContentText("The number of documents that were indexed is: " + indexer.docsCorpusMap.size() +
                            ". The number of terms that were found is: " + indexer.termsCorpusMap.size() + ". We found " +
                            "this data in: " + (System.nanoTime() - indexer.startTime) / 1000000000.0 + " sec.");
                    alert.showAndWait();

                    //opens a new window for writing the query after the inverted index and all the other relevant stuff are ready
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    Parent root1 = fxmlLoader.load(getClass().getResource("/engineWindow.fxml").openStream());
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("engineWindow");
                    stage.setScene(new Scene(root1, 600, 500));
                    stage.show();
                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    //alert.setTitle("Information Dialog");
                    alert.setHeaderText("Oops! the posting files folder wasn't found. Please try again");
                    //alert.setContentText("s");
                    alert.showAndWait();
                }

            }//if index stopped in the middle of the process because of wrong disk path - display alert
            else
            {
                //opens a new window for writing the query after the inverted index and all the other relevant stuff are ready
                FXMLLoader fxmlLoader = new FXMLLoader();
                Parent root1 = fxmlLoader.load(getClass().getResource("/engineWindow.fxml").openStream());
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("engineWindow");
                stage.setScene(new Scene(root1, 600, 500));
                stage.show();
            }

        }


        /**
         * file choose for choosing corpus path
         * @param event
         */
        public void BrowseCorpusPath(ActionEvent event) {
            JFileChooser chooser = new JFileChooser();
            //chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Choose Corpus' Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                corpusPath.setText("" + chooser.getSelectedFile());


        }

        /**
         * file choose for choosing disk path
         * @param event
         */
        public void browseDiskPath(ActionEvent event) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("."));
            chooser.setDialogTitle("Choose Disk's Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                diskPath.setText("" + chooser.getSelectedFile());
        }

        /**
         * restarting the system
         * @param event
         */
        public void Reset(ActionEvent event) {


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
                alert.setHeaderText("Reset the engine was succeeded");
                //alert.setContentText("s");
                alert.showAndWait();
            }

        /**
         * displaying the dictionary by term and frequency columns
         * @param event
         * @throws IOException
         */
        public void displayDictionary(ActionEvent event) throws IOException {


                if (Indexer.termsCorpusMap != null) {


                    TableColumn<TermDataInMap, String> termColumn = new TableColumn<>("Term");
                    termColumn.setMinWidth(300);
                    termColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
                    TableColumn<TermDataInMap, Integer> freqCoulmn = new TableColumn<>("Frequency In Dictionary");
                    freqCoulmn.setMinWidth(300);
                    freqCoulmn.setCellValueFactory(new PropertyValueFactory<>("totalTf"));


                    TableView<TermDataInMap> table = new TableView<>();
                    table.setItems(getTermAndFrequency());
                    table.getColumns().addAll(termColumn, freqCoulmn);

                    VBox vbox = new VBox();

                    vbox.getChildren().addAll(table);

                    FXMLLoader fxmlLoader = new FXMLLoader();
                    Parent root1 = fxmlLoader.load(getClass().getResource("/dictionaryWindow.fxml").openStream());
                    Stage stage = new Stage();
                    //stage.initModality(Modality.APPLICATION_MODAL);
                    //stage.setTitle("displayDictionaryWindow");
                    stage.setScene(new Scene(vbox));

                    stage.show();

                }
                else
                {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    //alert.setTitle("Information Dialog");
                    alert.setHeaderText("We couldn't find your dictionary :(" );
                    //alert.setContentText("s");
                    alert.showAndWait();
                }
            }

        public ObservableList<TermDataInMap> getTermAndFrequency()
        {
            TreeMap<String, TermDataInMap> sortingTermsInCorpus = new TreeMap<>();
            sortingTermsInCorpus.putAll(Indexer.termsCorpusMap);
            ObservableList<TermDataInMap> frequencies = FXCollections.observableArrayList();

            for(String term: Indexer.termsCorpusMap.keySet()) {
                frequencies.add(Indexer.termsCorpusMap.get(term));
            }
            return frequencies;

        }

        /**
         * loading dictionary object from file to termInCorpus parameter
         * @param event
         */
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


        @FXML
        private void initChoiceBox(){

            languagesBox.setItems(languagesBoxOptions);
            languagesBox.setValue("English");
        }

}