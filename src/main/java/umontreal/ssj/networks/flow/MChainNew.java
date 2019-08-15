package umontreal.ssj.networks.flow;

import java.util.ArrayList;
import java.util.HashMap;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.MarkovChainNetworkReliability;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.rng.RandomPermutation;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

import umontreal.ssj.splitting.MarkovChainWithImportance;


//creation de la MChain
//clonage

public class MChainNew extends MarkovChainWithImportance {
	
	protected MaxFlowEdmondsKarp Ek;
	public HashMap <Double,int[]> coordinates;
	protected RandomStream streamPermut; // for random permutations of links
	int demand;
	public ArrayList<Double> Yinf;  //retient les Yi,j inférieurs à gamma(t-1)
	public double[] valuesY;  //toutes les valeurs de Y
	
	public void initialState(RandomStream stream, double gamma) {
		   for (int i=0;i<Ek.network.getNumLinks();i++) {
			   Ek.network.initLinkLambda(i);
		   }
	}
	
	
// juste la simulation conditionnelle des Y et leur modification.
	// PB, mettre à jour la structure qui concerne les gamma_t-1 et gamma_t
	//pour le test de re sampling, il faut faire increase cap et decrease cap
	//que faire des new Y?
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		int numY = valuesY.length;
		int[] tab = new int[numY]; //tab contient des indices,
		double [] newvaluesY = new double [numY]; // utile ?
		
		streamPermut.resetNextSubstream();
	    RandomPermutation.init(tab, numY);
	    RandomPermutation.shuffle(tab, streamPermut); // permute the links
	    for (int l = 0; l < numY; l++) {
	    	int j = tab[l] - 1;
	    	double oldY = valuesY[j];
			int [] indices = coordinates.get(oldY);
			int i = indices[0];
			int k = indices[1];
			double newY;
			// test pour savoir si TC<gamma(t-1). on decrease cap dedans aussi
			
			if (testIncreaseCap(i,k)) { //augmenter la capa donne TC<gamma(t-1)
				LinkFlow EdgeI  = Ek.network.getLink(i);
				double lambda = EdgeI.getLambdaValue(k); // VERIFIER le k ou k+1
				newY = gamma +ExponentialDist.inverseF(lambda, stream.nextDouble());
				//MAJ de values Y et coordinates ?	
			}
			else {
				LinkFlow EdgeI  = Ek.network.getLink(i);
				double lambda = EdgeI.getLambdaValue(k);  // VERIFIER le k ou k+1
				newY = ExponentialDist.inverseF(lambda, stream.nextDouble());
				//MAJ de values Y et coordinates ? 
				if (newY<= gamma) {
					int prevCapacity = Ek.network.getLink(i).getCapacity();
					int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
					boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
					if (reload) {
						Ek.EdmondsKarp();
						}
				}	
			}
			coordinates.remove(oldY);
			coordinates.put(newY, indices);   // VERIFIER QUE indices ne meurt pas quand 
											//on eneleves oldY
			
			//dans la structure des Y inférieurs à gamma[t-1]
			
			//enlever le oldY. quelle influence sur les capacités ? dépend
			
			
			
			boolean removeY =Yinf.remove(oldY); //la liste est inchangée si il n'y est pas
			if (removeY) { // on doit faire un decrease de la capacité et trouver le premier
							//y concerné, c'est chiant
				
			}
			Yinf.add(newY); //TRIER OU PAS Yinf ?
			
			valuesY[j] =newY;
			
			//set dans l'arete ?
			//MAJ de la capacité
			
			
			
			
			
			
	    }
		
	}
	
	
	
	public boolean testIncreaseCap(int i, int k) {
		int prevCapacity = Ek.network.getLink(i).getCapacity();
		int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
		boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
		if (reload) {
			Ek.EdmondsKarp();
			}
		int flow = Ek.maxFlowValue;
		boolean test = (flow >= demand);
		Ek.DecreaseLinkCapacity(i, newCapacity-prevCapacity);
		return test;
		
	}
	
	
	
	//doit chercher les Y qui sont plus grands que gamma_t-1 et plus petits que gamma_t(=gamma),
	//pour les intégréer a la structure de maxFlot
	public void updateChainGamma (double gamma) {}
	
	
	//si on a fiat le calcul du maxFLot à l'instant gammat = gam, on le compare à la demande
	   public boolean isImportanceGamma(double gam) {
		      return Ek.maxFlowValue >= demand;
		   }

	@Override
	public double getImportance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getPerformance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
