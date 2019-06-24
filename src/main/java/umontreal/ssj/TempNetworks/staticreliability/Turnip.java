package umontreal.ssj.TempNetworks.staticreliability;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.rng.*;
import java.util.Arrays;

/**
 * This class implements the turnip method to estimate the unreliability of a
 * network. It uses the constructive schema, i.e., it assumes
 * that initially, all links are failed and then add working links until the
 * network becomes operational, i.e., the subset of nodes V0 becomes connected.
 * In this class, we generate all link repair times.
 * 
 * @see PMC
 */
public class Turnip extends PMC
{
   /** Constructor. creates PMC and then inits Lambda.
   * @param graph
   * @param forest
   */
   public Turnip(GraphReliability graph, GraphWithForest forest)
   {
      super(graph, forest);
      initLam();
   }

   @Override
   protected double doOneRun(RandomStream stream)
   {
      drawRepairTimes (stream);  // Draw independent repair times
      int b = getCriticalLinkAndRates (); // rank of critical link in sorted weights
      criticalLink.add(b + 1); // critical shock; b counts from 0 in program
      double ell = computeBarF(Lam, b + 1);
      return ell;
   }




   /**
    * Computes the rates of the compounded lambdas until the network becomes
    * operational.
    * @return rank of critical link
    */
   private int getCriticalLinkAndRates ()
   {
      forest.initForestNotWeights();
      double[] W = forest.getWeight();
      int m = father.getNumLinks();   // W.length
      double[] A = new double[m];
      System.arraycopy (W, 0, A, 0, m);
      Arrays.sort(A);     // sorted weights
      int[] rank = forest.findLinkIndices (A);  // link ranks
      int s = -1;    // current link
      int co = 0;    // current Lam index
      // Add sorted links until network becomes operational
      for (int j = 0; j < m; j++) {
         s = rank[j];
         if (s < 0)     // link already taken into account before
            continue;
         forest.setOperational(s, true);
         forest.insertLink(father.getLink(s));
         if (forest.isConnected()){
            return co;
         }
         computeRate (j, rank, co);
         co++;
      }
      return -1;
   }

   /**
    * Computes rates such that Lamda(i) = sum lambda(link) over links that
    * are redundant. The new rates are put in Lam
    * @param j loop index of links
    * @param rank ranks of links sorted according to weight
    * @param co index of current Lam
    */
   private void computeRate (int j, int[] rank, int co)
   {
      int s = rank[j];
      LinkReliability lien = father.getLink(s);
      double x = lien.getParam();
      rank[j] = -1;           // set link visited
      int r = lien.getSource();
      int tree = forest.getTree(r);

      // search for redundant links connected to tree
      for (int i = j + 1; i < father.getNumLinks(); i++) {
         r = rank[i];    // link r
         if (r < 0)      // link already taken into account
            continue;
         lien = father.getLink(r);
         int node1 = lien.getSource();
         int node2 = lien.getTarget();
         // are the 2 nodes of link r in tree
         if ((tree == forest.getTree(node1)) &&
               (tree == forest.getTree(node2))) {
            x += lien.getParam();   // add this link lambda to compound Lam
            rank[i] = -1;           // set link visited
         }
      }
      Lam[co + 1] = Lam[co] - x;
   }

   /**
    * Computes the first compound Lambda, which is the sum of all lambdas. Stores it in
    * the array Lam.
    */
   
   
   private void initLam() {
      int m = father.getNumLinks();
      double x = 0;
      for (int j = 0; j < m; j++)
         x += father.getLink(j).getParam();
      Lam[0] = x;
   }

}
