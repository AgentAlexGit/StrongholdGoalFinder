package com.vision;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class opencvLibraryHandler {
	
	private static void loadDll(InputStream in, String name){
		try{
			byte[] buffer = new byte[1024];
		    int read = -1;
		    File temp = File.createTempFile(name, "");
		    FileOutputStream fos = new FileOutputStream(temp);
	
		    while((read = in.read(buffer)) != -1) {
		        fos.write(buffer, 0, read);
		    }
		    fos.close();
		    in.close();
		    
		    String absPath = temp.getAbsolutePath();
		    
		    System.load(absPath);
		    System.out.println(absPath);
		    temp.deleteOnExit();
		}
		catch(Exception e1){
			System.out.println(e1.toString());
		}
	}
	
	public static void loadOpenCVdllFiles(){
		
		String arch = System.getenv("PROCESSOR_ARCHITECTURE");
		String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

		boolean is64bit = arch.endsWith("64")
		                  || wow64Arch != null && wow64Arch.endsWith("64")
		                      ? true : false;
		
		InputStream in;
		
		  try {
			  if(is64bit){
				  System.out.println("Is 64-bit \nLoading x64 dll files...");
				  in = opencvLibraryHandler.class.getResourceAsStream("/x64/opencv_java310.dll");
			  }
			  else {
				  System.out.println("Is 32-bit \nLoading x86 dll files...");
				  in = opencvLibraryHandler.class.getResourceAsStream("/x86/opencv_java310.dll");

			  }		
			  System.out.println("Loading opencv core dll...");
		      loadDll(in, "opencv_java310.dll");
	      
		      
		   } catch (Exception e) {
			   System.out.println("Failed to retrieve dll "+e.toString());
		   }
		  
		  System.out.println("OpenCV has loaded correctly! :D \n\n");
	}
	
	
	
}
