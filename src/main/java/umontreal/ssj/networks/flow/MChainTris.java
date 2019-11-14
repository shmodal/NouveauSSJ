package umontreal.ssj.networks.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.MarkovChainNetworkReliability;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.rng.RandomPermutation;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.MarkovChainWithImportance;

import umontreal.ssj.splitting.MarkovChainWithImportance;

public class MChainTris extends MarkovChainWithImportance {
	public MaxFlowEdmondsKarp Ek; //when chain is at level gamma, it keeps the info
	//about links added  before gamma
	public HashMap <Double,int[]> coordinates;
	protected RandomStream streamPermut; // for random permutations of links
	int demand;
	public ArrayList<Double> Yinf;  //retient les Yi,j inférieurs à gamma(t-1)
	public double[] valuesY;  //toutes les valeurs de Y

	
	
	
	public MChainTris(GraphFlow father, RandomStream streamPermut,
			int demand)
	{
		super();
		this.demand = demand;
		this.Ek = new MaxFlowEdmondsKarp(father);
		Ek.EdmondsKarp();
		this.streamPermut = streamPermut;
		this.coordinates  = new HashMap <Double,int[]>();
		this.valuesY = new double[1];this.valuesY[0]=0.0;
		Yinf = new ArrayList<Double>();
	}
	

	public void initialState(RandomStream stream, double gamma) {
		int taille = 0; 
		for (int i=0;i<Ek.network.getNumLinks();i++) {
			Ek.network.initLinkLambda(i);
			
			//////////////PEUT FAIRE BUGGER EN NON COMMENTE
			
			//double[] tabY = new double[father.getLink(i).getB()];
			//father.getLink(i).setValuesY(tabY); // je m'en sers jamais ? mais initialise au moins(pas NULL)
			taille += Ek.network.getLink(i).getB();
		}
		//initialiser le drawing. On ne peut pas faire autrement
		Yinf = new ArrayList<Double>();
		valuesY = new double[taille];
		int compteur = 0;
		for (int i=0;i<Ek.network.getNumLinks();i++) {
			double [] lambI = Ek.network.getLambdaValues(i);
			for (int j=0;j< lambI.length;j++) {
				double lambda = lambI[j];
				valuesY[compteur] = ExponentialDist.inverseF(lambda, stream.nextDouble());
				int [] t = new int[2];
				t[0] = i; //System.out.println(i);
				t[1] = j; //System.out.println(j);
				coordinates.put(valuesY[compteur],t);
				//System.out.println(valuesY[compteur]);
				compteur++;
			}
		}
		Arrays.sort(valuesY);   //se demander si je garde ça
	}
	
	
	
	
	
	
	
	
	
	
	
