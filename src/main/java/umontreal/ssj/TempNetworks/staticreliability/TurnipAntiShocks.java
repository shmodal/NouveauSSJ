package umontreal.ssj.TempNetworks.staticreliability;


import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.rng.*;
import java.util.*;

/**
 * This class implements the adapted turnip algorithm with anti-shocks to
 * estimate the unreliability of a network. We generate all anti-shock times and
 * then sort them in time order. Then we scan the shocks, assuming all shocks
 * have struck and the network is down; we repair the shocks until the network
 * becomes operational.
 * 
 * @since octobre 2013
 * @author Richard Simard
 * @see PMCShocks
 */
public class TurnipAntiShocks extends TurnipShocksAntiScan {
   private double[] m_rate0; // original lambdas

   /**
    * @param graph
    *           parent graph
    * @param forest
    *           type must be ForestShocks
    * @param shocks
    *           shocks list
    */
   public TurnipAntiShocks(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
      initMu(shocks.getRates());
   }

   private void initMu(double[] rate) {
      m_rate0 = new double[kappa];
      System.arraycopy(rate, 0, m_rate0, 0, kappa);

      double[] mu = new double[kappa];
      for (int j = 0; j < kappa; j++)
         mu[j] = -Math.log(-Math.expm1(-rate[j]));

      shocks.setRates(mu);
   }

   
   @Override
   protected double doOneRun(RandomStream stream) {
      initLamShock();
      initRun();
      ((GraphWithForestAntiShocks) forest).sampleShockTimes(stream); // random shock times
      int[] ranks = computeRanks();
      ((GraphWithForestAntiShocks) forest).initForestAntiShocksNotWeights();

      int b; // critical shock
      b = getCriticalShock(ranks);
      criticalLink.add(b); // critical shock; b counts from 0 in program
      double ell = computeBarF(Lam, b);
      return ell;
   }

   /**
    * Computes the rank of the critical shock for which the network becomes
    * operational. Shock weights (or times) have been sampled randomly; the
    * forest has been initialized to all broken links and all shocks on. Repair
    * the shocks in order of sorted weights until network is repaired.
    * 
    * @param rank
    *           ranks of sorted shocks
    * @return rank of critical shock
    */
   @Override
   protected int getCriticalShock(int[] rank) {
      // Add sorted anti-shocks (from smallest time to largest) until
      // network is repaired
      int s; // current shock
      int k = 0; // Lambda index
      int kprim = 0;  // loop index

      while (true) {
         if (!linkSet0.isEmpty()) {
            markShocks(rank, kprim, k);
         }
       
         while (mark[rank[kprim]] > 0) { // shock already seen before
            ++kprim;
         }
         s = rank[kprim];
         mark[s] = 1;
         Lam[k + 1] = Lam[k] - shocks.getRate(s);
         linkSet0.clear();
         ((GraphWithForestAntiShocks) forest).repairLinksOfShock(s, linkSet0, failedLinks);
         ++k;
         ++kprim;
         if (forest.isConnected()) {
            return k;
         }
      }
   }

   /**
    * Marks shocks to 1 to be discarded. mark[j] = 1 is marked; mark[j] = 0 is
    * unmarked
    * 
    * @param rank
    *           index of sorted shocks
    * @param kprim        
    * @param k       
    */
  // @Override
   protected void markShocks(int[] rank, final int kprim, final int k) {
      Set<Integer> choc = null;
      Set<Integer> links = new HashSet<Integer>(32); // temporary links
      int r; // shock number;

      for (int i : linkSet0) {  // link i
         for (int j = kprim; j < kappa; ++j) {
            r = rank[j];
            if (mark[r] > 0)
               continue; // shock is marked already
            choc = shocks.getShock(r);
            if (choc.contains(i)) {
               links.clear();
               links.addAll(choc);  // all links in choc
               links.retainAll(failedLinks); // intersection of choc and linkSet
               if (links.isEmpty()) {
                  mark[r] = 1; // intersection is empty, mark this shock
                  Lam[k] -= shocks.getRate(r);
               }
            }
         }
      }

      links.clear();
   }


}
