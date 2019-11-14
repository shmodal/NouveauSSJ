package umontreal.ssj.splitting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import umontreal.ssj.networks.flow.ExamplesGraphs;
import umontreal.ssj.networks.flow.GraphFlow;
import umontreal.ssj.networks.flow.MChainBis;
import umontreal.ssj.networks.flow.MChainNew;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class Debug5 {
	
	public static void main(String[] args) throws IOException {
	
		String file= ExamplesGraphs.getGraphfile("lattice2and2");
		   
		   int demand = 4;
		   double previous_gamma;
		   
		   
			GraphFlow g = new GraphFlow(file);
			
			int b = 2; // taille b+1
			int[] cap = new int[3];cap[0]=0;cap[1]=1; cap[2]=4;
			//double[] prob = new double[2] ;prob[0]=0.1;prob[1]=0.9;
			double [] prob = new double[3] ;prob[0]=0.05;prob[1]=0.05;prob[2]=0.9;
			for (int i = 0;i<g.getNumLinks();i++) {
				g.setB(b);
				g.setCapacityValues(cap);
				g.setProbabilityValues(prob);
			}
			
			g.resetCapacities();
			
			RandomStream stream = new LFSR113();
			RandomStream streamP = new MRG31k3p();
			

			MChainBis mother = new MChainBis(g, streamP, demand);
			

			int N=4;
			int s = 2;
			   final int q = N - N/s;
			   LinkedList <MChainBis> list0 = new LinkedList <MChainBis> ();
			   LinkedList <MChainBis> list1 = new LinkedList <MChainBis> ();
			
			   
			   ArrayList <Double> gammaT = new ArrayList <Double> ();
			   double[] tabS = new double[N];
			   double current_gamma = 0.0;
			   gammaT.add (0.0);
			   
			   
			   int i;
			   for (i = 0; i < N; i++) {
				   MChainBis chain0 = mother.clone ();
				   //System.out.println("initialState");
				   chain0.initialState (stream,current_gamma);
				   System.out.println("======================================");
				   chain0.printHash();
				   System.out.println("======================================");
				   //System.out.println("FIN");
				   list0.add (chain0);
			   }
			   
			   
			   
			   

			   //previous_gamma = current_gamma; 
			   current_gamma = getNextGamma (q, tabS, 1.0);
			   //System.out.println("Current gamma " +current_gamma);
			   gammaT.add (current_gamma);
			   
			   
			   
			   for (MChainBis chain: list0) {
				   //chain.setGammaLevel (currentGamma);
				   //System.out.println(p);
				   //System.out.println("Importance à ce moment " +chain.getImportance());
				   
				   chain.updateChainGamma(current_gamma);  //
				   //System.out.println(chain.isImportanceGamma (current_gamma));
				   if (chain.isImportanceGamma (current_gamma)) {
					   list1.add (chain);
					   //
				   }
			   }
			   
			   
			   
			   while (current_gamma < 1.0) {
				   System.out.println();
				  System.out.println("current_gamma" + current_gamma);
				  System.out.println();
				   for (MChainBis chain1: list1) {
					   // select each MarkovChain in the list of old survivors
					   MChainBis newchain = chain1.clone ();
					   for (i = 0; i < s; i++) {
						   newchain.nextStep (stream,current_gamma);
						   //newchain.updateChainGamma(current_gamma);
						   list0.add (newchain);
					   }
				   }


				   list1.clear ();          // don't need list1 anymore
				   i = 0;
				   //System.out.println(tabS.length);
				   //System.out.println(list0.size());
				   //System.out.println("ici");
				   //System.out.println("Gamma quand ça bugge " +current_gamma);
				   for (MChainBis chain: list0) {
					   System.out.println("======================================");
					   chain.printHash();
					   System.out.println("======================================");
					   tabS[i] = chain.getImportance();
					   i++;
				   }
				   previous_gamma = current_gamma;
				   current_gamma = getNextGamma (q, tabS, 1.0);
				   gammaT.add (current_gamma);

				   // update for next gamma level; keep only chains which are 
				   // non-operational at time = next gamma
				   for (MChainBis chain: list0) {
					   //chain.setGammaLevel (currentGamma);
					   chain.updateChainGamma(current_gamma);
					   if (chain.isImportanceGamma (current_gamma)) {
						   //System.out.println("Oui");
						   list1.add (chain);
					   }
				   }
				   list0.clear ();
				   
				   if (list1.size () <= 0) {
					   System.out.println("\n******** ADAM:  0 chain survivor at this level\n");
					   break;
				   }
			   }
			   
			
	}

	   private static double getNextGamma (int q, double[] tab, double lastGammaLevel)
	   {
	      Arrays.sort (tab);
	      double x = 0.5*(tab[q-1] + tab[q]);
	      if (x >= lastGammaLevel)
	      	return lastGammaLevel;
	      return x;
	   }


}


