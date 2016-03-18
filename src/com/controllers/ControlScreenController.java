package com.controllers;

import com.helpers.GoalFinderHelper;
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

import java.net.URL;
import java.util.ResourceBundle;

public class ControlScreenController implements Initializable{

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Timeline line = new Timeline();
        src.fitWidthProperty().bind(((Pane)src.getParent()).widthProperty());
        src.fitHeightProperty().bind(((Pane)src.getParent()).heightProperty());

        GoalFinderHelper helper = new GoalFinderHelper(processed, src);
        new Thread(helper).start();

        processed.fitWidthProperty().bind(((Pane)processed.getParent()).widthProperty());
        processed.fitHeightProperty().bind(((Pane)processed.getParent()).heightProperty());
        for(int i = 1; i < 15; i++)
        {
            final int finalI = i;
            KeyFrame frame = new KeyFrame(Duration.millis(1000 * i), event ->
            {
                System.out.println(finalI);
                String filePath = "/images/testImages/img" + finalI + ".jpg";
                helper.setFilePath(filePath);

            });

            line.getKeyFrames().add(frame);
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

        line.setCycleCount(Animation.INDEFINITE);
        line.play();

    }




}
