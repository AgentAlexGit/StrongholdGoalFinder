package com.controllers;

import com.helpers.GoalFinderHelper;
import com.vision.IPCameraManual;
import com.vision.ImageOperations;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.controlsfx.control.RangeSlider;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.net.URL;
import java.util.ResourceBundle;

public class ControlScreenController implements Initializable{

    private IPCameraManual ipcam;

    @FXML
    private ImageView src;

    @FXML
    private ImageView processed;

    @FXML
    private RangeSlider hSlider;

    @FXML
    private RangeSlider vSlider;

    @FXML
    private RangeSlider sSlider;


    private Mat nextFrame()
    {

        try {
            Mat test = ipcam.getMatSnapshotFromStream();
            ImageOperations.saveMat(test);
            return test;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline line = new Timeline();
        src.fitWidthProperty().bind(((Pane)src.getParent()).widthProperty());
        src.fitHeightProperty().bind(((Pane)src.getParent()).heightProperty());

        try {
            ipcam = new IPCameraManual("169.254.160.69", IPCameraManual.HIGH_RES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GoalFinderHelper helper = new GoalFinderHelper(processed, src);

        processed.fitWidthProperty().bind(((Pane)processed.getParent()).widthProperty());
        processed.fitHeightProperty().bind(((Pane)processed.getParent()).heightProperty());
        for(int i = 1; i < 2; i++)
        {
            final int finalI = i;
            KeyFrame frame = new KeyFrame(Duration.millis(2000 * i), event ->
            {
                Mat snapshot = nextFrame();
                if(snapshot != null)
                {
                    src.setImage(helper.fromMattoImage(ImageOperations.filterHSV(snapshot)));
                    processed.setImage(helper.fromMattoImage(ImageOperations.findBoundingBoxes(snapshot)));
                }
            });

            line.getKeyFrames().add(frame);
            line.setCycleCount(Animation.INDEFINITE);
            line.play();
        }


        hSlider.setHighValue(ImageOperations.hHigh);
        hSlider.setLowValue(ImageOperations.hLow);

        vSlider.setHighValue(ImageOperations.vHigh);
        vSlider.setLowValue(ImageOperations.vLow);

        sSlider.setHighValue(ImageOperations.sHigh);
        sSlider.setLowValue(ImageOperations.sLow);

        sSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.sLow = (double) newValue);
        sSlider.highValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.sHigh = (double) newValue);

        vSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.vLow = (double) newValue);
        vSlider.highValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.vHigh = (double) newValue);

        hSlider.lowValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.hLow = (double) newValue);
        hSlider.highValueProperty().addListener((observable, oldValue, newValue) -> ImageOperations.hHigh = (double) newValue);

        //line.setCycleCount(Animation.INDEFINITE);
        line.play();

    }




}
