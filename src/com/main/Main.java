package com.main;


import com.vision.IPCameraManual;
import com.vision.ImageOperations;
import com.vision.NetTable;
import com.vision.opencvLibraryHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Mat;

public class Main extends Application{

    public static void main(String args[]) throws Exception {
        opencvLibraryHandler.loadOpenCVdllFiles();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/ControlScreen.fxml"));


        primaryStage.setScene(new Scene(root));

        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

    }
}
