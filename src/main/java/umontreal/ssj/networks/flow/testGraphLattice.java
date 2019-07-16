package umontreal.ssj.networks.flow;

import java.io.IOException;
import umontreal.ssj.networks.*;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testGraphLattice {

	  public static void main(String[] args) throws IOException {

	     //GraphFlow g = buildLatticeComplete();
	      //GraphFlow g2 = buildDodecaComplete();
	      //g.setSource(0);
	      //g.setTarget(15);
	      //g2.setSource(0);
	      //g2.setTarget(19);
 
	      //GraphFlow g = buildLattice6();
	      //g.setSource(0);
	      //g.setTarget(35);
	      //g.toString();
	      //g2.toString();
	      
	      //int b = 8;
	      //int m0 = g.getNumLinks();
	      //int m0 = g2.getNumLinks();
	      //int[] tab = new int[m0];
	      //for (int i = 0; i<m0;i++) {
	    //	  tab[i] = b;
	     // }
	      
	     // PMC p = new PMC(g);
	      //PMC p = new PMC(g2);
	    
	      //double prob = p.doOneRun(stream,demand, false);
	      //System.out.println("Proba" + prob);

	       
	       
	       PMC pL4 = doLattice44();
	       PMC pL6 = doLattice66();
	       PMC pDo = doDodeca();
		   RandomStream stream = new LFSR113();
		   int b = 4;
		   //int m0 = pL4.father.getNumLinks();
		   //int m0 = pL6.father.getNumLinks();
		   int m0 = pDo.father.getNumLinks();
		   int[] tab = new int[m0];
		   for (int i = 0; i<m0;i++) {
			   tab[i] = b;
		      }
		   double eps = 1.0e-8;
		   double rho = 0.7;
		   int demand = 5;
		   //pL4.initCapaProbaB(tab, rho, eps);
		   //pL4.trimCapacities(demand);
		   //pL6.initCapaProbaB(tab, rho, eps);
		   //pL6.trimCapacities(demand);
		   pDo.initCapaProbaB(tab, rho, eps);
		   pDo.trimCapacities(demand);
		   
	       int nrun = 50000;
	       
	       
	       //pL4.setHypoExpKind(1);

	       //pL4.run(nrun,stream,demand, false);
	       
	       //pL6.setHypoExpKind(1);

	       //pL6.run(nrun,stream,demand, false);
	       pDo.setHypoExpKind(1);

	       pDo.run(nrun,stream,demand, false);
	       
	       
	       
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
	   
	   private static PMC doLattice44() {
		   GraphFlow g = buildLatticeComplete();
		   g.setSource(0);
		   g.setTarget(15);
		   PMC p = new PMC(g);
		   return p;
	   }
	   
	   private static PMC doLattice66() {
		   GraphFlow g = buildLattice6();
		   g.setSource(0);
		   g.setTarget(35);
		   PMC p = new PMC(g);
		   return p;
	   }
	   
	   private static PMC doDodeca() {
		   GraphFlow g = buildDodecaComplete();
		   g.setSource(0);
		   g.setTarget(19);
		   PMC p = new PMC(g);
		   return p;
	   }
	   
	   
	  
	   // build 4*4 lattice Graph (with no full orientation)
	   
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
	   
	// build 4*4 lattice Graph (Full orientation)
	   
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
	  
	   
	   // build Lattice taille 2 (complete)
	   private static GraphFlow buildLattice2() {
		      GraphFlow g=new GraphFlow();
		      
		      
		      for (int i = 0;i<4;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      g.addLink(new LinkFlow(0,0,1));
		      g.addLink(new LinkFlow(1,2,3));
		      g.addLink(new LinkFlow(2,0,2));
		      g.addLink(new LinkFlow(3,1,3));
		      g.addLink(new LinkFlow(4,1,0));
		      g.addLink(new LinkFlow(5,2,0));
		      g.addLink(new LinkFlow(6,3,2));
		      g.addLink(new LinkFlow(7,3,1));
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
	   
	   
	   //build Lattice complete 6*6
	   private static GraphFlow buildLattice6() {
		      GraphFlow g=new GraphFlow();
		      
		      int count = 0; //number of links
		      for (int i = 0;i<36;i++) {
		    	  g.addNode(new NodeBasic(i));
		      }
		      
		      // aretes horizontales
		      
		      for (int i=0; i<6;i++) { //ligne i
		    	  for (int j=0;j<5;j++) {
		    	  g.addLink(new LinkFlow(count,6*i +j, 6*i +j+1));
		    	  g.addLink(new LinkFlow(count+1,6*i +j+1,6*i +j));
		    	  count+=2;
		    	  }
		      }
		      //aretes verticales
		      
		      for (int j=0; j<6;j++) { //ligne i
		    	  for (int i=0;i<5;i++) {
		    	  g.addLink(new LinkFlow(count, 6*i +j, 6*i +j + 6));
		    	  g.addLink(new LinkFlow(count+1,6*i +j+6,6*i +j));
		    	  count+=2;
		    	  }
		      }

		      return g;
	   }
	   
	   
	   
}
