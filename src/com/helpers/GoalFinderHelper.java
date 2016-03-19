package com.helpers;

import com.vision.ImageOperations;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

/**
 * Created by alex1 on 3/18/2016.
 */
public class GoalFinderHelper implements Runnable {

    private ImageView processed;
    private ImageView src;
    private String filePath = new String();
    private Boolean running = true;

    public GoalFinderHelper(ImageView processed, ImageView src) {
        this.processed = processed;
        this.src = src;
    }

    private void processesImages(String filePath)
    {


        Mat output =ImageOperations.findGoalHomography(ImageOperations.getMatImageFromFile(filePath)); /*ImageOperations.filterHSV(ImageOperations.getMatImageFromFile(filePath));*/

        //processed.setImage(fromMattoImage(output));
        System.out.println("Set output mat");
    }




    public Image fromMattoImage(Mat src)
    {

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( src.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = src.channels()*src.cols()*src.rows();
        byte [] b = new byte[bufferSize];
        src.get(0,0,b); // get all the pixels
        BufferedImage bImage = new BufferedImage(src.cols(),src.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        WritableImage wr = null;
        if (bImage != null) {
            wr = new WritableImage(bImage.getWidth(), bImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            // pw.setPixels(0,0,src.cols(),src.rows(),pw.getPixelFormat(),targetPixels,0,0);
            for (int x = 0; x < bImage.getWidth(); x++) {
                for (int y = 0; y < bImage.getHeight(); y++) {
                    pw.setArgb(x, y, bImage.getRGB(x, y));
                }
            }
        }
        System.out.println("Made to image to display");
        return wr;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
        System.out.println("Set file path");
    }

    public void stop()
    {
        running = false;
    }

    @Override
    public void run() {
        String oldValue = "";
        System.out.println("running");
        while(running)
        {
            //System.out.println("OldValue " + oldValue + " New Value " + filePath );
            if(!oldValue.equals(filePath))
            {
                src.setImage(new Image(filePath));
                System.out.println("Processing images");
                oldValue = filePath;
                processesImages(filePath);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
