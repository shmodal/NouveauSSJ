package umontreal.ssj.networks.staticreliability.old;

import umontreal.ssj.networks.old.LinkOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.rng.*;

//import graph.*;
//import sampling.*;

/**
 * Extends the class MarkoChain and implements the methods referring to the 
 * conditional sampling, for the constructive scheme.
 * At each step every link weight is resampled conditionally.
 *
 */
public class MarkovChainNetworkReliabilityOld extends MarkovChainOld implements Cloneable
{
   /**
    * @param forest
    * @param streamPermut stream for permutation of links
    * @param sam for random sampling
    */
   public MarkovChainNetworkReliabilityOld(GraphWithForest forest, RandomStream streamPermut,
   		SamplerType sam)
   {
   	super(forest, streamPermut, sam);
   	father.setSampler(sam, false);
   }


   /**
    * Do one step of the Markov chain. Visit all the links and sample each
    * link conditionally on the system staying non-operational. Here, we use
    * random scan Gibbs sampling, doing a random permutation of the links
    * before visiting them one by one.
    *
    * @param stream to generate a random length for each link
    */
   @Override
   public void nextStep(RandomStream stream)
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
         if (oldwei > m_gamma)
         	conditionalSample(stream, j);
         else
         	sampleOneLink(stream, 0, j);
         double newwei = forest.getWeight(j); // new weight

         if (oldwei < m_gamma) {
            if (newwei > m_gamma) {
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
            if (newwei < m_gamma) {
               // this link becomes operational; add it
               forest.setOperational(j, true);
               forest.insertLink(father.getLink(j));
            }
         }
      }
   }

   protected void sampleOneLink(RandomStream stream, double a, int j)
   {
      // a is either 0, -inf, or threshold gamma
      double len = father.getLink(j).sample(stream, a);
      forest.setWeight(j, len);
   }

   /**
    * Sample link j of graph. When adding link j to the current configuration,
    * if the network becomes operational, then sample link length
    * conditional on Y_j > gamma; otherwise sample from original
    * distribution. This is to make sure that source and target are always
    * disconnected and thus network is never operational.
    */
 /*
    private void conditionalSample0(RandomStream stream, int j)
   {
      Link link = father.getLink(j);
      int a = link.getSource();
      int b = link.getTarget();
      int s = father.getSource();
      int t = father.getTarget();

      // Test if source and target of network can be connected by link j.
      // If source and target are in 2 different trees, and one node of link
      // j is in one of these 2 trees, and the other node of link j is in
      // the other, then setting the repair time of link j to 0 will make
      // source and target connected in one tree. Thus sample conditionnaly
      // to keep the 2 trees disconnected. Otherwise, sample without condition.
      if (forest.isConnected(a, s) && forest.isConnected(b, t)
            || forest.isConnected(a, t) && forest.isConnected(b, s))
         sampleOneLink(stream, m_gamma, j);
      else
         sampleOneLink(stream, 0, j);
   }
 */


   /**
    * Sample link j of graph. When adding link j to the current configuration
    * would make the network becomes operational, then sample link length
    * conditional on Y_j > gamma; otherwise sample from original
    * distribution. This is to make sure that the nodes in subset V0 are
    * always disconnected and thus network is never operational. The network
    * can become operational by adding link j when all the nodes in V0 belong
    * to two trees only, and one node of link j belong to one tree while the
    * other belong to the other tree.
    */
   protected void conditionalSample(RandomStream stream, int j)
   {
      LinkOld link = father.getLink(j);
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
         sampleOneLink(stream, m_gamma, j);
      else
         sampleOneLink(stream, 0, j);
   }

   @Override
   public MarkovChainNetworkReliabilityOld clone()
   {
      MarkovChainNetworkReliabilityOld image = (MarkovChainNetworkReliabilityOld) super.clone();
      return image;
   }

}
