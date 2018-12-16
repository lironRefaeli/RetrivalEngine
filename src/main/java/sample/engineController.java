package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    static boolean stemmerSelection;
    static String pathToDisk;


    public void searchQuery(ActionEvent actionEvent) throws IOException {

        String queryText = searchBoxText.getText();
        parser = new Parse(stemmerSelection);
        ranker = new Ranker(stemmerSelection, pathToDisk);
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
        searcher = new Searcher(ranker, parser);
        searcher.handleQuery(queryList);


    }



    public static void setStemmerSelection(boolean selected) {

        stemmerSelection = selected;
    }

    public static void setPathToDisk(String path) {

        pathToDisk = path;
    }
}

