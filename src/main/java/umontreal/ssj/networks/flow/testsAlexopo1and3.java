package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


/**
 * This class calls reliability estimation tests on networks presented in Daly and Alexopoulos (2006).
 * Crude Monte Carlo, PMC Flow, PMC Flow with Filter and PMC Flow with FilterOutside can be
 * called for comparaison.
 * 
 */


public class testsAlexopo1and3 {

	public static void main(String[] args) {
		

		//Build graph 3 Daly and Alexopoulos
		
		
		//GraphFlow g3 = ExamplesGraphs.buildAlexo3NoOr();
		
		//int demande = 20; // ou 20
		//g3.setSource(0);
		//g3.setTarget(6);
	    
		//MonteCarloFlowNonOriented mc3 = new MonteCarloFlowNonOriented(g3);
	    
		//RandomStream stream = new LFSR113();
		//mc3.run(50000,stream,demande);
		
		
		
		
		//PMCNonOriented p = new PMCNonOriented(g3);
		
		//p.trimCapacities(demande);
		
		//System.out.println("Filter single");
	    //p.filter=true;
		//stream.resetStartSubstream();
	    //p.run(100000,stream,demande);
	    
	    //System.out.println("No Filter");
	    //p.filter=false;
		//stream.resetStartSubstream();
	    //p.run(100000,stream,demande);
	    
	    
	    
		//Build graph 1 Daly and Alexopoulos
		
		//GraphFlow g1 = ExamplesGraphs.buildAlexo1NoOr();
		
//		GraphFlow g1 = ExamplesGraphs.buildAlexo1NoOr();
//
//		
//		int demande = 30; // ou 30
//		g1.setSource(0);
//		g1.setTarget(9);
//		
//		//MonteCarloFlowNonOriented mc1 = new MonteCarloFlowNonOriented(g1);
//	    
//		RandomStream stream = new LFSR113();
//		//mc1.run(1000000,stream,demande);
//		
//		
//		PMCNonOriented p = new PMCNonOriented(g1);
//		
//		p.trimCapacities(demande);
//		
//		System.out.println("Filter single");
//	    p.filter=true;
//	    //p.level = 0.7;
//		stream.resetStartSubstream();
//	    p.run(100000,stream,demande);
//	    
//	    System.out.println("No Filter");
//	    p.filter=false;
//	    
//		stream.resetStartSubstream();
//	    p.run(100000,stream,demande);
	    
		
		int demand = 20;
	    proc(demand,0,6);
		
//		int m = g1.getNumLinks();
//		for (int i=0;i<m;i++) {
//			LinkFlow Edge = g1.getLink(i);
//			int a = Edge.getSource();
//			int b = Edge.getTarget();
//			System.out.println("Lien " + (i+1) + ": " + (a+1) + " et " + (b+1));
//			System.out.println("Capacités");
//			printTab(Edge.getProbabilityValues());
//		}
	    
	}

	
	private static void proc(int demand,int source, int target) {
		int numG = 3; // 1 for network 1 of Daly/Alexopo,  3 for network 3
		boolean mc = false; // true for launching MC Test
		mc = true;
		boolean pmc = false; //true for launching PMC
		pmc = true;
		boolean filter = false; // true for filtering simple
		filter= true;
		boolean filterOutside = false; // true for Filter Outside
		filterOutside = true;
		
		int nrun = 1000000 ;
		
		GraphFlow g = null;
		String nameG = null;
		
		if (numG ==1) {
			g =  ExamplesGraphs.buildAlexo1NoOr();
		    nameG = "Network number 1 of Daly and Alexopoulos";}
		if (numG ==3) {
			g =  ExamplesGraphs.buildAlexo1NoOr(); 	
			nameG = "Network number 3 of Daly and Alexopoulos";
		}
		int numNodes = g.getNumNodes();
		assert(0 <= source && source < numNodes );
		assert(0 <= target && target < numNodes );
		
		g.setSource(source);
		g.setTarget(target);
		
		RandomStream stream = new LFSR113();
		if (mc) {
			System.out.println("============================================ Monte Carlo");
			System.out.println("Graph : " +nameG);
			System.out.println("Warning : estimation of reliability instead of unreliability");
			MonteCarloFlowNonOriented mc1 = new MonteCarloFlowNonOriented(g);
			mc1.run(nrun,stream,demand);
		}
		
		stream.resetStartSubstream();
		PMCFilterOutsideNew p = null;
		if(pmc) { 
			System.out.println("============================================ Permutation Monte Carlo");
			if (filter) {
				System.out.println("============================================ Filter single");
				//PMCNonOriented p = new PMCNonOriented(g); 
				p = new PMCFilterOutsideNew(g);
				p.filter=true; p.filterOutside=false;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
				
			}
			else if (filterOutside) {
				System.out.println("============================================ Filter Outside");
				//PMCFilterOutsideNew p = new PMCFilterOutsideNew(g);
				p = new PMCFilterOutsideNew(g);
				p.filterOutside=true; p.filter=false;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
			}
			else { System.out.println("============================================ No Filter");
				//PMCNonOriented p = new PMCNonOriented(g);
				p = new PMCFilterOutsideNew(g);
				p.filter=false; p.filterOutside=false;
				stream.resetStartSubstream();
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
				}
		
		
			
		
		}
	
		
		
		
	}
	



	   private static void printTab(double[] t) {
		   int m = t.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t[i]);
		   }
	   }
	   private static void printTab(int[] t) {
		   int m = t.length;
		   for (int i =0;i<m;i++) {
			   System.out.println(t[i]);
		   }
	   }

		
}
