package umontreal.ssj.networks.staticreliability;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.LinkReliability;
import umontreal.ssj.rng.*;
import java.util.*;

/**
 * This class implements the adapted turnip algorithm with shocks to
 * estimate the unreliability of a network. We generate all shock times
 * and then sort them in time order.
 *
 * @since août 2013
 * @author Richard Simard
 * @see PMCShocks
 */
public class TurnipShocks extends PMCShocks {

   /**
    * @param graph
    *           parent graph
    * @param forest
    *           type must be ForestShocks
    * @param shocks
    *           shocks list
    */
   public TurnipShocks(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
      initLamShock();
   }


   @Override
   protected double doOneRun(RandomStream stream) {
      ((GraphWithForestShocks) forest).sampleShockTimes(stream);  // random shock times
      int[] ranks = computeRanks();
      
      int b = -1; // critical shock
      if (antiScanFlag) {
         throw new UnsupportedOperationException
             ("   reverse scanning is in another class");
      
      } else {
         ((GraphWithForestShocks) forest).initForestShocksNotWeights();
         b = getCriticalShockAndRates (ranks);  // critical failing shock
      }
      criticalLink.add(b + 1);  // critical shock; b counts from 0 in program
      double ell = computeCDF(Lam, b + 1);
      return ell;
   }


   
   /**
    * Computes the rates of the compounded lambdas until the network fails.
    * Returns the rank of the critical shock (with sorted weights).
    * @return rank of critical shock
    */
   private int getCriticalShockAndRates(int[] rank) {
      // Shock weights (or times) have been sampled randomly; the forest has
      // been initialized to all working links. Apply shocks in sorted order
      // of weights until failure.
      int s;    // current shock
      int k = 0;   // current Lam index

      // Add sorted shocks until network fails
      for (int j = 0; j < kappa; j++) {
         s = rank[j];
         if (s < 0)     // shock already hit before
            continue;
         ((GraphWithForestShocks) forest).failLinksOfShock(s);
         if (!forest.isConnected())
            return k;
         ++k;
         updateRate (k, rank, j);
      }
      return -1;
   }


   /**
    * Update compound rate Lambda[k] with shock rate at rank c and remove
    * shock c from future consideration.
    *
    * @param k index of next compound rate
    * @param rank shocks ranks
    * @param c rank index
    */
   protected void updateRate(int k, int[] rank, int c) {
      int s = rank[c];
      Lam[k] = Lam[k - 1] - rates[s];
      rank[c] = -1;   // remove this shock from consideration
      ((GraphWithForestShocks) forest).removeUnconnectedLinks();
      removeAllFailedShocks(k, rank, c);
   }


   /**
    * Removes shocks whose links are all failed from further consideration.
    * Updates the compound rate <tt>Lam[k]</tt>.
    * @param k index of compound rate
    * @param rank shocks rank
    * @param c shock index
    */
   private void removeAllFailedShocks(int k, int[] rank, int c) {
      boolean flag;
      Set<Integer> choc;
      int s;  // current shock

      for (int j = c + 1; j < kappa; j++) {
         s = rank[j];
         if (s < 0)     // shock already removed before
            continue;
         choc = shocks.getShock(s);
         flag = false;
         for (int i : choc) { // i = link
            // if at least 1 link of this shock is operational, flag = true
            if (forest.isOperational(i)) {
               flag = true;
               break;
            }
         }
         if (!flag) {
            // all links of this shock are failed, remove it.
            Lam[k] -= rates[s];
            rank[j] = -1;
         }
      }
   }
   

   /**
    * Removes shocks, whose links are not connected to the V_0 nodes, from
    * further consideration. 
    * <p>Cette méthode ne marche pas: ERREUR.</p>
    * @param rank rank of sorted shocks          
    */
   private void removeDisconnectedShocks(int[] rank, int r) {
      boolean flag = false;
      Set<Integer> choc;
      LinkReliability lien;
      int node, tree, tree0 = -1;
      int s, co;

      for (int j = 0; j < r; ++j) {
         s = rank[j];
         if (s < 0)
            continue;
         choc = shocks.getShock(s);
         if (choc.size() < 2)
            continue;

         co = 0;
         for (int i : choc) { // i = link
            lien = father.getLink(i);
            node = lien.getSource();
            if (0 == co) {
               tree0 = forest.getTree(node);
               flag = true;
            } else {
               // are all links in same connected component?
               tree = forest.getTree(node);
               flag = flag && (tree0 == tree);
            }
            if (!flag)
               break;
            ++co;
         }
         if (flag) {
            // all links of shock are in the same component, remove it.
            rank[j] = -1;
         }
      }
   }

}
