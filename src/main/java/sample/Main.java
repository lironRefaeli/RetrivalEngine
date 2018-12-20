package sample;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The main class is opening the main window for configuring the engine by JAVAFX.
 */
    public class Main extends Application {


        @Override
        public void start(Stage primaryStage) throws Exception {

            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("/mainWindow.fxml").openStream());
            primaryStage.setTitle("mainWindow");
            Scene scene = new Scene(root, 610, 320);
            //scene.getStylesheets().add(getClass().getResource("/cssTemplate.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();


        }


        public static void main(final String[] args) throws IOException {


            launch(args);

        }
    }



