package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class citiesSelectionsController{

    //ObservableList<String> citiesOptions = FXCollections.observableArrayList("London","Paris");
    @FXML
    ListView citiesList;

    @FXML
    CheckBox londonCheckBox;
    @FXML
    CheckBox parisCheckBox;


    public void chooseCities(ActionEvent event) {

        boolean london = londonCheckBox.isSelected();
        boolean paris = parisCheckBox.isSelected();

        if(london)
            Ranker.listCitiesFromUser.add("LONDON");
        if(paris)
            Ranker.listCitiesFromUser.add("PARIS");

        for(int i=0;i<Ranker.listCitiesFromUser.size();i++)
            System.out.println(Ranker.listCitiesFromUser.get(i));
    }




}
