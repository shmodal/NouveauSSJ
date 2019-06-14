package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.rng.*;
//import graph.*;
//import sampling.*;

/**
 * Abstract class, extended by Connectivity State and al. 
 * Implements one step of the Markov chain.
 * Implements methods to set the gamma level and update the forest and links according to it.
 * 
 */
public abstract class MarkovChainOld implements Cloneable {
   protected GraphWithForest forest;
   protected GraphOld father; // same father as in forest
   /**
    * Used for random permutation of links
    * 
    */
   protected RandomStream streamPermut; // for random permutations of links
   protected double m_gamma; // gamma level

   /**
    * @param forest
    * @param streamPermut
    *           stream for permutation of links
    * @param sam
    *           sampler flag, = 0 (Uniform), = 1 (Exponential), = 2 (Normal)
    */
   public MarkovChainOld(GraphWithForest forest, RandomStream streamPermut, SamplerType sam) {
      this.forest = forest;
      this.father = forest.getFather();
      this.streamPermut = streamPermut;
      m_gamma = 0.;
   }

   /**
    * Initializes the Markov chain, i.e. does one step of the Markov chain.
    */
   public void initialState(RandomStream stream) {
      nextStep(stream);
   }

   /**
    * Sets the gamma level. *** <i>IMPORTANT</i> *** This method updates the
    * forest with respect to the new gamma.
    * 
    * @param gamma
    */
   public void setGammaLevel(double gamma) {
      setGamma(gamma);
      forest.update(gamma);
   }

   /**
    * Sets the gamma level.
    * 
    * @param gamma
    */
   public void setGamma(double gamma) {
      this.m_gamma = gamma;
   }

   /**
    * @return the value of gamma
    */
   public double getGamma() {
      return m_gamma;
   }

   /**
    * @return the forest associated to this chain
    */
   public GraphWithForest getForest() {
      return forest;
   }

   /**
    * Do one step of the Markov chain.
    * 
    * @param stream
    *           of U(0,1) random numbers
    */
   public abstract void nextStep(RandomStream stream);

   @Override
   public MarkovChainOld clone() {
      MarkovChainOld image = null;
      try {
         image = (MarkovChainOld) super.clone();
      } catch (CloneNotSupportedException e) {
      }

      image.forest = forest.clone();
      image.father = forest.getFather();
      image.streamPermut = streamPermut;
      image.m_gamma = m_gamma;

      return image;
   }

   /**
    * Tests if the Markov Chain is above the given gamma level.
    * 
    * @param gam
    *           gamma level
    * @return true if importance function is above gamma
    */
   public boolean isImportanceGamma(double gam) {
      return (!forest.isConnected());
   }

   /**
    * Given a network, computes the importance function.
    * 
    * @return the value of the importance function.
    */
   public double getImportance() {
      GraphWithForest image = forest.clone();
      double[] h = image.getRepairTime();
      return h[0];
   }

}
