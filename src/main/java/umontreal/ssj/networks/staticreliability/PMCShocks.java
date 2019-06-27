package umontreal.ssj.networks.staticreliability;

import umontreal.ssj.util.Tools;
import umontreal.ssj.rng.*;
import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.probdist.*;
import umontreal.ssj.util.*;
import java.util.*;

/**
 * This class implements the Permutation Monte Carlo method with shocks to
 * estimate the reliability of a network. A shock corresponds to a list of links
 * that all fail simultaneously with a given probability. The method uses the
 * destructive schema: it assumes that all links are working initially, then the
 * links start failing (by experiencing one shock after another) until the
 * network fails, i.e., the subset of nodes V0 becomes disconnected.<br />
 * 
 * <p>
 * It also uses a reverse scan of the shocks similar to a constructive schema
 * where all the shocks have struck initially. Then the shocks are removed one
 * by one until the network becomes operational.
 * </p>
 * 
 */
public class PMCShocks extends PMC {
   protected ShockList shocks;
   protected int kappa;
   protected double[] rates; // shock rates

   public PMCShocks(GraphReliability graph, GraphWithForest forest,
         ShockList shocks) {
      super(graph, forest);
      if (antiScanFlag) {
         if (!(forest instanceof GraphWithForestAntiShocks))
            throw new IllegalArgumentException(
                  "forest must be an instance of ForestAntiShocks");
      } else {
         if (!(forest instanceof GraphWithForestShocks))
            throw new IllegalArgumentException(
                  "forest must be an instance of ForestShocks");
      }
      this.shocks = shocks;
      shockFlag = true;
      kappa = shocks.getShocks().size();
      rates = shocks.getRates();
      Lam = new double[kappa + 1];
   }

   @Override
   protected double doOneRun(RandomStream stream) {
      // Draw independent shock times
      ((GraphWithForestShocks) forest).sampleShockTimes(stream);
      int b = -1; // critical shock
      int[] ranks; // shock ranks

      if (antiScanFlag) {
         ranks = computeRanks();
         ((GraphWithForestAntiShocks) forest).initForestAntiShocksNotWeights();
         b = getCriticalShock(ranks);

      } else {
         double[] A = new double[kappa];
         double[] res = ((GraphWithForestShocks) forest).getFailTime(A);
         b = (int) (res[1]); // sorted rank of critical shock
         // now find shock indices by sorted weights
         ranks = ((GraphWithForestShocks) forest).findShockRanks(A);
      }

      computeRates(ranks, b);
      criticalLink.add(b + 1); // critical shock; b counts from 0
      double ell = computeCDF(Lam, b + 1);
      return ell;
   }

   protected double computeCDF(double[] Lambda, int b) {
      double[] tLam = trimLam(Lambda, b);
      int flag = getHypoExpKind();
      
      switch (flag) {
      case 0: return HypoExponentialDistEqual.cdf(kappa, b, shocks.getRate(0), 1.0);
      case 1: return HypoExponentialDistQuick.cdf(tLam, 1.0);
      case 2: return HypoExponentialDist.cdf2(tLam, 1.0);
      case 3: return HypoExponentialDist.cdf(tLam, 1.0);
      default: return -1.0;
      }
   }

   /**
    * Computes rates such that Lam(E_i) = sum lambda(shock) over shocks. The
    * shocks are sorted according to their weight.
    * 
    * @param rank
    *           shock ranks
    * @param b
    *           critical shock rank at which network fails
    * @return the compound rates
    */
   protected void computeRates(int[] rank, int b) {
      initLamShock();
      int r; // shock number
      for (int j = 0; j < b; j++) {
         r = rank[j];
         Lam[j + 1] = Lam[j] - shocks.getRate(r);
      }
   }

   /**
    * Sorts the a copy of the shocks weight in ascending order and find the
    * ranks of the shocks weights compared to the original shocks order.
    * 
    * @return the shocks ranks
    */
   protected int[] computeRanks() {
      double[] W = ((GraphWithForestShocks) forest).getShockWeight();
      // kappa = W.length = number of shocks
      double[] A = new double[kappa];
      System.arraycopy(W, 0, A, 0, kappa);
      Arrays.sort(A); // sorted weights A
      // find ranks of sorted shocks
      int[] ranks = ((GraphWithForestShocks) forest).findShockRanks(A);
      return ranks;
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
   protected int getCriticalShock(int[] rank) {
      int s; // current shock

      // Add sorted shocks (from largest time to smallest) until network is
      // repaired
      for (int j = kappa - 1; j >= 0; j--) {
         s = rank[j];
         if (s < 0) // shock already visited before
            continue;
         ((GraphWithForestShocks) forest).repairLinksOfShock(s);
         if (forest.isConnected())
            return j;
      }
      return -1;
   }

   /**
    * Computes the first compound Lambda, which is the sum of all lambdas.
    * 
    * @return the first compound Lambda.
    */
   protected double initLamShock() {
      double x = 0;
      for (int j = 0; j < kappa; j++) {
         x += rates[j]; // Sum all shock rates
         Lam[j] = 0; 
      }
      Lam[0] = x; // first Lambda
      Lam[kappa] = 0;
      return x;
   }

}