	private static void printTab(double[] t) {
		int m = t.length;
		for (int i =0;i<m;i++) {
			System.out.print(" " +t[i] +", ");
		}
	}
	private static void printTab(int[] t) {
		int m = t.length;
		for (int i =0;i<m;i++) {
			System.out.print(" " +t[i] +", ");
		}
	}
	
	
	
	
	public void updateChainGamma (double gamma) {
		int taille = Yinf.size(); //nombre d'élements y inférieurs à gamma(t-1). 0 initial
		int p = taille;
		
		while (p<valuesY.length && valuesY[p]<=gamma) { 
			double y = valuesY[p]; 

			Yinf.add(valuesY[p]);
			int [] indices = coordinates.get(y);
			int i = indices[0];
			int k = indices[1];
			int prevCapacity = Ek.network.getLink(i).getCapacity();
			int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
			Ek.network.setCapacity(i, newCapacity);
			boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
			if (reload) {
				Ek.EdmondsKarp();
			}
			p++;

		}
	}

	
	
	
	@Override
	public boolean isImportanceGamma(double gam) {
		//System.out.println("max Flot vaut" + Ek.maxFlowValue);
		return Ek.maxFlowValue < demand;
	}

	
	
	
	// Verison de clonage direct, sans passer par valuesY
	@Override
	   public MChainTris clone() {
		   //MarkovChainRandomDiscreteCapacities image = (MarkovChainRandomDiscreteCapacities) super.clone(); 
		MChainTris image = new MChainTris(Ek.network,streamPermut,
			   		demand);
		
		   //cloner les graphes du EK (on perd les capacités sinon et la structure de maxFlot)
		   image.Ek = this.Ek.clone();
		   // Copier les Les lambda k . Il audra faire une MAJ de EK pour qu'il marche sur des GraphCapa
		   for (int i=0;i<image.Ek.network.getNumLinks();i++) {
			   double [] lamb = Ek.network.getLambdaValues(i);
			   Ek.network.initLinkLambda(i);
			   //image.Ek.network.setLambdaValues(lamb, i);
			   //image.Ek.residual.setLambdaValues(lamb, i);   
		   }
  
///CLONAGE DE VALUES Y(tous les Y)
		   
		  double [] copy = new double[this.valuesY.length];
		  //System.arraycopy(this.valuesY, 0, copy, 0, valuesY.length);
		  for (int i=0;i<valuesY.length;i++) {
			  copy[i] = valuesY[i];
		  }
		  image.valuesY = copy;
		  
// Clonage de la hash map
		  
		  //System.out.println("Début clonage hash map");
		  
	        for (Map.Entry mapentry : this.coordinates.entrySet()) {
		           double key = (double) mapentry.getKey(); //est ce que ca marche ?
		           int[] t = this.coordinates.get(key);
		           int i = t[0];
		           int k = t[1];
		           int[] tab = new int[2]; tab[0] =i; tab[1]=k;
		           image.coordinates.put(key, tab);
	        		
	        }
	        
	        //System.out.println("Fin clonage hash map");
	        
	//CLonage de Yinf
	        
		  for (int l=0;l<Yinf.size();l++) {
			  image.Yinf.add(Yinf.get(l));
		  }
		  
	      return image;
	   }
	
	
	
