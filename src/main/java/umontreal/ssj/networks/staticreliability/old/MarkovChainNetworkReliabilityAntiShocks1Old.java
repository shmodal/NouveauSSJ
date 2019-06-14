package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.networks.staticreliability.ShockList;
import umontreal.ssj.rng.*;
import java.util.*;

/**
 * Implements one step of the Markov chain for the schema with
 * anti-shocks, using the anti-rates mu_j = -ln(1 - exp(-lambda_j)).
 * 
 * @author Richard simard
 * @see MarkovChainNetworkReliabilityShocksOld
 * @since novembre 2013
 */
public class MarkovChainNetworkReliabilityAntiShocks1Old extends MarkovChainNetworkReliabilityShocksOld 
      implements Cloneable {
   private double[] m_rate0;       // original lambdas

   /**
    * @param forest
    * @param streamPermut
    *           random stream for permutation of links
    * @param sam
    *           type of random sampling for weights
    * @param shocks
    *           all the shocks
    */
   public MarkovChainNetworkReliabilityAntiShocks1Old(GraphWithForest forest, RandomStream streamPermut,
         SamplerType sam, ShockList shocks) {
      super(forest, streamPermut, sam, shocks);
      if (!(forest instanceof GraphWithForestAntiShocks))
         throw new IllegalArgumentException
            ("forest must be an instance of ForestAntiShocks");

      initMu (shocks.getRates());
      shocks.setAntiConnect(true);
   }

   /**
    * Do one step of the Markov chain. Visit all the anti-shocks and sample each
    * shock conditionally on the system staying non-operational. Here, we use
    * random scan Gibbs sampling, doing a random permutation of the anti-shocks
    * before visiting them one by one.
    * 
    * @param stream
    *           to generate a random "time" for each anti-shock
    */
   @Override
   public void nextStep(RandomStream stream) {
      int[] tab = new int[kappa]; // permuted shock indices
      // create a random permutation of {1, 2, ..., kappa}
      streamPermut.resetNextSubstream();
      RandomPermutation.init(tab, kappa);
      RandomPermutation.shuffle(tab, streamPermut); // permute the shocks

      for (int i = 0; i < kappa; i++) {
         int j = tab[i] - 1; // j in {0, 1, ..., kappa-1}
         double oldwei = ((GraphWithForestShocks) forest).getShockWeight(j); // old weight
         if (oldwei > m_gamma)
            conditionalSample(stream, j);
         else
            sampleOneShock(stream, j, 0);
         double newwei = ((GraphWithForestShocks) forest).getShockWeight(j); // new weight

         if (oldwei < m_gamma) {
            if (newwei > m_gamma) {
               // links in this shock becomes non-operational; remove them
               ((GraphWithForestShocks) forest).failLinksOfShock(j);
            } else {
            //   ((ForestShocks) forest).repairLinksOfShock(j);
            }

         } else {
            if (newwei < m_gamma) {
               // this shock is repaired; its links may become operational
               // if no other shock has them failed; check them
               ((GraphWithForestShocks) forest).repairLinksOfShock(j);
            }
         }
      }
   }

   /**
    * Sample one shock. Choose a = 0 to sample over the complete range,
    * and a = gamma to sample over a restricted range.
    */
   @Override
   protected void sampleOneShock(RandomStream stream, int j, double a) {
      // a is either 0, or threshold gamma
      ((GraphWithForestAntiShocks) forest).sampleShockTime(stream, j, a);
   }

   @Override
   protected void conditionalSample(RandomStream stream, int s) {
      Set<Integer> choc = shocks.getShock(s);
      boolean flag;

      // flag is true if network becomes operational by repairing shock s
      flag = ((GraphWithForestAntiShocks) forest).wouldBeConnected2(choc);

      if (flag)
         sampleOneShock(stream, s, m_gamma);
      else
         sampleOneShock(stream, s, 0);
   }

   /**
    * Given a network, computes the importance function.
    * 
    * @return value of importance function
    */
   @Override
   public double getImportance() {
      GraphWithForestAntiShocks image = ((GraphWithForestAntiShocks) forest).clone();
      double[] h = image.getRepairTime();
      return h[0];
   }

   /**
    * Sets the gamma level. *** <i>IMPORTANT</i> *** This method updates the
    * forest with respect to the new gamma.
    * 
    * @param gamma gamma level
    */
   @Override
   public void setGammaLevel(double gamma) {
      m_gamma = gamma;
      forest.update(gamma);
   }

   @Override
   public MarkovChainNetworkReliabilityAntiShocks1Old clone() {
      MarkovChainNetworkReliabilityAntiShocks1Old image =
         (MarkovChainNetworkReliabilityAntiShocks1Old) super.clone();
      image.m_rate0 = m_rate0;   // no need to clone as they never change
      return image;
   }

   
   /**
    * Initializes the anti-rates mu_j = -ln(1 - exp(-lambda_j)).
    * @param rate lambda
    */
   private void initMu(double[] rate) {
      m_rate0 = new double[kappa];
      System.arraycopy(rate, 0, m_rate0, 0, kappa);
      
      double[] mu = new double[kappa];
      for (int j = 0; j < kappa; j++)
         mu[j] = -Math.log(-Math.expm1(-rate[j]));
      
      shocks.setRates (mu);
   }

}
