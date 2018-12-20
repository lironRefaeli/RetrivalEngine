package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class citiesSelectionsController{

    ObservableList<String> citiesOptions = FXCollections.observableArrayList("London","Paris");
    @FXML
    ListView citiesList;


    public void chooseCities(ActionEvent event) {

        citiesList.setItems(citiesOptions);
        List<String> citiesSelections = new ArrayList<>();
        citiesSelections.addAll(citiesList.getSelectionModel().getSelectedItems());

        for(int i=0;i<citiesSelections.size();i++)
            System.out.println(citiesSelections.get(i));
    }




}
