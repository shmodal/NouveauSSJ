package umontreal.ssj.networks.flow.nouv;

import java.io.IOException;
import umontreal.ssj.networks.*;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testGraphLattice {

	  public static void main(String[] args) throws IOException {

		  //GraphFlow g = buildLattice();
	      GraphFlow g = buildLatticeComplete();
	      //GraphFlow g2 = buildDodecaComplete();
	      g.setSource(0);
	      g.setTarget(15);
	      //g2.setSource(0);
	      //g2.setTarget(19);
	      
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
	      
	      double eps = 1.0e-5;
	      
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
	   
	   
	   public static GraphFlow buildLatticeComplete() {
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
		      
		      
		      
		      
		      g.addLink(new LinkFlow(20,13,7));
		      g.addLink(new LinkFlow(21,12,6));
		      g.addLink(new LinkFlow(22,17,11));
		      g.addLink(new LinkFlow(23,19,16));
		      g.addLink(new LinkFlow(24,18,14));
		      
		      g.addLink(new LinkFlow(25,2,0));
		      g.addLink(new LinkFlow(26,5,1));
		      g.addLink(new LinkFlow(27,10,4));
		      g.addLink(new LinkFlow(28,15,9));
		      g.addLink(new LinkFlow(29,8,3));
		      
	
		      return g;
	   }
	   
	   private static GraphFlow buildDodecaComplete() {
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
		      
		      
		      
		      
		      g.addLink(new LinkFlow(20,13,7));
		      g.addLink(new LinkFlow(21,12,6));
		      g.addLink(new LinkFlow(22,17,11));
		      g.addLink(new LinkFlow(23,19,16));
		      g.addLink(new LinkFlow(24,18,14));
		      
		      g.addLink(new LinkFlow(25,2,0));
		      g.addLink(new LinkFlow(26,5,1));
		      g.addLink(new LinkFlow(27,10,4));
		      g.addLink(new LinkFlow(28,15,9));
		      g.addLink(new LinkFlow(29,8,3));
		      
	
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      
		      g.addLink(new LinkFlow(30,1,0));
		      g.addLink(new LinkFlow(31,4,1));
		      g.addLink(new LinkFlow(32,9,4));
		      g.addLink(new LinkFlow(33,3,9));
		      g.addLink(new LinkFlow(34,0,3));
		      
		      
		      g.addLink(new LinkFlow(35,6,2));
		      g.addLink(new LinkFlow(36,5,6));
		      g.addLink(new LinkFlow(37,11,5));
		      g.addLink(new LinkFlow(38,10,11));
		      g.addLink(new LinkFlow(39,16,10));
		      g.addLink(new LinkFlow(40,15,16));
		      g.addLink(new LinkFlow(41,14,15));
		      g.addLink(new LinkFlow(42,8,14));
		      g.addLink(new LinkFlow(43,7,8));
		      g.addLink(new LinkFlow(44,2,7));
		      
		      g.addLink(new LinkFlow(45,12,13));
		      g.addLink(new LinkFlow(46,17,12));
		      g.addLink(new LinkFlow(47,19,17));
		      g.addLink(new LinkFlow(48,18,19));
		      g.addLink(new LinkFlow(49,13,18));
		      
		      
		      
		      
		      g.addLink(new LinkFlow(50,7,13));
		      g.addLink(new LinkFlow(51,6,12));
		      g.addLink(new LinkFlow(52,11,17));
		      g.addLink(new LinkFlow(53,16,19));
		      g.addLink(new LinkFlow(54,14,18));
		      
		      g.addLink(new LinkFlow(55,0,2));
		      g.addLink(new LinkFlow(56,1,5));
		      g.addLink(new LinkFlow(57,4,10));
		      g.addLink(new LinkFlow(58,9,15));
		      g.addLink(new LinkFlow(59,3,8));
		      
		      
		      
		      return g;
	   }
	   
	   
}
