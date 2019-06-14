package umontreal.ssj.splitting;


import umontreal.ssj.networks.staticreliability.old.MarkovChainOld;

import java.util.*;
import umontreal.ssj.rng.*;

public class SplittingGSAdam {
	/**
	 * Array storing the gamma levels.
	 */
   protected double[] m_Gamma;   // gamma levels
   protected double current_gamma ; // for use in algorithm
   protected double previous_gamma; //idem

   /**
    * Runs the ADAM algorithm and computes the gamma levels.
    *
    * @param mother mother of all Markov chains created by the ADAM Algorithm
    * @param N constant number of Markov chains at the beginning of each level
    * @param s splitting factor
    * @param stream to advance the Markov chains
    * @param firstGammaLevel the first level
    * @param lastGammaLevel the target level
    */

   
   public void run (MarkovChainWithImportance mother, int N, int s, RandomStream stream,
		   double firstGammaLevel, double lastGammaLevel)
   {
	   if (0 != N % s)
		   throw new IllegalArgumentException ("N not multiple of s");
	   if (s < 2 || s >= N)
		   throw new IllegalArgumentException ("must have 1 < s < N");
	   final int q = N - N/s;
	   LinkedList <MarkovChainWithImportance> list0 = new LinkedList <MarkovChainWithImportance> ();
	   LinkedList <MarkovChainWithImportance> list1 = new LinkedList <MarkovChainWithImportance> ();
	   ArrayList <Double> gammaT = new ArrayList <Double> ();
	   double[] tabS = new double[N];

	   current_gamma = firstGammaLevel;
	   gammaT.add (firstGammaLevel);
	   int i;

	   for (i = 0; i < N; i++) {
		   MarkovChainWithImportance chain0 = mother.clone ();
		   //System.out.println("initialState");
		   chain0.initialState (stream,current_gamma);
		   //System.out.println("FIN");
		   list0.add (chain0);
	   }

	   i = 0;
	   for (MarkovChainWithImportance chain: list0) {
		   tabS[i] = chain.getImportance();
		   //System.out.println(i);
		   //System.out.println(tabS[i]);
		   i++;
		   
	   }

	   //previous_gamma = current_gamma; 
	   current_gamma = getNextGamma (q, tabS, lastGammaLevel);
	   gammaT.add (current_gamma);

	   // select survivors
	   for (MarkovChainWithImportance chain: list0) {
		   //chain.setGammaLevel (currentGamma);
		   chain.updateChainGamma(current_gamma);
		   if (chain.isImportanceGamma (current_gamma)) {
			   list1.add (chain);
		   }
	   }
	   list0.clear ();     // don't need list0 anymore

	   while (current_gamma < lastGammaLevel) {
		   for (MarkovChainWithImportance chain1: list1) {
			   // select each MarkovChain in the list of old survivors
			   MarkovChainWithImportance newchain = chain1.clone ();
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
		   for (MarkovChainWithImportance chain: list0) {
			   tabS[i] = chain.getImportance();
			   i++;
		   }
		   previous_gamma = current_gamma;
		   current_gamma = getNextGamma (q, tabS, lastGammaLevel);
		   gammaT.add (current_gamma);

		   // update for next gamma level; keep only chains which are 
		   // non-operational at time = next gamma
		   for (MarkovChainWithImportance chain: list0) {
			   //chain.setGammaLevel (currentGamma);
			   chain.updateChainGamma(current_gamma);
			   if (chain.isImportanceGamma (current_gamma)) {
				   list1.add (chain);
			   }
		   }
		   list0.clear ();

		   if (list1.size () <= 0) {
			   System.out.println("\n******** ADAM:  0 chain survivor at this level\n");
			   break;
		   }
	   }

	   int m = gammaT.size();
	   m_Gamma = new double[m];
	   for (i = 0; i < m; i++)
		   m_Gamma[i] = gammaT.get(i);
   }

   
   
   
   


   /**
    * Returns the next gamma level. It is the element of <tt>tab</tt> such that
    * q elements are <b>larger</b> than this element. The array <tt>tab</tt>
    * contains the value of the importance function of each Markov chain.
    *
    * @param q array index to determine gamma
    * @param tab array containing the values of the importance function 
    * @param lastGammaLevel the target level
    * @return the next gamma level
    */
   
   private double getNextGamma (int q, double[] tab, double lastGammaLevel)
   {
      Arrays.sort (tab);
      double x = 0.5*(tab[q-1] + tab[q]);
      if (x >= lastGammaLevel)
      	return lastGammaLevel;
      return x;
   }

   
   /**
    * Returns the gamma levels computed by the method <tt>run</tt>.
    * @return the gamma levels.
    */
   
   public double[] getGamma ()
   {
      return m_Gamma;
   }

	

}
