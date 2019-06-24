package umontreal.ssj.TempNetworks.staticreliability;


import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.rng.*;
import java.util.*;

/**
 * This class implements the turnip algorithm with reverse scan to estimate the
 * unreliability of a network. We generate all shock times and then sort them in
 * time order. It uses a reverse scan of the shocks similar to a constructive
 * schema where all the shocks have occurred initially. Then the shocks are
 * removed one by one until the network becomes operational.
 * 
 * @since octobre 2013
 * @author Richard Simard
 * @see TurnipShocks
 */
public class TurnipShocksAntiScan extends PMCShocks {
   protected Set<Integer> failedLinks; // set of failed link indices
   protected Set<Integer> linkSet0; // working set of links
   protected int[] mark;   // mark shocks, 0 is unmarked, 1 is marked

   /**
    * @param graph
    *           parent graph
    * @param forest
    *           type must be ForestAntiShocks
    * @param shocks
    *           shocks list
    */
   public TurnipShocksAntiScan(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
      final int m = graph.getNumLinks();
      failedLinks = new HashSet<Integer>(m);
      linkSet0 = new HashSet<Integer>();
      mark = new int[kappa];
   }

   /**
    * Init linkSet before each run; all links are failed
    */
   protected void initRun() {
      // all links are in set L
      final int m = father.getNumLinks();
      for (int j = 0; j < m; j++)
         failedLinks.add(j);
      
      linkSet0.clear(); // empty set
      for (int j = 0; j < kappa; j++)
         mark[j] = 0;
   }


   @Override
   protected double doOneRun(RandomStream stream) {
      throw new UnsupportedOperationException(
      "L'ALGORITHME À LA BASE DE CETTE CLASSE N'EST PAS CORRECT");
    /*  initRun();
      ((ForestAntiShocks) forest).sampleShockTimes(stream); // random shock times
      int[] ranks = computeRanks();
      ((ForestAntiShocks) forest).initForestAntiShocksNotWeights();

      int b = -1; // critical shock
      b = getCriticalShock(ranks);
      b = computeRates2(ranks, b);

      criticalLink.add(b + 1); // critical shock; b counts from 0 in program
      double ell = computeCDF(Lam, b + 1);
      return ell; */
   }
   

   /**
    * Computes the rank of the critical shock for which the network becomes
    * operational. Shock weights (or times) have been sampled randomly; the
    * forest has been initialized to all broken links and all shocks on. Repair
    * the shocks in reverse order of sorted weights until network is repaired.
    * @param rank ranks of sorted shocks
    * @return rank of critical shock
    */
   @Override
   protected int getCriticalShock(int[] rank) {

      // Add sorted shocks (from largest time to smallest) until network is
      // repaired
      int s; // current shock
      int k = kappa - 1; // rank of sorted shock
      while (true) {
         if (!linkSet0.isEmpty()) {
            markShocks (rank);
         }
         while ((mark[rank[k]]) > 0)  // shock already visited before
            --k;
         s = rank[k];
         mark[s] = 1;
         linkSet0.clear();
         ((GraphWithForestAntiShocks) forest).repairLinksOfShock(s, linkSet0, failedLinks);
         if (forest.isConnected())
            return k;
         --k;
      }
   }


   /**
    * Computes rates such that Lam(E_i) = sum lambda(shock) over shocks. The
    * shocks are sorted according to their weight.
    *
    * @param rank
    *            rank of sorted shocks
    * @param b
    *           critical shock rank
    * @return the compound rates
    */
   protected int computeRates2(int[] rank, int b) {
      initLamShock();
      int k = 0;
      int r, s; // shock index
      for (int j = 0; j < b; j++) {
         r = rank[j];
      //   s = rank[k];
         if (mark[r] > 0) {
            Lam[k] -= shocks.getRate(r);
         } else {
            Lam[k+1] = Lam[k] - shocks.getRate(r);
            ++k;
         }
      }
      return k;
   }

   
   /**
    * Marks shocks to 1 to be discarded. 
    * mark[j] = 1 is marked; mark[j] = 0 is unmarked
    * @param rank rank of sorted shocks
    */
   protected void markShocks (int[] rank) {
      Set<Integer> choc = null;
      int r; // shock index;
      
      Set<Integer> links = new HashSet<Integer>(32);  // temporary links
      
      for (int i : linkSet0) {
         for (int j = kappa - 1; j >= 0; j--) {
            r = rank[j];
            if (mark[r] > 0) 
               continue;   // shock is marked already
            choc = shocks.getShock(r);
            if (choc.contains(i)) {
               links.clear();
               links.addAll(choc);
               links.retainAll(failedLinks); // intersection of choc and linkSet
               if (links.isEmpty())
                  mark[r] = 1;  // intersection is empty, mark this shock
               }
            }
         }
      
      links.clear();
      }
      
}
