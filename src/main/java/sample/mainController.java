package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class mainController {


    public void StartEngine(ActionEvent event) {

        try{
            FXMLLoader fxmlLoader=new FXMLLoader();
            Parent root1 = fxmlLoader.load(getClass().getResource("engineWindow.fxml").openStream());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("engineWindow");
            stage.setScene(new Scene(root1,500,500));
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
}
