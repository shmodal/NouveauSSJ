package umontreal.ssj.networks.flow;

import java.util.HashMap;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

// Version orientée

public class MarkovChainRandomDiscreteCapacities extends MarkovChainWithImportance {
	
	protected double[] valuesY;
	protected GraphFlow father; //
	public HashMap <Double,int[]> permutation;
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
	
	
	   public void initialState(RandomStream stream, double gamma) {
		   for (int i=0;i<father.getNumLinks();i++) {
			   father.initLinkLambda(i);
		   }
		   valuesY = father.drawY(stream);
		   int TC = computeTC(this.Ek1);
		   this.time = TC;
	   }
	   
	   // Suppose que le tableau des Y est trié. Ce n'est pas une mise à jour
	   public int computeTC(MaxFlowEdmondsKarp EK) {
		   //GraphFlow copy = father.clone();
		   int p =0;
		   int maxFlow = 0;
		   while (maxFlow < demand && p<valuesY.length ) {
			   double y = valuesY[p];
			   int [] indices = permutation.get(y);
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
		// TODO Auto-generated method stub
		
	}
	
	   //update the Markov chain according to the gamma,
	// on doit rajouter des aretes successivement, et voir si on a déjà atteint le max flot
	// ou pas. On le modifie dans EK1.
	   @Override
	   public void updateChainGamma (double gamma) {
		   //on recupere Y_values
		   
		   
		   
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
