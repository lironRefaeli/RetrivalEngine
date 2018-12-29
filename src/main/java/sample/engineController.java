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
    static boolean stemmerSelection;
    static String pathToDisk;

    @FXML
    private TextField searchBoxText;
    @FXML
    private CheckBox semanticCheckBox;

    /**
    called when user click on "Search" button
    distinguishes between a written query and a file of queries
    start the searcher object to make the search */
    public void searchQuery(ActionEvent actionEvent)
    {

        boolean semanticSelection = semanticCheckBox.isSelected();
        String queryText = searchBoxText.getText();
        parser = new Parse(stemmerSelection);
        try{parser.LoadStopWordsList(pathToDisk + "\\stop_words.txt");}
        catch (IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not find the stop_words file in the specified folder");
            alert.showAndWait();
            return;
        }
        ranker = new Ranker(stemmerSelection, semanticSelection, pathToDisk);
        List<Query> queryList = new ArrayList<>();

        //The user didnt write anything in the search line
        if(queryText.length() == 0)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("please enter a query or a text file that contains queries");
            alert.showAndWait();
            return;
        }
        //it is not a query, it is a txt file that contains several queries
        else if(queryText.length() > 4 && queryText.substring(queryText.length()-4).equals(".txt"))
        {
            queryList = ReadFile.ReadQueries(queryText);
            if(queryList == null)
                return;

        }
        //A written query
        else
        {
            Query onlyQuery = new Query();
            onlyQuery.queryID = Integer.toString(Query.randomQueryID);
            Query.randomQueryID++;
            onlyQuery.title = queryText;
            queryList.add(onlyQuery);
        }
        //start the searcher to handle the query/queries
        searcher = new Searcher(ranker, parser, semanticSelection, stemmerSelection);
        searcher.handleQuery(queryList);
        displayResults();


    }

    //returns a list of QureyIDAndDocNumber object for displayResults() function to display
    public ObservableList<QureyIDAndDocNumber> getQueriesAndDocs()
    {
        ObservableList<QureyIDAndDocNumber> queriesAndDocs = FXCollections.observableArrayList();

        for(String queryId : searcher.queryIDToRankedMap.keySet())
        {
            for(String docNumber : searcher.queryIDToRankedMap.get(queryId).keySet())
            {
                QureyIDAndDocNumber queryAndDoc = new QureyIDAndDocNumber(queryId,docNumber);
                queriesAndDocs.add(queryAndDoc);
            }
        }

        return queriesAndDocs;

    }

    //display the user the result the searcher found
    public void displayResults() {

        //The map that saves all the queries ids and the documents numbers
        if (searcher.queryIDToRankedMap != null) {

            //create the table's format
            TableColumn<QureyIDAndDocNumber, String> queryColumn = new TableColumn<>("QueryId");
            queryColumn.setMinWidth(300);
            queryColumn.setCellValueFactory(new PropertyValueFactory<>("queryID"));
            TableColumn<QureyIDAndDocNumber, String> docColumn = new TableColumn<>("DocNumber");
            docColumn.setMinWidth(300);
            docColumn.setCellValueFactory(new PropertyValueFactory<>("docNum"));


            TableView<QureyIDAndDocNumber> table = new TableView<>();
            table.setItems(getQueriesAndDocs());
            table.getColumns().addAll(queryColumn, docColumn);

            //on double click on one of the rows, display the entities of the doc number in that row
            table.setRowFactory( tv -> {
                TableRow<QureyIDAndDocNumber> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        QureyIDAndDocNumber rowData = row.getItem();
                        String docNum = rowData.getDocNum();
                        try {
                            displayEntities(docNum);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return row ;
            });

            VBox vbox = new VBox();
            vbox.getChildren().addAll(table);
            FXMLLoader fxmlLoader = new FXMLLoader();
            try
            {
                Parent root1 = fxmlLoader.load(getClass().getResource("/resultsWindow.fxml").openStream());
            }
            catch (IOException e)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Had a problem with opening the results window");
                alert.showAndWait();
                return;
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(vbox));
            stage.show();



        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setTitle("Information Dialog");
            alert.setHeaderText("No results for this query" );
            //alert.setContentText("s");
            alert.showAndWait();
        }
    }

    //display the user the entities of one of the retrieved documents
    private void displayEntities(String docNum) throws IOException {

        //The map that contains all the documents in the corpus
        if (Indexer.docsCorpusMap != null) {

            //create the table's format
            TableColumn<EntityAndGrade, String> entityColumn = new TableColumn<>("Entity");
            entityColumn.setMinWidth(300);
            entityColumn.setCellValueFactory(new PropertyValueFactory<>("entity"));
            TableColumn<EntityAndGrade, Double> gradeColumn = new TableColumn<>("Grade");
            gradeColumn.setMinWidth(300);
            gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));


            TableView<EntityAndGrade> table = new TableView<>();
            table.setItems(getEntitiesAndGrades(docNum));
            table.getColumns().addAll(entityColumn, gradeColumn);

            VBox vbox = new VBox();
            vbox.getChildren().addAll(table);
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root1 = fxmlLoader.load(getClass().getResource("/entitiesWindow.fxml").openStream());
            Stage stage = new Stage();
            stage.setScene(new Scene(vbox));
            stage.show();

        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            //alert.setTitle("Information Dialog");
            alert.setHeaderText("No results for this query" );
            //alert.setContentText("s");
            alert.showAndWait();
        }
    }

    //returns a list of EntityAndGrade object for displayEntities() function to display for every document
    private ObservableList<EntityAndGrade> getEntitiesAndGrades(String docNum) {

        ObservableList<EntityAndGrade> entityAndGrades = FXCollections.observableArrayList();
        for(String entity: Indexer.docsCorpusMap.get(docNum).entitiesGardes.keySet())
        {
            EntityAndGrade entityAndGrade = new EntityAndGrade(entity, Indexer.docsCorpusMap.get(docNum).entitiesGardes.get(entity));
            entityAndGrades.add(entityAndGrade);
        }

        return entityAndGrades;
    }


    public static void setStemmerSelection(boolean selected) { stemmerSelection = selected; }

    public static void setPathToDisk(String path) { pathToDisk = path; }

    //opens the window with all the cities in corpus to choose from
    public void openCitiesSelections(ActionEvent event) throws IOException
    {

        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root1 = fxmlLoader.load(mainController.class.getResource("/citiesSelections.fxml").openStream());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("engineWindow");
        stage.setScene(new Scene(root1, 800, 600));
        stage.show();


    }

    //responsible for the "Browse" button on the Serach window
    public void BrowseQueriesPath(ActionEvent event)
    {
        JFileChooser chooser = new JFileChooser();
        //chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Choose queries' file");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            searchBoxText.setText("" + chooser.getSelectedFile());


    }

    //responsible for the "Save results" button on the Serach window
    public void SaveResultFiles(ActionEvent actionEvent)
    {
        String folderPath = "";
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose Result File Path");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            folderPath =  chooser.getSelectedFile().getPath();

        try { searcher.saveResultFile(folderPath); }
        catch (IOException e) { }
        catch (NullPointerException e) { }

    }
}

