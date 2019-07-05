package umontreal.ssj.networks.flow.nouv;

import java.io.IOException;
import umontreal.ssj.networks.*;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testGraphLattice {

	  public static void main(String[] args) throws IOException {

		  GraphFlow g = buildLattice();
	      //GraphFlow g = buildLattice2();
	      g.setSource(0);
	      g.setTarget(15);
	      
	      //System.out.println(g.toString());
	      //MaxFlowEdmondsKarp EK=new MaxFlowEdmondsKarp(g);
	      //EK.EdmondsKarp();
	      //System.out.println(EK.maxFlowValue);
	      //System.out.println(g.residual().toString());
	      //System.out.println(EK.EdmondsKarp());
	      
	      //String filename = TestParams.getGraphfile("1-diamond"); 
	      //System.out.println("Peut etre");
	      //GraphFlow graph = new GraphFlow(filename);
	      
	      
	      
	      int m0 = g.getNumLinks();
	      int[] tab = new int[m0];
	      for (int i = 0; i<m0;i++) {
	    	  tab[i] = 8;
	      }
	      
	      PMC p = new PMC(g);
	      RandomStream stream = new LFSR113();
	      //RandomStream stream = new F2NL607();
	      
	      double eps = 1.0e-8;
	      
	      //0.0001
	      
	      //double prob = p.testRun(stream, 1000000, false, tab, 0.6, eps);
	      //System.out.println("Proba" + prob);
	      
	       double nrun = 5* 1.0e4;
	       nrun = 1;
	       
	       nrun = 50000;
	      //printTab(g.getLink(1).getCapacityValues());
	       
	       
	       p.run(50000,stream,10, false, tab, 0.6, eps);
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
	  
	   private static GraphFlow buildLattice() {
		   GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<16;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      
		      int m = 0;
		      for (int i = 0; i<3;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 4; i<7;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 8; i<11;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 12; i<15;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      
		      
		      for (int i = 0; i<12;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      
		      for (int i = 1; i<13;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      for (int i = 2; i<14;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      for (int i = 3; i<15;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      return g;
	   }
	   
	   
	   public GraphFlow buildLatticeComplete() {
		      GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<16;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      
		      int m = 0;
		      for (int i = 0; i<3;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 4; i<7;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 8; i<11;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      for (int i = 12; i<15;i++) {
		    	  g.addLink(new LinkFlow(m,i,i+1));
		    	  m++;
		      }
		      
		      
		      for (int i = 0; i<12;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      
		      for (int i = 1; i<13;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      for (int i = 2; i<14;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }
		      for (int i = 3; i<15;i+=4) {
		    	  g.addLink(new LinkFlow(m,i,i+4));
		    	  m++;
		      }   
		      for (int i = 3; i>0;i--) {
		    	  g.addLink(new LinkFlow(m,i,i-1));
		    	  m++;
		      }
		      for (int i = 7; i>4;i--) {
		    	  g.addLink(new LinkFlow(m,i,i-1));
		    	  m++;
		      }
		      for (int i = 11; i>8;i--) {
		    	  g.addLink(new LinkFlow(m,i,i-1));
		    	  m++;
		      }
		      for (int i = 15; i>12;i--) {
		    	  g.addLink(new LinkFlow(m,i,i-1));
		    	  m++;
		      }
		      
		      
		      for (int i = 12; i>0;i-=4) {
		    	  g.addLink(new LinkFlow(m,i,i-4));
		    	  m++;
		      }
		      
		      for (int i = 13; i>1;i-=4) {
		    	  g.addLink(new LinkFlow(m,i,i-4));
		    	  m++;
		      }
		      for (int i = 14; i>2;i-=4) {
		    	  g.addLink(new LinkFlow(m,i,i-4));
		    	  m++;
		      }
		      for (int i = 15; i>3;i-=4) {
		    	  g.addLink(new LinkFlow(m,i,i-4));
		    	  m++;
		      }
		      return g;
	   }
	  
	   
	   
	   private static GraphFlow buildLattice2() {
		      GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<4;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      g.addLink(new LinkFlow(0,0,1));
		      g.addLink(new LinkFlow(1,2,3));
		      g.addLink(new LinkFlow(2,0,2));
		      g.addLink(new LinkFlow(3,1,3));
		      return g;
		   
	   }
}
