package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testSet {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		GraphFlow g=new GraphFlow();
		//add Nodes
		for (int i =0;i<3;i++) {
			g.addNode(new NodeBasic(i));
		}

		g.addLink(new LinkFlow(0,0,1));
		g.addLink(new LinkFlow(1,1,2));
		int b = 2;
		g.setB(0, b);
		
		double[] Pr0 = new double[b+1];
		Pr0[0] =0.02 ;Pr0[1] = 0.08; Pr0[2] = 0.9;
		g.setProbabilityValues(0, Pr0);
		
		//printTab(g.getProbabilityValues(0));
		
		int[] Cap0 = new int[b+1];
		Cap0[0] = 0; Cap0[1] = 8; Cap0[2] = 12;
		g.setCapacityValues(0, Cap0);
		
		
		Pr0[0] = 0.04;Pr0[1]=0.06;
		g.setProbabilityValues(1, Pr0);
		
		
		
		
		double[] tab0Proba = g.getProbabilityValues(0);
		double[] tab1Proba = g.getProbabilityValues(1);
	    
		
		printTab(tab0Proba);
		printTab(tab1Proba);

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
