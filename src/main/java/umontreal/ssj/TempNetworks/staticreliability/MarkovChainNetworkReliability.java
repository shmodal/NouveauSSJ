package umontreal.ssj.TempNetworks.staticreliability;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
import umontreal.ssj.TempNetworks.staticreliability.GraphWithForest;
import umontreal.ssj.rng.RandomPermutation;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

/**
 * Extends the class MarkovChainWithImportance. It is to be applied on networks, for the 
 * computation of static reliability.
 * It implements the methods referring to the 
 * importance function, and the conditional sampling of links (for the constructive scheme).
 * At each step every link weight is resampled conditionally.
 *
 */

public class MarkovChainNetworkReliability extends MarkovChainWithImportance {
	   protected GraphWithForest forest;
	   protected GraphReliability father; // same father as in forest
	   protected RandomStream streamPermut; // for random permutations of links
	   
	   
	   /**
	    * @param forest
	    * @param streamPermut
	    *           stream for permutation of links
	    * @param sam
	    *           sampler flag, = 0 (Uniform), = 1 (Exponential), = 2 (Normal)
	    */
	   
	   public MarkovChainNetworkReliability(GraphWithForest forest, RandomStream streamPermut,
		   		SamplerType sam)
		   {
		   	super();
		   	this.streamPermut = streamPermut;
			this.forest = forest;
			this.father = forest.getFather();
		   	father.setSampler(sam, false);
		   }
	    
	   @Override
	   public MarkovChainNetworkReliability clone() {
		  MarkovChainNetworkReliability image = (MarkovChainNetworkReliability) super.clone(); 
		  image.streamPermut = streamPermut;
		  image.forest = forest.clone(); 
		  image.father = forest.getFather();
	      return image;
	   }
	   
	   
	   /**
	    * @return the forest associated to this chain
	    */
	   public GraphWithForest getForest() {
	      return forest;
	   }
	   
	   /**
	    * Updates forest of the chain and adds new links which have become operational. 
	    * 
	    * @param gamma level
	    */
	   
	   //update the Markov chain according to the gamma, puisqu'on ne retient plus gamma
	   // dans la MC
	   @Override
	   public void updateChainGamma (double gamma) {
		   forest.update(gamma);
	   }
	   
	   
	   /**
	    * Tests if the Markov Chain is above the given gamma level.
	    * Rather than computing the importance function( which needs to compute get
	    * RepairTime, less efficient), it looks at the forest to see if it
	    * 
	    * @param gam
	    *           gamma level
	    * @return true if importance function is above gamma
	    */
	   @Override
	   // On ovveride car plus efficace que calculer getRepairTime à chaque fois.
	   // car on travaille sur le même graphe auquel on ajoute/enleve des aretes
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
	   

	   /**
	    * Does one step of the Markov chain. Visit all the links and sample each
	    * link conditionally on the system staying non-operational. Here, we use
	    * random scan Gibbs sampling, doing a random permutation of the links
	    * before visiting them one by one.
	    *
	    * @param stream to generate a random length for each link
	    */
	   @Override
	   public void nextStep(RandomStream stream, double gamma)
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
	         if (oldwei > gamma) { // Modif
	         	conditionalSample(stream, j,gamma);
	         	//System.out.println("ConditionalSample");
	         	}
	         else {
	         	sampleOneLink(stream, 0, j);
	         	//System.out.println("sampleOnelink");
	         	}
	         double newwei = forest.getWeight(j); // new weight}

	         if (oldwei < gamma) {
	            if (newwei > gamma) {
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
	            if (newwei < gamma) {
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
	    * Sample link j of graph. When adding link j to the current configuration
	    * would make the network becomes operational, then sample link length
	    * conditional on Y_j > gamma; otherwise sample from original
	    * distribution. This is to make sure that the nodes in subset V0 are
	    * always disconnected and thus network is never operational. The network
	    * can become operational by adding link j when all the nodes in V0 belong
	    * to two trees only, and one node of link j belong to one tree while the
	    * other belong to the other tree.
	    */
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
	         sampleOneLink(stream, gamma, j);
	      else
	         sampleOneLink(stream, 0, j);
	   }

	@Override
	public double getPerformance() {
		// TODO Auto-generated method stub
		return 0;
	}
	   

	   
	   
}
