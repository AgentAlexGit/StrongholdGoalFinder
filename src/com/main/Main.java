package com.main;


import org.opencv.core.Mat;
import com.vision.ImageOperations;
import com.vision.dllHandler;

public class Main {
	static double[] me = {3,3};
	static Mat image;



	public static void main (String args[]) throws Exception{
		dllHandler.loadOpenCVdllFile();

		for(int i =1; i < 14; i++){
			ImageOperations.saveMat(ImageOperations.findBoundingBoxes(ImageOperations.getMatImageFromFile("/res/images/testImages/img"+i+".jpg")), "filteredRects"+i);
		}
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

}  
