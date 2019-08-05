package umontreal.ssj.networks.flow;


import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testConstructorFile {
	public static void main(String[] args) {
		
		int demand = 60; // 5 or 20
		String number="1";
	    proc(demand,number);
	    
	}

	
	private static void proc(int demand,String number) {
		boolean mc = false; // true for launching MC Test
		mc = true;
		boolean pmc = false; //true for launching PMC
		pmc = true;
		boolean nofilter = false; //true for plain PMC(without filter)
		nofilter = true;
		boolean filter = false; // true for filtering simple
		filter= true;
		boolean fOutside = false; // true for Filter Outside
		//fOutside = true;
		
		int nrun = 1000000 ;
		
		GraphFlow g = null;
		String nameG = null;
		
		try {
			//g = new GraphFlow("../../../../../../../Alexo"+number);
			g = new GraphFlow("C:/Users/guill/eclipse-workspace/NouveauSSJ/Alexo"+number+".txt");
		    g = ExamplesGraphs.Undirect(g);
			nameG = "Network number "+number+" of Daly and Alexopoulos";
		}catch(IOException e){
	    	System.out.println("Problème de fichier "+ "../../../../../../../Alexo"+number);
	    }
		int numNodes = g.getNumNodes();
		
		
		RandomStream stream = new LFSR113();
		if (mc) {
			System.out.println("============================================ Monte Carlo");
			System.out.println("Graph : " +nameG);
			System.out.println("Warning : estimation of reliability instead of unreliability");
			MonteCarloFlowNonOriented mc1 = new MonteCarloFlowNonOriented(g);
			mc1.run(nrun,stream,demand);
		}
		
		stream.resetStartSubstream();
		PMCFlowNonOriented p = null;
		if(pmc) { 
			System.out.println("============================================ Permutation Monte Carlo");
			if (filter) {
				System.out.println("============================================ Filter single");
				p = new PMCFlowNonOriented(g); 
				//p = new PMCFilterOutsideNew(g);
				p.filter=true; p.filterOutside=false;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
				
			}
			if (fOutside) {
				System.out.println("============================================ Filter Outside");
				//PMCFilterOutsideNew p = new PMCFilterOutsideNew(g);
				p = new PMCFlowNonOriented(g);
				p.filterOutside=true; p.filter=false;
				p.frequency=5;p.seuil=0.8;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
			}
			else { System.out.println("============================================ No Filter");
				//PMCNonOriented p = new PMCNonOriented(g);
				p = new PMCFlowNonOriented(g);
				p.filter=false; p.filterOutside=false;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
				}
		
		
			
		
		}
	
		
		
	}
	
}
