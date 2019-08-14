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
	
	
	   public MarkovChainRandomDiscreteCapacities(GraphFlow father, RandomStream streamPermut,
		   		int demand)
		   {
		   	super();
		   	this.father = father;
		   	this.demand = demand;
		   }
	
	
	   public void initialState(RandomStream stream, double gamma) {
		   father.drawY(stream);
		   for (int j=0;j<)
	   }
	   
	   // Suppose que le tableau est trié
	   public int computeTC() {
		   GraphFlow copy = father.clone();
		   int p =0;
		   int maxFlow = 0;
		   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(copy);
		   while (maxFlow < demand && p<valuesY.length ) {
			   double y = valuesY[p];
			   int [] indices = permutation.get(y);

			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   if (reload) {
				   maxFlow = Ek.EdmondsKarp();
			   }
			   
			   p++;
			   
		   }
		   return p;
	   }
	   
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		// TODO Auto-generated method stub
		
	}
	
	   //update the Markov chain according to the gamma, puisqu'on ne retient plus gamma
	   // dans la MC
	   @Override
	   public void updateChainGamma (double gamma) {
		   forest.update(gamma);
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
