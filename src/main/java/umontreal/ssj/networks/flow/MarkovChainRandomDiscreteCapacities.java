package umontreal.ssj.networks.flow;

import java.util.Arrays;
import java.util.HashMap;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.MarkovChainNetworkReliability;
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
	protected MaxFlowEdmondsKarp Ek;
	double time; //incomplete TC : can be TC or less
	
	
	   public MarkovChainRandomDiscreteCapacities(GraphFlow father, RandomStream streamPermut,
		   		int demand)
		   {
		   	super();
		   	this.father = father;
		   	this.demand = demand;
		   	this.Ek = new MaxFlowEdmondsKarp(father);
		   	this.streamPermut = streamPermut;
		   	this.coordinates  = new HashMap <Double,int[]>();
		   	this.valuesY = new double[1];this.valuesY[0]=0.0;
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
		   System.out.println("Ok");
		   return valuesY;
		   
	   }
	
	   
	   
	   
	   public void initialState(RandomStream stream, double gamma) {
		   for (int i=0;i<father.getNumLinks();i++) {
			   father.initLinkLambda(i);
		   }
		   double [] tab = drawY(stream); // coordinates a aussi été modifié
		   Arrays.sort(tab);
		   valuesY = tab;
		   
	   }
	   
	   // Suppose que le tableau des Y est trié et calcule le TC
	   // Old Y : on a déjà effectué le saut avec ce Y
	   public double computeTC(double[] val,MaxFlowEdmondsKarp EK,double oldY) {
		   //GraphFlow copy = father.clone();
		   int p =0;
		   int maxFlow = 0;
		   while (maxFlow < demand && p<valuesY.length ) {
			   double y = val[p];
			   if (y != oldY) {
			   int [] indices = coordinates.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =EK.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   if (reload) {
				   maxFlow = EK.EdmondsKarp();
			   }
			   }
			   else 
			   			{}
			   p++;	   
		   }
		   return val[p-1]; // a verifier
	   }
	   
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		int numY = valuesY.length;
		int[] tab = new int[numY]; //tab contient des indices,
		double [] newvaluesY = new double [numY];
		streamPermut.resetNextSubstream();
	    RandomPermutation.init(tab, numY);
	    RandomPermutation.shuffle(tab, streamPermut); // permute the links
	    for (int l = 0; l < numY; l++) {
	    	int j = tab[l] - 1;
	    	double oldY = valuesY[j];
			int [] indices = coordinates.get(oldY);
			int i = indices[0];
			int k = indices[1];
			int prevCapacity = father.getLink(i).getCapacity(); // on s'en fiche de cette 
			// capa, elle est probablement nulle
			// compute TC à partir du EK et du Y
			MaxFlowEdmondsKarp E = new MaxFlowEdmondsKarp(father);
			boolean reload =E.IncreaseLinkCapacity(i,father.getLink(i).getCapacityValue(k+1) - prevCapacity  );
			if (reload) {
				  E.EdmondsKarp();
			   }
			double TC = computeTC(valuesY,E,oldY);
			
			if (TC < gamma) {
				//new Sample, set it into tab+ add it into permut
				LinkFlow EdgeI  = father.getLink(i);
				double lambda = EdgeI.getLambdaValue(k);
				double newY = gamma +ExponentialDist.inverseF(lambda, stream.nextDouble());
				
				newvaluesY[l] = newY;
				 int [] t = new int[2];
				 t[0] = i;
				 t[1] = k;
				coordinates.put(newY, t);
				coordinates.remove(oldY); // Is it best ?
			}
			else {
				//new Sample, set it into tab + add it into permut
				LinkFlow EdgeI  = father.getLink(i);
				double lambda = EdgeI.getLambdaValue(k);
				double newY = ExponentialDist.inverseF(lambda, stream.nextDouble());
				newvaluesY[l] = newY;
				 int [] t = new int[2];
				 t[0] = i;
				 t[1] = k;
				coordinates.put(newY, t);
				coordinates.remove(oldY); // Is it best ?
				
			}

			Arrays.sort(valuesY);
			valuesY = newvaluesY; // les nouvelles valeurs de Y;
	    	
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
		   //ArrayIndexComparator comparator = new ArrayIndexComparator(valuesY);
		   //Integer[] indexesY = comparator.createIndexArray();
		   //Arrays.sort(indexesY, comparator);
		   int p = 0;
		   int maxFlow = 0;
		   double t = 0;  //le TC
		   MaxFlowEdmondsKarp Ek1 = new MaxFlowEdmondsKarp(father); // on recrée un EK a chaque fois ??
		   while (p<valuesY.length) {
			   double y = valuesY[p];
			   t =valuesY[p];
			   int [] indices = coordinates.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek1.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   if (reload) {
				   maxFlow = Ek1.EdmondsKarp();
			   }
			   if (maxFlow >= demand || t>gamma) {
				   this.time = t;
				   break;
			   }
			   p++;
		   }  
	   }
	   
	   // Si après avoir rajouté les arêtes, on n'a pas encore atteint le MaxFlot, alors
	   // on sait que TC
	   public boolean isImportanceGamma(double gam) {
		      return (time>gam);
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
	


	
	// FONCTION DE CLONAGE, cloner correctmeent le tableau Y et la table de hashages
	
	   @Override
	   public MarkovChainRandomDiscreteCapacities clone() {
		   //MarkovChainRandomDiscreteCapacities image = (MarkovChainRandomDiscreteCapacities) super.clone(); 
		   MarkovChainRandomDiscreteCapacities image = new MarkovChainRandomDiscreteCapacities(father,streamPermut,
			   		demand);
		  
		  double [] copy = new double[this.valuesY.length];
		  System.arraycopy(this.valuesY, 0, copy, 0, valuesY.length);
		  
		  image.valuesY = copy;
		  for (int l=0;l<valuesY.length;l++) {
			  double y = this.valuesY[l];
			  int[] t = coordinates.get(y);
			  image.coordinates.put(y, t);}
	      return image;
	   }
	
	
}
