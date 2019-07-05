package umontreal.ssj.networks.flow.nouv;

import java.io.IOException;
import umontreal.ssj.networks.*;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testGraph {

	  public static void main(String[] args) throws IOException {
	      GraphFlow g=new GraphFlow();
	      
	      g.addNode(new NodeBasic(0));
	      g.addNode(new NodeBasic(1));
	      g.addNode(new NodeBasic(2));
	      g.addNode(new NodeBasic(3));
	      g.addNode(new NodeBasic(4));
	      g.addNode(new NodeBasic(5));
	      
	      g.addLink(new LinkFlow(0,0,1,10));
	      g.addLink(new LinkFlow(1,0,2,8));
	      g.addLink(new LinkFlow(2,1,3,8));
	      g.addLink(new LinkFlow(3,1,2,2));
	      g.addLink(new LinkFlow(4,2,4,7));
	      g.addLink(new LinkFlow(5,3,5,10));
	      g.addLink(new LinkFlow(6,4,5,10));
	      g.addLink(new LinkFlow(7,2,3,6));
	      
	      g.setSource(0);
	      g.setTarget(5);
	      
	      //System.out.println(g.toString());
	      //MaxFlowEdmondsKarp EK=new MaxFlowEdmondsKarp(g);
	      //EK.EdmondsKarp();
	      //System.out.println(EK.maxFlowValue);
	      //System.out.println(g.residual().toString());
	      //System.out.println(EK.EdmondsKarp());
	      
	      //String filename = TestParams.getGraphfile("1-diamond"); 
	      //System.out.println("Peut etre");
	      //GraphFlow graph = new GraphFlow(filename);
	      
	      
	      
	      int m = g.getNumLinks();
	      int[] tab = new int[m];
	      for (int i = 0; i<m;i++) {
	    	  tab[i] = 8;
	      }
	      
	      PMC p = new PMC(g);
	      RandomStream stream = new LFSR113();
	      //RandomStream stream = new F2NL607();
	      
	      double prob = p.testRun(stream, 1000000, false, tab, 0.6, 0.0001);
	      System.out.println("Proba" + prob);
	      
	      
	      printTab(g.getLink(0).getCapacityValues());
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
