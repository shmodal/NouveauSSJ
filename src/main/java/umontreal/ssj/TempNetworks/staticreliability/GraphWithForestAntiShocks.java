package umontreal.ssj.TempNetworks.staticreliability;

import java.util.*;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.staticreliability.ShockList;

/**
 * Implements forest for the schema with anti shocks.
 * 
 * @author Richard Simard
 * @see GraphWithForestShocks
 * @since novembre 2013
 */
public class GraphWithForestAntiShocks extends GraphWithForestShocks implements Cloneable {

   /**
    * Constructor. Initializes the schema for anti-shocks.
    * 
    * @param father graph
    * @param shocks all shocks
    */
   public GraphWithForestAntiShocks(GraphReliability father, ShockList shocks) {
      super(father, shocks);
    //  initForestAntiShocksNotWeights();
    //  initShockWeights();
   }


   /**
    * Initializes the forest and shocks for the anti-shocks schema, except for
    * the weights (or times) which are unaffected. Initially, all shocks have
    * struck and all links are broken.
    */
   public void initForestAntiShocksNotWeights() {
      initForestNotWeights();
      initShockCounters(shocks);
      setShockState(1);
   }
   
   
   /**
    * Initially, all links are failed. Then the shocks are repaired
    * in "time" order until the network becomes operational.
    * 
    * @return a 2-element array: the time at which the network becomes
    * operational, and the rank of the shock that made it so.
    */
   @Override
   public double[] getRepairTime() {
      initForestAntiShocksNotWeights();
      double[] A = new double[kappa];
      System.arraycopy(shockWeight, 0, A, 0, kappa); // Copy the weights
      Arrays.sort(A); // and sort the copy
      double[] res = new double[2];
      int s = -1;
      for (int j = 0; j < kappa; j++) {
         s = findShock(A[j]); // find the shock with the given weight
         repairLinksOfShock(s);
         if (isConnected()) {
            res[0] = shockWeight[s];
            res[1] = j;
            return res;
         }
      }
      res[0] = -1.0e300;
      res[1] = -1;
      return res;
   }
   
   
   /**
    * Updates forest for new gamma level. Inserts in the forest the new links
    * that become operational at this level, that is, those for which 
    * W_j < gamma, and for which all the shocks have been
    * repaired.
    * 
    * @param gamma  gamma level
    */
   @Override
   public void update(double gamma) {
      initWeights();
      updateLinksWeight(true);
      for (int s = 0; s < kappa; ++s) {
         if (shockWeight[s] < gamma)
            repairLinksOfShock(s);
      }
   }

   @Override
   public GraphWithForestAntiShocks clone() {
      GraphWithForestAntiShocks image = (GraphWithForestAntiShocks) super.clone();
      return image;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(super.toString());
      return sb.toString();
   }

}
