
package umontreal.ssj.TempNetworks.staticreliability;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.rng.*;

/**
 * This class implements the simple Monte Carlo method with shocks to estimate
 * the <b>unreliability</b> of a network. It uses the destructive schema, i.e.,
 * it assumes that initially, all links are operational. When a shock occurs,
 * all links affected by this shock fail.
 * 
 * @author Richard Simard
 * @since juillet 2013
 */
public class MonteCarloShocks extends MonteCarlo {
   protected ShockList shocks;
   protected int kappa;   // number of shocks

   
   public MonteCarloShocks(GraphReliability graph, GraphWithForest forest, ShockList shocks) {
      super(graph, forest);
      if (!(forest instanceof GraphWithForestShocks))
         throw new IllegalArgumentException(
            "forest must be an instance of ForestShocks");
      this.shocks = shocks;
      kappa = shocks.getShocks().size();
   }

   @Override
   protected double doOneRun(RandomStream stream) {
      ((GraphWithForestShocks) forest).initForestDestructNotWeights();
      drawConfig(stream);
      if (forest.isConnected())
         return 0.0;
      else
         return 1.0; // unreliability is estimated
   }

   /**
    * Generate a random configuration of the network from the shocks.
    * 
    * @param stream
    *           random stream
    */
   private void drawConfig(RandomStream stream) {
      LinkReliability link;
      double rate, rel;
      
      for (int s = 0; s < kappa; ++s) {  // go through all shocks
         rate = shocks.getRate(s);
         rel = Math.exp(-rate);  // reliability
         if (stream.nextDouble() > rel) {
            // go through all links failed by this shock
            for (int i : shocks.getShock(s)) {
               if (forest.isOperational(i)) {
                  forest.setOperational(i, false);
                  link = father.getLink(i);
                  forest.removeLink(link);
               }
            }
         }
      }
   }

}
