package com.vision;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sun.org.apache.xalan.internal.utils.FeatureManager;
import javafx.scene.transform.Rotate;
import org.opencv.core.*;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageOperations {

	private static final double filterSize = 1.5;

	public static double hHigh=255;
	public static double sHigh=255;
	public static double vHigh=255;
	public static double hLow=230;
	public static double sLow=235;
	public static double vLow=230;

	public static Mat flipImageY(Mat imageToFlip){
		Core.flip(imageToFlip, imageToFlip, 1);
		System.out.println("Image flipped along Y!");
		return imageToFlip;
	}

	public static boolean saveMat(Mat matToSave){
		return saveMat(matToSave, getTime());	
	}

	public static boolean saveMat(Mat matToSave, String filename){
		filename += ".jpg";
		if(!matToSave.empty()){
			Imgcodecs.imwrite(filename, matToSave);
			System.out.println(String.format("Saved: %s", filename));
			return true;
		}
		return false;		
	}

	public static Mat filterHSV(Mat image){
		Mat output = new Mat();
		Core.inRange(image, new Scalar(hLow,sLow,vLow), new Scalar(hHigh,sHigh,vHigh), output);
		Imgproc.erode(output, output, new Mat(new Size(3,3),0),new Point(),1);
		Imgproc.dilate(output, output, new Mat(new Size(4,4),0),new Point(),2);
		Imgproc.erode(output, output, new Mat(new Size(2.5,2.5),0),new Point(),3);
		Imgproc.dilate(output, output, new Mat(new Size(4,4),0),new Point(),1);
		return output;
	}

	public static String getTime(){
		return String.valueOf(System.currentTimeMillis());
	}

	public static Mat getMatImageFromFile(String filePath){
		System.out.println("\nCreating input stream");
		InputStream in = ImageOperations.class.getClass().getResourceAsStream(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int read = -1;
			while((read = in.read(buffer)) != -1) {
				baos.write(buffer, 0, read);
			}
			return Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return new Mat();		
	}

	public static Mat findBoundingBoxes(Mat image){
		try{
			ArrayList<MatOfPoint> contours = new ArrayList<>();
			Mat hierarchy = new Mat();

			Mat original = image.clone();

			Mat filteredImage = ImageOperations.filterHSV(original);

			Mat backupFiltered = filteredImage.clone();

			Imgproc.findContours(filteredImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

	        MatOfPoint2f   approxCurve = new MatOfPoint2f();
	        System.out.println("Found "+contours.size()+" contours");
	        //For each contour found
	        for (int i=0; i<contours.size(); i++)
	        {
	            //Convert contours(i) from MatOfPoint to MatOfPoint2f
	            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
	            
	            //Processing on mMOP2f1 which is in type MatOfPoint2f
	            double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	            Imgproc.approxPolyDP(contour2f, approxCurve,approxDistance, true);

	            //Convert back to MatOfPoint
	            MatOfPoint2f points = new MatOfPoint2f( approxCurve.toArray() );

	            // Get bounding rect of contour
	            RotatedRect rect = Imgproc.minAreaRect(points);

				drawRotatedRect(rect,original,new Scalar(0,0,255));
	            if(approxCurve.toArray().length > 5 && approxCurve.toArray().length < 11&& rect.size.width < 500 && rect.size.height/rect.size.width < .8 && rect.size.width > 50){
	            	//draw enclosing rectangle (all same color, but you could use variable i to make them unique)
		            System.out.println("Rectangle: "+i +" Width: "+rect.size.width);

		            Mat sub = backupFiltered.submat(rect.boundingRect());
		            if(compareWhiteToBlackPixels(sub)){
						System.out.println("Points " + approxCurve.toArray().length);
						drawRotatedRect(rect,original,new Scalar(0,255,0));
		            }

		           
	            }	            
	        }
	        
	        return original;

		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		return image;
	}

	private static void drawRotatedRect(RotatedRect rect, Mat src, Scalar color)
	{
		Point[] points = new Point[4];
		rect.points(points);
		for(int i =0; i < 4; i++)
		{
			Imgproc.line(src,points[i],points[(i+1)%4],color,4);
		}
	}


	public static Mat findGoalHomography(Mat src)
	{
		Mat img_object = getMatImageFromFile("/images/goalpost.png");
		Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
		Imgproc.cvtColor(img_object,img_object,Imgproc.COLOR_BGR2GRAY);
		int minHessian = 400;

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.AKAZE);
		MatOfKeyPoint keyPoints_src = new MatOfKeyPoint();
		MatOfKeyPoint keyPoints_object = new MatOfKeyPoint();
		detector.detect(src,keyPoints_src);
		detector.detect(img_object,keyPoints_object);

		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.AKAZE);

		System.out.println("Created extractor");
		Mat descriptors_src = new Mat();
		Mat descriptors_obj = new Mat();
		extractor.compute(src,keyPoints_src,descriptors_src);
		extractor.compute(img_object,keyPoints_object,descriptors_obj);

		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

		MatOfDMatch matches = new MatOfDMatch();
		matcher.match(descriptors_obj,descriptors_src,matches);

		double max_dist = 0; double min_dist = 100;

		for( int i = 0; i < descriptors_obj.rows(); i++ )
		{ double dist = matches.toArray()[i].distance;
			if( dist < min_dist ) min_dist = dist;
			if( dist > max_dist ) max_dist = dist;
		}

		MatOfDMatch good_matches = new MatOfDMatch();
		System.out.println(descriptors_obj.rows());
			for(int i =0;i < descriptors_obj.rows();i++)
			{
				if(matches.toArray()[i].distance< 3*min_dist)
				{
					good_matches.toList().add(good_matches.toArray()[i]);
					System.out.println("i");
				}
			}
		System.out.println(good_matches.cols());

		return null;
	}


	public static boolean compareWhiteToBlackPixels(Mat subMat){
		//saveMat(subMat, "subMat");
		int whitePixels = Core.countNonZero(subMat);
		float areaToWhite = (float)(subMat.width()*subMat.height())/(float)whitePixels;
		System.out.println("White Pixel Count in submat: "+whitePixels);
		System.out.println("Ratio of area to white pixels: "+areaToWhite);
		return (areaToWhite >= 2);
	}
}
