package umontreal.ssj.splitting.old;

import umontreal.ssj.networks.*;
import umontreal.ssj.networks.staticreliability.old.MarkovChainOld;

import java.util.*;

//import umontreal.ssj.hups.KorobovLattice;
//import umontreal.ssj.hups.PointSet;
//import umontreal.ssj.hups.PointSetRandomization;
//import umontreal.ssj.hups.RandomShift;
import umontreal.ssj.rng.*;
//import umontreal.ssj.rng.RandomStream;

/**
 * This class implements the ADAM (ADAptative Multilevel splitting) algorithm.
 * This algorithm is used as a pilot run to estimate the gamma levels for the Generalized
 * splitting algorithm.
 * It uses the <b>constructive</b> schema for links: it assumes that all links are 
 * initially in a failed state; then it adds working links until the network
 * becomes operational.
 * The main parameters to run the algorithm are:
 * - a Markov chain
 * - a constant number of Markov chains at the beginning
 * - a splitting factor s
 * - the first gamma level
 * - the target gamma level
 */

public class AdamSplittingOld
{
	/**
	 * Array storing the gamma levels.
	 */
   protected double[] m_Gamma;   // gamma levels

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
   public void run (MarkovChainOld mother, int N, int s, RandomStream stream,
   		           double firstGammaLevel, double lastGammaLevel)
   {
   	if (0 != N % s)
   		throw new IllegalArgumentException ("N not multiple of s");
   	if (s < 2 || s >= N)
   		throw new IllegalArgumentException ("must have 1 < s < N");
    	final int q = N - N/s;
      LinkedList <MarkovChainOld> list0 = new LinkedList <MarkovChainOld> ();
      LinkedList <MarkovChainOld> list1 = new LinkedList <MarkovChainOld> ();
      ArrayList <Double> gammaT = new ArrayList <Double> ();
      double[] tabS = new double[N];
 
      mother.setGamma (firstGammaLevel);
      gammaT.add (firstGammaLevel);
      int i;

      for (i = 0; i < N; i++) {
         MarkovChainOld chain0 = mother.clone ();
         chain0.initialState (stream);
         list0.add (chain0);
      }

      i = 0;
      for (MarkovChainOld chain: list0) {
         tabS[i++] = chain.getImportance ();
         System.out.println(tabS[i]);
      }
      
      double currentGamma = getNextGamma (q, tabS, lastGammaLevel);
      gammaT.add (currentGamma);

      // select survivors
      for (MarkovChainOld chain: list0) {
         chain.setGammaLevel (currentGamma);
         if (chain.isImportanceGamma (currentGamma)) {
            list1.add (chain);
         }
      }
      list0.clear ();     // don't need list0 anymore

      while (currentGamma < lastGammaLevel) {
         for (MarkovChainOld chain1: list1) {
            // select each MarkovChain in the list of old survivors
            MarkovChainOld newchain = chain1.clone ();
            for (i = 0; i < s; i++) {
               newchain.nextStep (stream);
               list0.add (newchain);
            }
         }
         

         list1.clear ();          // don't need list1 anymore
         i = 0;
         for (MarkovChainOld chain: list0) {
            tabS[i++] = chain.getImportance ();
         }

         currentGamma = getNextGamma (q, tabS, lastGammaLevel);
         gammaT.add (currentGamma);

         // update for next gamma level; keep only chains which are 
         // non-operational at time = next gamma
         for (MarkovChainOld chain: list0) {
            chain.setGammaLevel (currentGamma);
            if (chain.isImportanceGamma (currentGamma)) {
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
