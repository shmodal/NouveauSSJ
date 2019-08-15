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
		
		int nrun=4;
		
		int demandLow = 0; // 5 or 20
		int demandHigh = 10; // 5 or 20
		
		String file="/u/nervogui/eclipse-workspace/NouveauSSJ/dataFlow/";
		//double eps=0.05;
		
		//String number="2";
		//String file="Alexo"+number+".txt";
		file=file+"lattice3and3.txt";
		
		
		//procMC(demand,file, nrun);
		procPMC(demandLow, demandHigh,file, nrun,8);
		//procPMCfilter(demand,file, nrun);
		//procPMCfilterOut(demand,file, nrun);
	}

	
	private static void procMC(int demand,String file, int nrun) {
		
		GraphFlow g = null;
		String nameG = null;
		
		
		
		try {
			
			g = new GraphFlow(file);
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
			g = new GraphFlow(file);
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
	
	private static void procPMC(int demandLow,int demandHigh,String file, int nrun,int b) {
		GraphFlow g = null;
		try {
			g = new GraphFlow(file);
			g.Undirect();
			g.resetCapacities();
		}catch(IOException e){
	    	System.out.println("Probleme de fichier "+ file);
	    	return;
	    }
		
		RandomStream stream = new LFSR113();
		double rho = 0.6;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		PMCFlowContinuous p = new PMCFlowContinuous(g);

		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		System.out.println("============================================ No Filter");
		//PMCNonOriented p = new PMCNonOriented(g);
		p.filter=false; p.filterOutside=false;
		stream.resetStartSubstream();
		p.initCapaProbaB(tab, rho, epsilon[0]);
		p.trimCapacities(demandHigh);
		p.run(nrun,stream,demandLow,demandHigh);

	}
	
	
}
