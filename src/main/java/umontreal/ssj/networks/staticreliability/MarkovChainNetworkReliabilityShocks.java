package umontreal.ssj.networks.staticreliability;

import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.networks.staticreliability.ShockList;
import umontreal.ssj.networks.staticreliability.old.GraphWithForestShocks;
import umontreal.ssj.rng.*;
import java.util.*;

/**
 * Implements one step of the Markov chain for the destructive schema with
 * shocks. Remember that the m_gamma kept in the ancestor class is always
 * 1/(real gamma). This is done so as to use the same splitting classes as in
 * the constructive schema without any modification.
 * 
 * @author Richard simard
 * @see MarkovChainNetworkReliabilityDestructOld
 * @since juillet 2013
 */
public class MarkovChainNetworkReliabilityShocks extends MarkovChainNetworkReliabilityDestruct
      implements Cloneable {
   protected ShockList shocks;
   protected int kappa;

   /**
    * @param forest
    * @param streamPermut
    *           random stream for permutation of links
    * @param sam
    *           type of random sampling for weights
    * @param shocks
    *           all the shocks
    */
   public MarkovChainNetworkReliabilityShocks(GraphWithForest forest, RandomStream streamPermut,
         SamplerType sam, ShockList shocks) {
      super(forest, streamPermut, sam);
      
      if (!(forest instanceof GraphWithForestShocks))
         throw new IllegalArgumentException
            ("forest must be an instance of ForestShocks");
      if (sam != SamplerType.EXPONENTIAL)
         throw new IllegalArgumentException
         ("Only SamplerType.EXPONENTIAL is used for now");

      this.shocks = shocks;
      kappa = shocks.getShocks().size();
   }

   /**
    * Do one step of the Markov chain. Visit all the shocks and sample each
    * shock conditionally on the system staying non-operational. Here, we use
    * random scan Gibbs sampling, doing a random permutation of the shocks
    * before visiting them one by one. NOTE: remember that m_gamma = 1/(real
    * gamma).
    * 
    * @param stream
    *           to generate a random "time" for each shock
    */
   @Override
   public void nextStep(RandomStream stream,double gamma) {
      int[] tab = new int[kappa]; // permuted shock indices
      // create a random permutation of {1, 2, ..., kappa}
      streamPermut.resetNextSubstream();
      RandomPermutation.init(tab, kappa);
      RandomPermutation.shuffle(tab, streamPermut); // permute the shocks

      for (int i = 0; i < kappa; i++) {
         int j = tab[i] - 1; // j in {0, 1, ..., kappa-1}
         double oldwei = ((GraphWithForestShocks) forest).getShockWeight(j); // old weight
         if (1.0 / oldwei > gamma)
            conditionalSample(stream, j,gamma);
         else
            sampleOneShock(stream, j, 0);
         double newwei = ((GraphWithForestShocks) forest).getShockWeight(j); // new weight

         if (1.0 / oldwei < gamma) {
            if (1.0 / newwei > gamma) {
               // links in this shock becomes non-operational; remove them
               ((GraphWithForestShocks) forest).failLinksOfShock(j);
            } else {
              // ((ForestShocks) forest).repairLinksOfShock(j);
            }

         } else {
            if (1.0 / newwei < gamma) {
               // this shock is repaired; its links may become operational
               // if no other shock has them failed; check them
               ((GraphWithForestShocks) forest).repairLinksOfShock(j);
            }
         }
      }
   }

   /**
    * Sample one shock. Choose a = 0 to sample over the complete range; and a =
    * real_gamma to sample over a restricted range.
    */
   protected void sampleOneShock(RandomStream stream, int j, double a) {
      // a is either 0, or threshold real gamma
      ((GraphWithForestShocks) forest).sampleShockTime(stream, j, a);
   }

   @Override
   protected void conditionalSample(RandomStream stream, int s,double gamma) {
      Set<Integer> choc = shocks.getShock(s);
      boolean flag;

      // flag is true if network becomes operational by repairing shock s
      /* Forest image = forest.clone();
      flag = ((ForestShocks) image).wouldBeConnected1(choc); */

      flag = ((GraphWithForestShocks) forest).wouldBeConnected2(choc);

      if (flag)
         sampleOneShock(stream, s, 1.0 / gamma);
      else
         sampleOneShock(stream, s, 0);
   }

   /**
    * Given a network, computes the importance function.
    * 
    * @return 1 / (value of importance function).
    */
   @Override
   public double getImportance() {
      GraphWithForest image = forest.clone();
      double[] h = ((GraphWithForestShocks) image).getFailTime();
      return 1.0 / h[0];
   }

   /**
    * Sets the gamma level. *** <i>IMPORTANT</i> *** This method updates the
    * forest with respect to the new 1/gamma.
    * 
    * @param gamma
    *           1/(real gamma level)
    */
   //@Override
	/*
	 * public void setGammaLevel(double gamma) { //m_gamma = gamma;
	 * forest.update(gamma); }
	 */
   
	/*
	 * public void updateChainGamma (double gamma) { forest.update(gamma); }
	 */
   

   @Override
   public MarkovChainNetworkReliabilityShocks clone() {
      MarkovChainNetworkReliabilityShocks image = (MarkovChainNetworkReliabilityShocks) super.clone();
      image.shocks = shocks;
      image.kappa = kappa;
      return image;
   }

}
