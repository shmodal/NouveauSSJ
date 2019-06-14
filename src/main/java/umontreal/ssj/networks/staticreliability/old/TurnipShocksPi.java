package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.rng.*;
import umontreal.ssj.networks.*;
import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.ShockList;
import umontreal.ssj.probdist.*;
import java.util.*;

/**
 * This class implements the adapted turnip method with shocks to estimate the
 * unreliability of a network. The discrete probabilities are computed at the
 * beginning before any run is launched. We sample by rejection, generating
 * only shocks as needed; when a shock has been sampled, we remove it from further
 * sampling. In this class, we do not generate the shock times, only the
 * permutation.
 *
 * @author Richard Simard
 * @see PMCShocks
 * @since septembre 2013
 */
public class TurnipShocksPi extends PMCShocksPi {
   private boolean failNodesFlag;

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
   public TurnipShocksPi(GraphOld graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest, shocks);
   //   failNodesFlag = true;
   }
   

   @Override
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
         
         if (failNodesFlag) {
            ((GraphWithForestShocks) forest).failLinksNodesOfShock(c);
         } else {
            ((GraphWithForestShocks) forest).failLinksOfShock(c);
         }
            
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
   @Override
   protected void updateRate(int k, int c) {
      Lam[k] = Lam[k - 1] - rates[c];
      Lset[c] = -1;
      if (!failNodesFlag) {
         // removeUnconnectedLinks is called with failLinksOfShock
         // failLinksNodesOfShock calls it own internal removeUnconnectedLinks
         ((GraphWithForestShocks) forest).removeUnconnectedLinks();
      }
      removeAllFailedShocks(k);   
   }

   /**
    * Removes shocks whose links are all failed from further consideration.
    * Updates the compound rate <tt>Lam[k]</tt>.
    *
    * @param k
    *           index of compound rate
    */
   private void removeAllFailedShocks(int k) {
      boolean flag;
      Set<Integer> choc;
      int s;

      for (int j = 0; j < kappa; ++j) {
         s = Lset[j];
         if (s < 0)
            continue;
         choc = shocks.getShock(s);
         flag = false;
         for (int i : choc) { // i = link
            if (forest.isOperational(i)) {
               flag = true;
               break;
            }
         }
         if (!flag) {
            // all links of this shock are failed, remove it.
            Lam[k] -= rates[s];
            Lset[j] = -1;
         }
      }
   }

/*
   private int getCriticalShock(RandomStream stream) {
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
