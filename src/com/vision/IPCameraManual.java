package com.vision;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

public class IPCameraManual {
	
	public static final String
		HIGH_RES = "640x480",
		LOW_RES  = "320x240";
	
	
	private URL url;
	private InputStream is;
	
	public IPCameraManual(String ipAddress) throws Exception{
		init("http://"+ipAddress+"/axis-cgi/jpg/image.cgi?resolution="+LOW_RES);
	}
	
	public IPCameraManual(String ipAddress, String quality) throws Exception{
		init("http://"+ipAddress+"/axis-cgi/jpg/image.cgi?resolution="+quality);
		
	}
	
	public IPCameraManual(String ipAddress, String username, String password) throws Exception{
		init("http://"+username+":"+password+"@"+ipAddress+"/axis-cgi/jpg/image.cgi?resolution="+LOW_RES);		
	}
	
	public IPCameraManual(String ipAddress, String username, String password, String quality) throws Exception{
		init("http://"+username+":"+password+"@"+ipAddress+"/axis-cgi/jpg/image.cgi?resolution="+quality);
	}
	
	public boolean takeMatSnapshotFromStream() throws Exception{
		return takeMatSnapshotFromStream(getMatSnapshotFromStream(), getTime());
	}
	
	public boolean takeMatSnapshotFromStream(Mat matToSave) throws Exception{
		return takeMatSnapshotFromStream(matToSave, getTime());
	}
	
	public boolean takeMatSnapshotFromStream(Mat matToSave, String filename) throws Exception{
		filename += ".jpg";
	    if (matToSave == null || !(matToSave instanceof Mat)){
	    	System.out.println("Invalid Mat input type");
	    	return false;
	    }
	    else if(!matToSave.empty()){
		    Imgcodecs.imwrite(filename, matToSave);
		    System.out.println(String.format("Saved: %s", filename));
			return true;
		}
		else{
			Imgcodecs.imwrite(filename, getMatSnapshotFromStream());
			System.out.println(String.format("*Saved: %s", filename));
		}
		return false;		
	}
	
	public Mat getMatSnapshotFromStream() throws Exception {
		resetStream();
		ByteArrayOutputStream tempImage = new ByteArrayOutputStream();
		
		byte[] b = new byte[2048];
		int length;
		
		while ((length = is.read(b)) != -1) {
			tempImage.write(b, 0, length);
		}
		tempImage.close();
		System.out.println("Mat snapshot acquired!");
		return Imgcodecs.imdecode(new MatOfByte(tempImage.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
	} 
	
	public void takeSnapshotFromStream() throws Exception {
		takeSnapshotFromStream(getTime());
	} 
	
	public void takeSnapshotFromStream(String fileName) throws Exception {
		resetStream();
		fileName += ".jpg";
		OutputStream os = new FileOutputStream(fileName);
		
		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}
		os.close();
		System.out.println("Snapshot acquired! "+fileName);
	} 
	
	public void closeStream() throws Exception{
		is.close();
	}
	
	private void resetStream() throws Exception{
		is = url.openStream();
	}
	
	private void init(String imageURL) throws Exception{
		url = new URL(imageURL);
		resetStream();
	}
	private String getTime(){
		return String.valueOf(System.currentTimeMillis());
	}
}
