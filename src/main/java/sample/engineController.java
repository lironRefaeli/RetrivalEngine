package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is for section 2 of this project
 */
public class engineController
{
    Searcher searcher;
    Ranker ranker;
    Parse parser;
    @FXML
    private TextField searchBoxText;
    @FXML
    private CheckBox semanticCheckBox;

    static boolean stemmerSelection;
    static String pathToDisk;


    public void searchQuery(ActionEvent actionEvent) throws IOException {

        boolean semanticSelection = semanticCheckBox.isSelected();
        String queryText = searchBoxText.getText();
        parser = new Parse(stemmerSelection);
        parser.LoadStopWordsList(pathToDisk + "\\stop_words.txt");
        ranker = new Ranker(stemmerSelection, semanticSelection, pathToDisk);
        List<Query> queryList = new ArrayList<>();

        //it is not a query, it is a txt file that contains several queries
        if(queryText.length() > 4 && queryText.substring(queryText.length()-4).equals(".txt"))
        {
            queryList = ReadFile.ReadQueries(queryText);
            if(queryList == null)
                return;

        }
        else
        {
            Query onlyQuery = new Query();
            onlyQuery.queryID = Integer.toString(Query.randomQueryID);
            Query.randomQueryID++;
            onlyQuery.title = queryText;
            queryList.add(onlyQuery);
        }
        searcher = new Searcher(ranker, parser, semanticSelection);
        searcher.handleQuery(queryList);


    }



    public static void setStemmerSelection(boolean selected) {

        stemmerSelection = selected;
    }

    public static void setPathToDisk(String path) {

        pathToDisk = path;
    }

    public void openCitiesSelections(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root1 = fxmlLoader.load(mainController.class.getResource("/citiesSelections.fxml").openStream());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("engineWindow");
        stage.setScene(new Scene(root1, 300, 500));
        stage.show();


    }

    public void BrowseQueriesPath(ActionEvent event) {
        JFileChooser chooser = new JFileChooser();
        //chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose queries' file");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            searchBoxText.setText("" + chooser.getSelectedFile());


    }
}

