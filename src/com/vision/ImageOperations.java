package com.vision;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageOperations {

	private static final double filterSize = 2.5;

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
		Imgproc.erode(output, output, new Mat(new Size(filterSize,filterSize),0));
		Imgproc.dilate(output, output, new Mat(new Size(filterSize,filterSize),0));
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
			//ImageOperations.saveMat(filteredImage, "test");
			Mat backupFiltered = filteredImage.clone();
			//Imgproc.erode(imageFromFile, imageFromFile, new Mat(new Size(30,30), 0));
			//Imgproc.dilate(imageFromFile, imageFromFile, new Mat(new Size(30,30), 0));

			Imgproc.findContours(filteredImage, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
			//ImageOperations.saveMat(filteredImage,"contours");
			
			Mat dilated = filteredImage.clone();
			Imgproc.dilate(dilated, dilated, new Mat(new Size(30,30), 0));
			//ImageOperations.saveMat(dilated,"contours");

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
	            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

	            // Get bounding rect of contour
	            Rect rect = Imgproc.boundingRect(points);

				Imgproc.rectangle(original, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 0, 255), 3);
	            if(approxCurve.toArray().length > 5 && approxCurve.toArray().length < 11&& rect.width < 500 && rect.height/rect.width < .8 && rect.width > 50){
	            	//draw enclosing rectangle (all same color, but you could use variable i to make them unique)
		            System.out.println("Rectangle: "+i +" Width: "+rect.width);

		            Mat sub = backupFiltered.submat(rect);
		            if(compareWhiteToBlackPixels(sub)){
						System.out.println("Points " + approxCurve.toArray().length);
		            	Imgproc.rectangle(original, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 255, 0), 5);
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

	public static boolean compareWhiteToBlackPixels(Mat subMat){
		//saveMat(subMat, "subMat");
		int whitePixels = Core.countNonZero(subMat);
		float areaToWhite = (float)(subMat.width()*subMat.height())/(float)whitePixels;
		System.out.println("White Pixel Count in submat: "+whitePixels);
		System.out.println("Ratio of area to white pixels: "+areaToWhite);
		return (areaToWhite >= 2.5);
	}
}
