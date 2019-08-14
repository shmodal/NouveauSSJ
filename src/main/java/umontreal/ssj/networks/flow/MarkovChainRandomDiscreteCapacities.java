package umontreal.ssj.networks.flow;

import java.util.Arrays;
import java.util.HashMap;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.rng.RandomPermutation;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

// Version orientée

public class MarkovChainRandomDiscreteCapacities extends MarkovChainWithImportance {
	
	protected double[] valuesY;
	protected GraphFlow father; //
	public HashMap <Double,int[]> coordinates;
	protected RandomStream streamPermut; // for random permutations of links
	int demand;
	protected MaxFlowEdmondsKarp Ek1;
	int time;
	
	
	   public MarkovChainRandomDiscreteCapacities(GraphFlow father, RandomStream streamPermut,
		   		int demand)
		   {
		   	super();
		   	this.father = father;
		   	this.demand = demand;
		   	this.Ek1 = new MaxFlowEdmondsKarp(father);
		   	this.streamPermut = streamPermut;
		   }
	
	   public double[] drawY(RandomStream stream) {
		   int m = father.getNumLinks();
		   int taille = 0;
		   for (int i=0;i<m;i++) {
			   taille += father.getLink(i).getB();
		   }
		   double[] valuesY = new double[taille];
		   HashMap <Double,int[]> permutation = new HashMap <Double,int[]>();
		   int compteur = 0;
		   for (int i=0;i<m;i++) {
			   double [] lambI = father.getLambdaValues(i);
			   for (int j=0;j< lambI.length;j++) {
				   double lambda = lambI[j];
				   valuesY[compteur] = ExponentialDist.inverseF(lambda, stream.nextDouble());
				   int [] t = new int[2];
				   t[0] = i;
				   t[1] = j;
				   permutation.put(valuesY[compteur],t);
				   compteur++;
			   }
		   }
		   this.coordinates = permutation;
		   return valuesY;
	   }
	
	   
	   
	   
	   public void initialState(RandomStream stream, double gamma) {
		   for (int i=0;i<father.getNumLinks();i++) {
			   father.initLinkLambda(i);
		   }
		   valuesY = drawY(stream);
		   int TC = computeTC(this.Ek1);
		   this.time = TC;
		   
	   }
	   
	   // Suppose que le tableau des Y est trié. Ce n'est pas une mise à jour
	   // On va sureme
	   public int computeTC(MaxFlowEdmondsKarp EK) {
		   //GraphFlow copy = father.clone();
		   int p =0;
		   int maxFlow = 0;
		   while (maxFlow < demand && p<valuesY.length ) {
			   double y = valuesY[p];
			   int [] indices = coordinates.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =EK.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   if (reload) {
				   maxFlow = EK.EdmondsKarp();
			   }
			   p++;	   
		   }
		   return p;
	   }
	   
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		int numY = valuesY.length;
		int[] tab = new int[numY]; //tab contient des indices, et à la fin contient les nov
		//velles valeurs de Y
		streamPermut.resetNextSubstream();
	    RandomPermutation.init(tab, numY);
	    RandomPermutation.shuffle(tab, streamPermut); // permute the links
	    for (int l = 0; l < numY; l++) {
	    	int j = tab[l] - 1;
	    	double oldY = valuesY[j];
			int [] indices = coordinates.get(oldY);
			int i = indices[0];
			int k = indices[1];
			
			int prevCapacity = father.getLink(i).getCapacity();
			boolean reload =Ek1.IncreaseLinkCapacity(i,father.getLink(i).getCapacityValue(k+1) - prevCapacity  );
			if (reload) {
				  Ek1.EdmondsKarp();
			   }
			if (Ek1.maxFlowValue >= demand) {
				//new Sample, set it into tab
			}
			else {
				//new Sample, set it into tab
			}
			Ek1.DecreaseLinkCapacity(i, father.getLink(i).getCapacityValue(k+1) - prevCapacity);
			
			
	    	
	    }
		
		
		
		
	}
	
	   //update the Markov chain according to the gamma,
	// on doit rajouter des aretes successivement, et voir si on a déjà atteint le max flot
	// ou pas. On le modifie dans EK1. EK1
	// si on rajoute gamma-T aretes et que on n'a pas encore le maxFlow atteint, alors
	// on sait que TC > gamma_t
	   @Override
	   public void updateChainGamma (double gamma) {
		   //on recupere Y_values et l'ordre danns  lequel ils doivent être triés
		   ArrayIndexComparator comparator = new ArrayIndexComparator(valuesY);
		   Integer[] indexesY = comparator.createIndexArray();
		   Arrays.sort(indexesY, comparator);
		   int p = 0;
		   int maxFlow = 0;
		   while (p<valuesY.length && p<=gamma) {
			   double y = valuesY[p];
			   int [] indices = coordinates.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek1.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   if (reload) {
				   maxFlow = Ek1.EdmondsKarp();
			   }
			   
			   
			   
			   
			   
			   p++;
		   }
		   
		   
		   
	   }
	   
	   // Si après avoir rajouté les arêtes, on n'a pas encore atteint le MaxFlot, alors
	   // on sait que TC
	   public boolean isImportanceGamma(double gam) {
		      return Ek1.maxFlowValue <demand ;
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
	
	public

	
	// FONCTION DE CLONAGE, cloner correctmeent le tableau Y
}
