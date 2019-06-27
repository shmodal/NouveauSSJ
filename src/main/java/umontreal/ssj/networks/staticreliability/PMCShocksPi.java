package umontreal.ssj.networks.staticreliability;

import umontreal.ssj.rng.*;
import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.probdist.*;
import java.util.*;

/**
 * This class implements the Permutation Monte Carlo method with shocks to estimate the
 * unreliability of a network. The discrete probabilities are computed at the
 * beginning before any run is launched. We sample by rejection, generating
 * only shocks as needed; when a shock has been sampled, we remove it from further
 * sampling. In this class, we do not generate the shock times, only the
 * permutation.
 *
 * @author Richard Simard
 * @see PMCShocks
 * @since janvier 2014
 */
public class PMCShocksPi extends PMCShocks {
	   /**
	    * Set of shocks indices.
	    */
   protected int[] Lset; // set of shocks indices
   /**
    * Probabilities of shocks.
    */
   protected double[] prob; // probabilities of shocks
   /**
    * Distribution of shocks rates.
    */
   protected DiscreteDistribution dist; // distribution of shocks rates

   /**
    * Constructor.
    *
    * @param graph
    *           parent graph
    * @param forest
    *           type must be ForestShocks
    * @param shocks
    *           shocks list
    */
   public PMCShocksPi(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
      Lset = new int[kappa];
      prob = new double[kappa];
      init1();
   }

   /**
    * Set up before simulation.
    */
   protected void init1() {
      double x = initLamShock();

      // At first, all shocks are in set L and can be sampled
      for (int j = 0; j < kappa; j++) {
         Lset[j] = j;
         prob[j] = rates[j] / x;
      }
      dist = new DiscreteDistribution(Lset, prob, kappa);
   }

   
   /**
    * Reinit Lset before each run; any shock can be sampled.
    */
   protected void initRun() {
      // all shocks are in set L
      for (int j = 0; j < kappa; j++) {
         Lset[j] = j;
      }
   }

   @Override
   protected double doOneRun(RandomStream stream) {
      initRun();
      int k = -1;
      if (antiScanFlag) {
         throw new UnsupportedOperationException("  UNFINISHED");
         
      } else {
         ((GraphWithForestShocks) forest).initForestShocksNotWeights();
         k = getCriticalShockAndRates (stream);  // critical shock k
      }
      criticalLink.add(k); // critical shock k, count from 1, 2,...
      double ell = computeCDF(Lam, k); // count k from 0 in arrays
      return ell;
   }

   /**
    * The forest has been initialized to all working links and no shock
    * has hit yet. Apply shocks at random until failure. Updates compound rate 
    * Lambda with each shock.
    * @return number of shocks until failure
    */

   protected int getCriticalShockAndRates(RandomStream stream) {
      // The forest has been initialized to all working links and no shock
      // has hit yet. Apply shocks at random until failure.
      double u;
      int c; // shock index
      int k = 0;
      boolean again = true;

      while (again) { // while all nodes in V_0 are connected
         do {
            u = stream.nextDouble();
            c = (int) dist.inverseF(u);
            // if s = Lset[c] < 0, reject shock; already sampled before
         } while (Lset[c] < 0);
         
         ((GraphWithForestShocks) forest).failLinksOfShock(c);
            
         ++k;
         updateRate(k, c);
         again = forest.isConnected();
      }
      return k;
   }


   /**
    * Update compound rate Lambda[k] with shock rate of c. Remove shock c from
    * future sampling by setting Lset[c] = -1.
    *
    * @param k
    *           index of compound rate
    * @param c
    *           shock index
    */
   protected void updateRate(int k, int c) {
      Lam[k] = Lam[k - 1] - rates[c];
      Lset[c] = -1;
   }

 /* 

   protected int getCriticalShock(RandomStream stream) {
      // The forest has been initialized to all failed links and all shocks
      // have struck. Remove shocks at random until network is operational.
      double u;
      int c; // shock index
      int k = kappa - 1;
      do { // while all nodes in V_0 NOT connected
         do {
            u = stream.nextDouble();
            c = (int) dist.inverseF(u);
            // if s = Lset[c] < 0, disregard shock; already sampled before
         } while (Lset[c] < 0);

         ((ForestShocks) forest).repairLinksOfShock(c);
         --k;
      } while(!forest.isConnected());
      return k;
   }*/
}
