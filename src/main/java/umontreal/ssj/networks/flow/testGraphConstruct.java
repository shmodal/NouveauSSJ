package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.networks.createGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class testGraphConstruct {

	public static void main(String[] args) {
		
		
		
	   //public static final String RAREHOME = "/u/nervogui/Documents/Librairie rarev";
	   String RAREHOME = "/u/nervogui/Documents/Librairie rarev";
	   String DIRDAT = "/data/";
	   String name = RAREHOME + DIRDAT + "testGraphFlow" ;
	   
		try {
			
			
			GraphFlow g=new GraphFlow(name);
			System.out.println(g.toString());
		}catch (IOException ioe) {
	        System.out.println("Trouble reading from the file: " + ioe.getMessage());
	    } 
		
		
	}
}
