package umontreal.ssj.networks.flow;

import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testsAlexopo1and3 {

	public static void main(String[] args) {
		

		//Build graph 3 Daly and Alexopoulos
		
		
		//GraphFlow g3 = ExamplesGraphs.buildAlexo3NoOr();
		
		//int demande = 5; // ou 20
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
		///stream.resetStartSubstream();
	    //p.run(100000,stream,demande);
	    
	    
	    
		//Build graph 1 Daly and Alexopoulos
		
		//GraphFlow g1 = ExamplesGraphs.buildAlexo1NoOr();
		
		GraphFlow g1 = ExamplesGraphs.buildAlexo1NoOr();
		g1 = ExamplesGraphs.Undirect(g1);

		
		int demande = 60; // ou 30
		g1.setSource(0);
		g1.setTarget(9);
		
		MonteCarloFlowNonOriented mc1 = new MonteCarloFlowNonOriented(g1);
	    
		RandomStream stream = new LFSR113();
		mc1.run(1000000,stream,demande);
		
		
		PMCNonOriented p = new PMCNonOriented(g1);
		
		p.trimCapacities(demande);
		
		//System.out.println("Filter single");
	    //p.filter=true;
		//stream.resetStartSubstream();
	    //p.run(100000,stream,demande);
	    
	    //System.out.println("No Filter");
	    //p.filter=false;
		//stream.resetStartSubstream();
	    //p.run(100000,stream,demande);
	    
		


		
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
