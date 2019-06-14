package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.rng.*;
import umontreal.ssj.networks.*;
import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.networks.old.LinkOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.probdist.*;
import java.util.*;

/**
 * This class implements the adapted turnip method with links to estimate 
 * the unreliability of a network. The discrete probabilities are computed
 * at the beginning before any run is launched. We sample by rejection; 
 * when a link s has been sampled, we remove it from further sampling.
 * In this class, we do not generate the link repair times, only the
 * permutation.
 *
 * @author Richard Simard
 * @see Turnip
 * @since septembre 2013
 */
public class TurnipPi extends PMC {
	   /**
	    * Flags for links indices.
	    */
   private int[] Lset; // flags for links indices
   /**
    * Probabilities of links.
    */
   private double[] prob; // probabilities of links
   /**
    * Distribution of links rates.
    */
   private DiscreteDistribution dist; // distribution of links rates
   /**
    * Number of links in graph.
    */
   private int m;   // number of links in graph

   /**
    * Constructor.
    * @param graph
    *           parent graph
    * @param forest
    *           forest
    */
   public TurnipPi(GraphOld graph, GraphWithForest forest) {
      super(graph, forest);
      m = father.getNumLinks();
      Lset = new int[m];
      prob = new double[m];
      init1();
   }

   
   /**
    * Set up before the simulation is run.
    * Computes the first compound Lambda and stores it in Lam.
    * At first, all links are in set L and can be sampled.
    */
   private void init1() {
      double x = 0;
      for (int j = 0; j < m; j++) {
         x += father.getLink(j).getParam();   // sum all link rates
      }
      Lam[0] = x;

      // At first, all links are in set L and can be sampled
      for (int j = 0; j < m; j++) {
         Lset[j] = j;
         prob[j] = father.getLink(j).getParam() / x;
      }
      dist = new DiscreteDistribution(Lset, prob, m);
   }

   
   /**
    * Init Lset before each run so that any link can be sampled at first.
    */
   private void initRun() {
      // all links are in set L
      for (int j = 0; j < m; j++) {
         Lset[j] = j;
      }
   }

   
   @Override
   protected double doOneRun(RandomStream stream) {
      forest.initForestNotWeights();
      initRun();
      double u;
      int c;   // link index
      int k = 0;
      boolean again = true;
  //    System.out.print("perm = { ");
      while (again) { // while nodes in V_0 are NOT all connected
         do {
            u = stream.nextDouble();
            c = (int) dist.inverseF(u);
            // if s = Lset[c] < 0, reject link; already sampled before
         } while (Lset[c] < 0);
         
         forest.setOperational(c, true);
         forest.insertLink(father.getLink(c));
         ++k;
         updateRate(k, c);
         again = !forest.isConnected();
      }
   //   System.out.println(" }");
      criticalLink.add(k); // critical link k, count from 1, 2,...
      double ell = computeBarF (Lam, k);  // count k from 0 in arrays
      return ell;
   }

 

   /**
    * Update compound rates such that Lam(E_i) = sum lambda(link) over links that
    * are redundant. The new rates are put in Lam. Remove link c from
    * future sampling by setting Lset[c] = -1.
    * @param k index of compound Lam
    * @param c link index
    */
   private void updateRate (int k, int c)
   {
      LinkOld lien = father.getLink(c);
      double x = lien.getParam();
      Lset[c] = -1;           // set link sampled
      int node1, node2;
      node1 = lien.getSource();
      int tree = forest.getTree(node1);
      int r;

      // search for redundant links connected to tree
      for (int i = 0; i < m; i++) {
         r = Lset[i];    // link r
         if (r < 0)      // link already sampled
            continue;
         lien = father.getLink(r);
         node1 = lien.getSource();
         node2 = lien.getTarget();
         // are the 2 nodes of link r in tree
         if ((tree == forest.getTree(node1)) &&
             (tree == forest.getTree(node2))) {
          //  System.out.print(i + "  ");
            x += lien.getParam();   // add this link lambda to compound Lam
            Lset[i] = -1;           // set link sampled
         }
      }

      Lam[k] = Lam[k-1] - x;
   }

}
