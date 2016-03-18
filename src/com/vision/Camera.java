package com.vision;


import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Camera {
	private VideoCapture cap;
	
	public Camera(int cameraNumber){
		initializeCamera(cameraNumber);
	}
	
	public void initializeCamera(int cameraNumber){
		System.out.println("Accessing camera...");
		cap = new VideoCapture(cameraNumber);
		Mat matty = new Mat();
		cap.read(matty);
		if((matty.width()== 0 || matty.height()== 0) && cameraNumber != 0){
			System.out.println("Error reading from camera: "+cameraNumber+
							   "\nSelecting default camera: 0 instead...");
			initializeCamera(0);
			return;
		}
		System.out.println("Using camera: "+cameraNumber+"\n");
	}
	
	public Mat getMatSnapshot(){
		Mat tempMat = new Mat();
		cap.read(tempMat);
		System.out.println("Image Data:\nHeight: "+tempMat.height()+"\nWidth: "+tempMat.width());	
		return tempMat;
	}
	
	public boolean takeSnapshot(){
		return saveMatSnapshot(getMatSnapshot(), String.valueOf(getTime()));
	}
		
	public boolean saveMatSnapshot(Mat matToSave){
		return saveMatSnapshot(matToSave, String.valueOf(getTime()));
	}
	
	public boolean saveMatSnapshot(Mat matToSave, String filename){
		filename += ".jpg";
	    if (matToSave == null){
	    	System.out.println("Invalid Mat input type");
	    	return false;
	    }
	    else if(!matToSave.empty()){
		    Imgcodecs.imwrite(filename, matToSave);
		    System.out.println(String.format("Saved: %s", filename));
			return true;
		}
		else{
			Imgcodecs.imwrite(filename, getMatSnapshot());
			System.out.println(String.format("*Saved: %s", filename));
		}
		return false;		
	}
	
	private String getTime(){
		return String.valueOf(System.currentTimeMillis());
	}
	
	
}
