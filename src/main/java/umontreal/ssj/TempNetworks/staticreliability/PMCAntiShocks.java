package umontreal.ssj.TempNetworks.staticreliability;

//import graph.*;
//import util.*;
import umontreal.ssj.rng.*;
import java.util.*;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.util.Tools;

/**
 * This class implements the PMC algorithm with anti-shocks to
 * estimate the unreliability of a network. We generate all anti-shock times
 * and then sort them in time order. Then we scan the shocks,
 * assuming all shocks have struck and the network is down; we repair the
 * shocks until the network becomes operational. 
 *
 * @since janvier 2014
 * @author Richard Simard
 * @see PMCShocks
 */
public class PMCAntiShocks extends PMCShocks {
   private double[] m_rate0;       // original lambdas

   /**
    * @param graph
    *           parent graph
    * @param forest
    *           type must be ForestAntiShocks
    * @param shocks
    *           shocks list
    */
   public PMCAntiShocks(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
      initMu (shocks.getRates());
   }


   private void initMu(double[] rate) {
      m_rate0 = new double[kappa];
      System.arraycopy(rate, 0, m_rate0, 0, kappa);
      
      double[] mu = new double[kappa];
      for (int j = 0; j < kappa; j++)
         mu[j] = -Math.log(-Math.expm1(-rate[j]));

      shocks.setRates (mu);
   }

   
   @Override
   protected double doOneRun(RandomStream stream) {
      initLamShock();
      ((GraphWithForestAntiShocks) forest).sampleShockTimes(stream); // random shock times
      int[] ranks = computeRanks();
      ((GraphWithForestAntiShocks) forest).initForestAntiShocksNotWeights();

      int b = getCriticalShock(ranks); // critical shock
      computeRates(ranks, b);
      criticalLink.add(b + 1); // critical shock; b counts from 0 in program
      double ell = computeBarF(Lam, b+1);
      return ell;
   }
   

   /**
    * Computes the rank of the critical shock for which the network becomes
    * operational. Shock weights (or times) have been sampled randomly; the
    * forest has been initialized to all broken links and all shocks on. Repair
    * the shocks in reverse order of sorted weights until network is repaired.
    * 
    * @return rank of critical shock which repairs the network
    * @see getCriticalShockAndRates
    */
   @Override
   protected int getCriticalShock(int[] rank) {
      int s; // current shock

      // Add sorted shocks (from largest time to smallest) until network is
      // repaired
      for (int j = 0; j < kappa; ++j) {
         s = rank[j];
         ((GraphWithForestShocks) forest).repairLinksOfShock(s);
         if (forest.isConnected())
            return j;
      }
      return -1;
   }
 
}
