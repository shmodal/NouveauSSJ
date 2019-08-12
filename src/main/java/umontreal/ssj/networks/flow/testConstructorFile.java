package umontreal.ssj.networks.flow;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testConstructorFile {
	public static void main(String[] args) {
		
		int nrun=1000000;
		
		int demand = 80; // 5 or 20
		
		//double eps=0.05;
		
		//String number="2";
		//String file="Alexo"+number+".txt";
		String file="lattice6and6";
		
		procMC(demand,file, nrun);
		procPMC(demand,file, nrun);
		//procPMCfilter(demand,file, nrun);
		//procPMCfilterOut(demand,file, nrun);
	}

	
	private static void procMC(int demand,String file, int nrun) {
		
		GraphFlow g = null;
		String nameG = null;
		
		
		
		try {
			
			g = new GraphFlow("/u/nervogui/eclipse-workspace/NouveauSSJ/dataFlow/"+file);
		    g.Undirect();
		    g.resetCapacities();
		    
		}catch(IOException e){
	    	System.out.println("Probleme de fichier "+ file);
	    	return;
	    }
		RandomStream stream = new LFSR113();
		System.out.println("============================================ Monte Carlo");
		System.out.println("Graph : " +file);
		MonteCarloFlow mc1 = new MonteCarloFlowNonOriented(g);
		mc1.run(nrun,stream,demand);
	}
	
	private static void procPMCfilter(int demand,String file, int nrun) {
		
		GraphFlow g = null;
		try {
			g = new GraphFlow("/u/nervogui/eclipse-workspace/NouveauSSJ/dataFlow/"+file);
			
			g.Undirect();
		}catch(IOException e){
	    	System.out.println("Probleme de fichier "+ file);
	    	return;
	    }
		
		RandomStream stream = new LFSR113();
		stream.resetStartSubstream();
		PMCFlowNonOriented p = null;
		p = new PMCFlowNonOriented(g);
		System.out.println("============================================ Permutation Monte Carlo");
		System.out.println("============================================ Filter single");

		
		//p = new PMCFilterOutsideNew(g);
		p.filter=true; p.filterOutside=false;
		stream.resetStartSubstream();
		p.trimCapacities(demand);
		p.run(nrun,stream,demand);
	}
			
	private static void procPMCfilterOut(int demand,String file, int nrun) {
		
		GraphFlow g = null;
		try {
			g = new GraphFlow("/u/nervogui/eclipse-workspace/NouveauSSJ/dataFlow/"+file);
			g.Undirect();
		}catch(IOException e){
	    	System.out.println("Probleme de fichier "+ file);
	    	return;
	    }
		
		RandomStream stream = new LFSR113();
		PMCFlowNonOriented p = null;
		p = new PMCFlowNonOriented(g);
		System.out.println("============================================ Permutation Monte Carlo");
		System.out.println("============================================ Filter Outside");
		//PMCFilterOutsideNew p = new PMCFilterOutsideNew(g);
		p.filterOutside=true; p.filter=false;
		p.frequency=5;p.seuil=0.8;
		stream.resetStartSubstream();
		p.trimCapacities(demand);
		p.run(nrun,stream,demand);
	}
	
	private static void procPMC(int demand,String file, int nrun) {
		GraphFlow g = null;
		try {
			g = new GraphFlow("/u/nervogui/eclipse-workspace/NouveauSSJ/dataFlow/"+file);
			g.Undirect();
			g.resetCapacities();
		}catch(IOException e){
	    	System.out.println("Probleme de fichier "+ file);
	    	return;
	    }
		
		RandomStream stream = new LFSR113();
		PMCFlow p = null;
		p = new PMCFlowNonOriented(g);
		System.out.println("============================================ No Filter");
		//PMCNonOriented p = new PMCNonOriented(g);
		p.filter=false; p.filterOutside=false;
		stream.resetStartSubstream();
		p.trimCapacities(demand);
		p.run(nrun,stream,demand);

	}
	
	
}
