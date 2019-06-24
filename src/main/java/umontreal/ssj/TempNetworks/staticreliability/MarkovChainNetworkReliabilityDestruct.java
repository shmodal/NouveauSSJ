package umontreal.ssj.TempNetworks.staticreliability;




import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.TempNetworks.staticreliability.SamplerType;
import umontreal.ssj.rng.*;

/**
 * Implements one step of the Markov chain for the destructive schema.
 * Remember that the m_gamma kept in the ancestor class is always 
 * 1/(real gamma). This is done so as to use the same splitting classes
 * as in the constructive schema without any modification.
 * 
 * @see MarkovChainNetworkReliability
 */
public class MarkovChainNetworkReliabilityDestruct extends MarkovChainNetworkReliability implements Cloneable
{

   /**
    * @param forest
    * @param streamPermut stream for permutation of links
    * @param sam for random sampling
    */
   public MarkovChainNetworkReliabilityDestruct(GraphWithForest forest, RandomStream streamPermut,
   		SamplerType sam)
   {
   	super(forest, streamPermut, sam);
      father.setSampler(sam, true);
   }

   
   /**
    * Do one step of the Markov chain. Visit all the links and sample each 
    * link conditionally on the system staying non-operational. Here, we use
    * random scan Gibbs sampling, doing a random permutation of the links
    * before visiting them one by one.
    * NOTE: remember that m_gamma = 1/(real gamma).
    * 
    * @param stream to generate a random length for each link
    */
   @Override
   public void nextStep(RandomStream stream,double gamma)
   {
      int numLinks = father.getNumLinks();
      int[] tab = new int[numLinks]; // permuted link indices
      // create a random permutation of {1, 2, ..., numLinks}
      streamPermut.resetNextSubstream();
      RandomPermutation.init(tab, numLinks);
      RandomPermutation.shuffle(tab, streamPermut); // permute the links

      for (int i = 0; i < numLinks; i++) {
         int j = tab[i] - 1;   // j in {0, 1, ..., numLinks-1}
         double oldwei = forest.getWeight(j); // old weight
         if (1.0/oldwei > gamma)
            conditionalSample(stream, j,gamma);
         else
            sampleOneLink(stream, 0, j);
         double newwei = forest.getWeight(j); // new weight

         if (1.0/oldwei < gamma) {
            if (1.0/newwei > gamma) {
               // this link becomes non-operational; remove it
               forest.setOperational(j, false);
               forest.removeLink(father.getLink(j));
            } else {
               // is this necessary?
               forest.setOperational(j, true);
               if (0 == forest.isInForest(j))
                  forest.insertLink(father.getLink(j));
            }

         } else {
            if (1.0/newwei < gamma) {
               // this link becomes operational; add it in the forest
               forest.setOperational(j, true);
               forest.insertLink(father.getLink(j));
            }
         }
      }
   }

   /**
    * Sample one link. Choose a = 0 to sample over the complete range;
    * and a = real_gamma to sample over a restricted range.
    */
   @Override
   protected void sampleOneLink(RandomStream stream, double a, int j)
   {
      // a is either 0, or threshold real gamma
      double len = father.getLink(j).sampleDestruct(stream, a);
      forest.setWeight(j, len);
   }
   
   
   @Override
   protected void conditionalSample(RandomStream stream, int j,double gamma)
   {
	  LinkReliability link = father.getLink(j);
      // Get the 2 nodes of link j
      int a = link.getSource();
      int b = link.getTarget();
      if (forest.isConnected(a, b)) {
      	// The 2 nodes of link j belong to the same tree
      	sampleOneLink(stream, 0, j);
      	return;
      }
      
      // flag is true if network would become operational by adding link j
      boolean flag = true;
      int[] V = father.getV0();
      for (int i = 0; i < V.length; i++) {
      	if (!forest.isConnected(a, V[i]) &&
      	    !forest.isConnected(b, V[i])) {
      		flag = false;
      		break;
      	}
      }
      
      if (flag)
         sampleOneLink(stream, 1.0/gamma, j);
      else
         sampleOneLink(stream, 0, j);
   }
   

   /**
    * Given a network, computes the importance function.
    * 
    * @return 1 / (value of importance function).
    */
   @Override
   public double getImportance() {
      GraphWithForest image = forest.clone();
      double[] h = ((GraphWithForestDestruct)image).getFailTime();
      return 1.0/h[0];
   }


   /**
    * Sets the gamma level. *** <i>IMPORTANT</i> *** This method updates the
    * forest with respect to the new 1/gamma.
    * 
    * @param gamma 1/(real gamma level)
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
   public MarkovChainNetworkReliabilityDestruct clone()
   {
      MarkovChainNetworkReliabilityDestruct image = (MarkovChainNetworkReliabilityDestruct) super.clone();
      return image;
   }

}
