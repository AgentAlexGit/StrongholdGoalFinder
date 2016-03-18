package com.vision;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageOperations {

    private static final double filterSize = 25;

    private static final int hHigh = 255,
            sHigh = 255,
            vHigh = 255,
            hLow = 0,
            sLow = 235,
            vLow = 172;

    public static Mat flipImageY(Mat imageToFlip) {
        Core.flip(imageToFlip, imageToFlip, 1);
        System.out.println("Image flipped along Y!");
        return imageToFlip;
    }

    public static boolean saveMat(Mat matToSave) {
        return saveMat(matToSave, getTime());
    }

    public static boolean saveMat(Mat matToSave, String filename) {
        filename += ".jpg";
        if (!matToSave.empty()) {
            Imgcodecs.imwrite(filename, matToSave);
            System.out.println(String.format("Saved: %s", filename));
            return true;
        }
        return false;
    }

    private static Mat filterHSV(Mat image) {
        Mat output = new Mat();
        Core.inRange(image, new Scalar(hLow, sLow, vLow), new Scalar(hHigh, sHigh, vHigh), output);
        Imgproc.erode(output, output, new Mat(new Size(filterSize, filterSize), 0));
        Imgproc.dilate(output, output, new Mat(new Size(filterSize, filterSize), 0));
        return output;
    }

    private static String getTime() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static Mat getMatImageFromFile(String filePath) {
        System.out.println("\nCreating input stream");
        InputStream in = ImageOperations.class.getClass().getResourceAsStream(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new Mat();
    }

    public static Mat findBoundingBoxes(Mat image) {
        try {
            ArrayList<MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();

            Mat original = image.clone();
            Mat filteredImage = ImageOperations.filterHSV(original);
            //Imgproc.erode(imageFromFile, imageFromFile, new Mat(new Size(30,30), 0));
            //Imgproc.dilate(imageFromFile, imageFromFile, new Mat(new Size(30,30), 0));

            Imgproc.findContours(filteredImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            ImageOperations.saveMat(filteredImage, "contours");

            Mat dilated = filteredImage.clone();


            MatOfPoint2f approxCurve = new MatOfPoint2f();

            //For each contour found
            for (int i = 0; i < contours.size(); i++) {
                //Imgproc.drawContours(original,contours,i,new Scalar(Math.random()*255,Math.random()*255,Math.random()*255),5);

                //Convert contours(i) from MatOfPoint to MatOfPoint2f
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(i).toArray());
                //Processing on mMOP2f1 which is in type MatOfPoint2f
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                //Convert back to MatOfPoint
                MatOfPoint2f point2f = new MatOfPoint2f(approxCurve.toArray());
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                // Get bounding rect of contour
                RotatedRect rect = Imgproc.minAreaRect(point2f);
                Point[] rect_points = new Point[4];
                rect.points(rect_points);

                float aspectRatio = (float) rect.size.width / (float) rect.size.height;

                System.out.println(aspectRatio);
                System.out.println(rect.size.width);
                if ((aspectRatio < 0.7 && aspectRatio > 0.5) && (rect.size.width > 200)) {
                    //draw enclosing rectangle (all same color, but you could use variable i to make them unique)
                    System.out.println("Rectangle: " + i + " AR: " + aspectRatio + " Width: " + rect.size.width);
                    System.out.println("Array length " + approxCurve.toArray().length);


                    MatOfInt4 defects = new MatOfInt4();
                    MatOfInt hull = new MatOfInt();

                    Imgproc.convexHull(points, hull);
                    Imgproc.convexityDefects(points, hull, defects);

                    List<Integer> defectArray = defects.toList();
                    for (int j = 1; j < hull.toList().size(); j++) {
                        //Imgproc.line(original,contours.get(i).toList().get(hull.toList().get(j-1).intValue()),contours.get(i).toList().get(hull.toList().get(j).intValue()),new Scalar(0,255,0),5);
                    }
                    for (int j = 3; j < defectArray.size(); j += 4) {
                        float depth = (float) (defectArray.get(j) / 256.0);
                        //Imgproc.line(original,contours.get(i).toList().get(j-3),contours.get(i).toList().get(j-2),new Scalar(0,0,255),5);

                        //System.out.println(depth);
                        //System.out.println(contours.get(i).height());
                        System.out.println(point2f.toArray().length);
                        //if(depth/contours.get(i).height() > -1){
                        for (int k = 0; k < 4; k++)
                            Imgproc.line(original, rect_points[k], rect_points[(k + 1) % 4], new Scalar(0, 255, 0), 5);
                        //Imgproc.drawMarker(original,contours.get(i).toList().get(defectArray.get(j-1)),new Scalar(255,0,0),Imgproc.MARKER_STAR,20,5,8);

                        //}

                    }
                }
            }
            ImageOperations.saveMat(dilated, "contours");

            return original;

        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return image;
    }

}
