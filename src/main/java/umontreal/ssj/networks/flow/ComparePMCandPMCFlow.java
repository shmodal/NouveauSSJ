package umontreal.ssj.networks.flow;


import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.*;
import umontreal.ssj.networks.staticreliability.turniptest;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class ComparePMCandPMCFlow {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		 //GraphFlow g = buildDodecaNoOr();
		GraphFlow g = ExamplesGraphs.buildDodecaNoOr();
		 RandomStream stream = new LFSR113();
		 //stream = new MRG31k3p();
		 //stream.resetNextSubstream();
		 //stream.resetStartSubstream();
		 int demande =1;
		 g.setSource(0);
		 g.setTarget(19);
		 
		 double q = 0.01;
		 
		 
		 int b = 1;
		 int[] tab = new int[b+1];
		 tab[0] =0; tab[1] =1;
		 double[] prob = new double[b+1];
		 prob[0] =q; prob[1]=1-q;
		 for (int i =0;i<g.getNumLinks();i++) {
			 g.setB(i, b);
			 g.setCapacityValues(i, tab);
			 g.setProbabilityValues(i, prob);
		 }
		 
		 PMCNonOriented p = new PMCNonOriented(g);
		 //p.filter = true;
		 p.run(10000000,stream,demande);
		 
		 stream.resetStartSubstream();
		 turniptest.proc(q,"dodecahedron");
		 
		 
		 
		 

	}
	
	
	
	   private static GraphFlow buildDodeca() {
		   GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<20;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      
		      
		      g.addLink(new LinkFlow(0,0,1));
		      g.addLink(new LinkFlow(1,1,4));
		      g.addLink(new LinkFlow(2,4,9));
		      g.addLink(new LinkFlow(3,9,3));
		      g.addLink(new LinkFlow(4,3,0));
		      
		      
		      g.addLink(new LinkFlow(5,2,6));
		      g.addLink(new LinkFlow(6,6,5));
		      g.addLink(new LinkFlow(7,5,11));
		      g.addLink(new LinkFlow(8,11,10));
		      g.addLink(new LinkFlow(9,10,16));
		      g.addLink(new LinkFlow(10,16,15));
		      g.addLink(new LinkFlow(11,15,14));
		      g.addLink(new LinkFlow(12,14,8));
		      g.addLink(new LinkFlow(13,8,7));
		      g.addLink(new LinkFlow(14,7,2));
		      
		      g.addLink(new LinkFlow(15,13,12));
		      g.addLink(new LinkFlow(16,12,17));
		      g.addLink(new LinkFlow(17,17,19));
		      g.addLink(new LinkFlow(18,19,18));
		      g.addLink(new LinkFlow(19,18,13));
		      
		      
		      
		      
		      g.addLink(new LinkFlow(20,7,13));
		      g.addLink(new LinkFlow(21,6,12));
		      g.addLink(new LinkFlow(22,11,17));
		      g.addLink(new LinkFlow(23,16,19));
		      g.addLink(new LinkFlow(24,14,18));
		      
		      g.addLink(new LinkFlow(25,0,2));
		      g.addLink(new LinkFlow(26,1,5));
		      g.addLink(new LinkFlow(27,4,10));
		      g.addLink(new LinkFlow(28,9,15));
		      g.addLink(new LinkFlow(29,3,8));
		      
	
		      return g;
	   }
	   
	   private static GraphFlow buildDodecaNoOr(){
			GraphFlow g = buildDodeca();
			
			int numLinks = g.getNumLinks();
			//System.out.print("Nombre links" + numLinks);
			
			
			/*Storing the edges that are not present in both ways : source-->target and target-->source */
			LinkedList<Integer> Queue = new LinkedList<Integer>();
			
	  	      for (int i = 0; i < numLinks; i++) {
	   			      Queue.add(i);
	   	      }
	  	    int counterIndiceLink=g.getNumLinks();
	 	    while(!Queue.isEmpty()) {
	 		     /*we pop the first element of the queue*/
	 		     int duplicate=Queue.poll();
	 		     LinkFlow original = g.getLink(duplicate);
	 		     g.addLink(new LinkFlow(counterIndiceLink, original.getTarget(),
	 		        		 original.getSource(), original.getCapacity()));
	 		     g.getLink(counterIndiceLink).setCapacityValues(original.getCapacityValues());
	 		     g.getLink(counterIndiceLink).setProbabilityValues(original.getProbabilityValues());
	 		     g.getLink(counterIndiceLink).setB(original.getB());
	 		     counterIndiceLink++;
	 	      }
			
			return g;
	   }

}
