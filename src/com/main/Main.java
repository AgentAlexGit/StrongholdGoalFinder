package com.main;


import com.vision.opencvLibraryHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application{

    public static void main(String args[]) throws Exception {
        opencvLibraryHandler.loadOpenCVdllFiles();

        launch(args);

//				IPCameraManual ipcam = new IPCameraManual("169.254.158.54", IPCameraManual.HIGH_RES);
//				NetTable net = new NetTable();
//				net.publishTestData("hello");
//
//				for(int i =0; i < 10; i++){
//					Mat capture = ipcam.getMatSnapshotFromStream();
//					ImageOperations.saveMat(capture, "orig");
//					ImageOperations.saveMat(ImageOperations.filterHSV(capture));
//					System.out.println("Capture: "+i+" complete!");
//				}
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