	public boolean testIncreaseCap(int i, int k) {
		//System.out.println();
		//System.out.println("test Increase Cap ");
		int prevCapacity = Ek.network.getLink(i).getCapacity();
		int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
		System.out.println("prev capa" + prevCapacity);
		System.out.println("nouv capa" + newCapacity);
		boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
		if (reload) {
			Ek.EdmondsKarp();
		}
		//System.out.println("new capa" + newCapacity);
		int flow = Ek.maxFlowValue;
		System.out.println("max flow" + flow);
		System.out.println(Ek.network.toString());
		boolean test = (flow >= demand); //TC < gamma(t-1)
		Ek.DecreaseLinkCapacity(i, newCapacity-prevCapacity);
		return test;

	}
	
	
	
	
	
	
	public void printHash() {
		System.out.println("Observation de la hash map");
        for (Map.Entry mapentry : coordinates.entrySet()) {
	           double key = (double) mapentry.getKey(); //est ce que ca marche ?
	           System.out.println("Valeur Y");
	           System.out.println(key);
	           int[] t0 = coordinates.get(key);
	           int i = t0[0];
	           int k = t0[1];
	           System.out.println("Arete : " +i +" Indice : " + k);
     }
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
	
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		int numY = valuesY.length;
		int[] tab = new int[numY]; //tab contient des indices,
		//double [] newvaluesY = new double [numY]; // utile ?

		streamPermut.resetNextSubstream();
		RandomPermutation.init(tab, numY);
		RandomPermutation.shuffle(tab, streamPermut); // permute the links
		for (int l = 0; l < numY; l++) {
			int j = tab[l] - 1;
			double oldY = valuesY[j]; 
			System.out.println("Indice j " + j + " correspondant à y " + oldY );
			int [] indices = coordinates.get(oldY); //System.out.println(indices.length);
			int i = indices[0];
			int k = indices[1];
			System.out.println("Sanity check : indices " + i + " " + k);
			double newY;

			LinkFlow EdgeI  = Ek.network.getLink(i);
			double lambda = EdgeI.getLambdaValue(k); // VERIFIER le k ou k+1
			
			double newYtemp = ExponentialDist.inverseF(lambda, stream.nextDouble()); //unconditional sampling
			
			
			boolean flowBiggerDemand; //We create a boolean to see if by changing Yi,k to 0, the
									//max flow is modified
			
			//first, if expected new capacity <= current capacity, false(max flow already to low)
			
			if (Ek.network.getLink(i).getCapacityValue(k+1)<=Ek.network.getLink(i).getCapacity()) {
				flowBiggerDemand = false;
			}
			else {
				flowBiggerDemand = (testIncreaseCap(i,k));
			}
			
			System.out.println("Flow bigger demand ?" + flowBiggerDemand);
			
			if (flowBiggerDemand) {
				newY = gamma + newYtemp;
				System.out.println("nouveau Y simulé" + newY);
				Yinf.remove(oldY);
				
				valuesY[j] =newY;
				coordinates.put(newY, indices);
				coordinates.remove(oldY);
				
				
				
				
			}
			else { 


				newY = newYtemp;
				System.out.println("nouveau Y simulé" + newY);
				//MAJ de values Y et coordinates ? 
				if (newY <= gamma && oldY<= gamma) {
					System.out.println("On est dans cas newY inférieur et old Y inférieur");
					boolean removeY =Yinf.remove(oldY);
					Yinf.add(newY);

					valuesY[j] =newY;
					coordinates.put(newY, indices);
					coordinates.remove(oldY);
					
				}
				if (newY > gamma && oldY > gamma) {
					System.out.println("On est dans cas newY supérieur et old Y supérieur");
					valuesY[j] =newY;
					coordinates.put(newY, indices);
					coordinates.remove(oldY);
				}
				if ((newY <= gamma && oldY > gamma)) {
					System.out.println();
					System.out.println("On est dans cas newY inférieur et old Y supérieur");
					System.out.println();
					
					int prevCapacity = Ek.network.getLink(i).getCapacity();
					int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
					
					if (prevCapacity<=newCapacity) {
					
					boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);

					if (reload) {
						Ek.EdmondsKarp();
					}
					Ek.network.setCapacity(i, newCapacity);
					}
					
					Yinf.add(newY);
					valuesY[j] =newY;
					coordinates.remove(oldY);
					coordinates.put(newY, indices);
				}
				if ((newY > gamma && oldY <= gamma)) {
					System.out.println();
					System.out.println("ancien y appliqué, nouveau y out");
					System.out.println();
					
					System.out.println("indice k antérieur " + k);
					System.out.println("correspond dans capValues à  " + Ek.network.getLink(i).getCapacityValue(k+1));
					System.out.println("En effet, la capacité réelle est" + Ek.network.getLink(i).getCapacity());
					boolean removeY =Yinf.remove(oldY);
					
					coordinates.put(newY, indices);
					coordinates.remove(oldY);
					
					int prevCapacity = Ek.network.getLink(i).getCapacity();
					int possibleCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
					
					//2 cas possibles. Soit il existe des Yi,k+1(ou autre) inférieurs à gamma. Le terme ne nous interesse pas.
					// SOit les Yi correspondent à des capacités plus faibles. Dans ce cas, il faut trouver à quel capa dans 
					//Yinf on dot diminuer
					
					if (prevCapacity==possibleCapacity) {
						int kmax = k;
						System.out.println("begin Yinf");
						for (int p=0;p<Yinf.size();p++) {
							//System.out.println("Yinf en p" + Yinf.get(p));
							int [] t = coordinates.get(Yinf.get(p));
							int i0 = t[0];
							int k0 = t[1];
							if (i0==i &&k0 <kmax) {
								System.out.println("On a trouvé un saut de capacité inférieur, pour l'arete");
								System.out.println("indice i0 " + i0 + " indice k0 " +k0);
								System.out.println("indice i " + i);
								kmax = k0;
							}
						}
						if (kmax==k) {
							kmax = -1;	
						}

						int newC = Ek.network.getLink(i).getCapacityValue(kmax+1);
						System.out.println("newC " + newC);
						int oldC = Ek.network.getLink(i).getCapacity();
						System.out.println("oldC " + oldC);
						Ek.DecreaseLinkCapacity(i, oldC-newC);
						
						Ek.network.getLink(i).setCapacity(newC);

						valuesY[j] =newY;
					}
					
			}
			}
			


		}
			
		Arrays.sort(valuesY);
		
	}
	
	
}
