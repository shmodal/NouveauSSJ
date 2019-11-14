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

////////////////VERSION AVEC TOUS LES COMMENTAIRES, SI CA REFOIRE
//creation de la MChain
//clonage A FAIRE

//Version Orientée.
//Ensuite, pour Version non orientée : il faut faire gaffe aux lambda et capacité

public class OLDMChainNewOLDWIthComments extends MarkovChainWithImportance {
	protected GraphFlow father;
	protected MaxFlowEdmondsKarp Ek; //when chain is at level gamma, it keeps the info
	//about links added  before gamma
	public HashMap <Double,int[]> coordinates;
	protected RandomStream streamPermut; // for random permutations of links
	int demand;
	public ArrayList<Double> Yinf;  //retient les Yi,j inférieurs à gamma(t-1)
	public double[] valuesY;  //toutes les valeurs de Y


	public OLDMChainNewOLDWIthComments(GraphFlow father, RandomStream streamPermut,
			int demand)
	{
		super();
		this.demand = demand;
		this.father=father;
		this.Ek = new MaxFlowEdmondsKarp(father);
		Ek.EdmondsKarp();
		this.streamPermut = streamPermut;
		this.coordinates  = new HashMap <Double,int[]>();
		this.valuesY = new double[1];this.valuesY[0]=0.0;
		Yinf = new ArrayList<Double>();
		
		for (int i=0;i<Ek.network.getNumLinks();i++) {
			father.initLinkLambda(i);
			Ek.network.setLambdaValues(father.getLambdaValues(i), i);
		}
		
	}


	//a modifier en version Non Orientée : modifier en parallèle les indices
	public void initialStateOld(RandomStream stream, double gamma) {
		int taille = 0; 
		for (int i=0;i<Ek.network.getNumLinks();i++) {
			Ek.network.initLinkLambda(i);
			double[] tabY = new double[Ek.network.getLink(i).getB()];
			Ek.network.getLink(i).setValuesY(tabY);
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
	
	
	public void initialState(RandomStream stream, double gamma) {
		int taille = 0; 
		for (int i=0;i<father.getNumLinks();i++) {
			father.initLinkLambda(i);
			double[] tabY = new double[father.getLink(i).getB()];
			father.getLink(i).setValuesY(tabY); // je m'en sers jamais ? mais initialise au moins(pas NULL)
			taille += father.getLink(i).getB();
		}
		//initialiser le drawing. On ne peut pas faire autrement
		Yinf = new ArrayList<Double>();
		valuesY = new double[taille];
		int compteur = 0;
		for (int i=0;i<father.getNumLinks();i++) {
			double [] lambI = father.getLambdaValues(i);
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
	
	
	
	
	
	




	//initialiser avec vecteur à Y infini ?

	// juste la simulation conditionnelle des Y et leur modification.
	// PB, mettre à jour la structure qui concerne les gamma_t-1 et gamma_t
	//pour le test de re sampling, il faut faire increase cap et decrease cap
	//que faire des new Y?
	@Override
	public void nextStep(RandomStream stream, double gamma) {
		System.out.println();
		System.out.println("Next Step");
		System.out.println();
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
			// test pour savoir si TC<gamma(t-1).  On verifie si Yi,k augmente la capa
			//on decrease cap dedans aussi(on la remet à la valeur initiale)
			//testIncreaseCap = false si newCap < oldCap

			//Si le nouveau Yik est plus petit que gamma(t-1), on ne fait pas de decrease cap
			// Si il est plus grand, on fait decrease cap
			//LinkFlow EdgeI  = father.getLink(i);
			LinkFlow EdgeI  = Ek.network.getLink(i);
			double lambda = EdgeI.getLambdaValue(k); // VERIFIER le k ou k+1
			
			double newYtemp = ExponentialDist.inverseF(lambda, stream.nextDouble()); //unconditional sampling
			
			
			boolean flowBiggerDemand; //We create a boolean to see if by changing Yi,k to 0, the
									//max flow is modified
			
			//first, if Yik<gamma (jump already done), then it is false
			
			if (oldY<gamma) {
				flowBiggerDemand = false;
			}
			else { //we need to test first
				//if augmenter la capa donne TC<gamma(t-1)
				//on doit aussi savoir si il faut remettre la capa initalement ou pas
				flowBiggerDemand = (testIncreaseCap(i,k,newYtemp,gamma));
			}
			
			System.out.println("Flow bigger demand ?" + flowBiggerDemand);
			
			if (flowBiggerDemand) {
				newY = gamma + newYtemp;
				//MAJ de values Y et coordinates ?	
				System.out.println("nouveau Y simulé" + newY);
				Yinf.remove(oldY);
				//System.out.println(Yinf.get(0));
				//System.out.println(Yinf.get(1));
				//System.out.println(Yinf.get(2));
				//System.out.println("oldY " +oldY);
				//System.out.println(gamma);
				//System.out.println("newY " +newY);
				
				valuesY[j] =newY;
				coordinates.put(newY, indices);
				coordinates.remove(oldY);
				int [] test = coordinates.get(newY);
				System.out.println("indice i " + i + " indice k " +k);
				//int a =test[0];
				
			}
			else { System.out.println("on est dans l'autre boucle");

				//LinkFlow EdgeI  = Ek.network.getLink(i);
				//double lambda = EdgeI.getLambdaValue(k);  // VERIFIER le k ou k+1
				//newY = ExponentialDist.inverseF(lambda, stream.nextDouble());
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
					
					// PAS SUR QUE CE SOIT UTILE
					// CEST SUREMENT DEJA FAIT DANS TEST INCREASE CAP COMME NEWY<=GAMMA
					int prevCapacity = Ek.network.getLink(i).getCapacity();
					int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
					boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
					System.out.println("prev capacity" + prevCapacity);
					System.out.println("new capacity à set" + newCapacity);
					if (reload) {
						Ek.EdmondsKarp();
					}
					Ek.network.setCapacity(i, newCapacity);
					System.out.println("new capacity :" + Ek.network.getLink(i).getCapacity());
					Yinf.add(newY);
					valuesY[j] =newY;
					coordinates.remove(oldY);
					coordinates.put(newY, indices);
				}
				if ((newY > gamma && oldY <= gamma)) {
					System.out.println();
					System.out.println("ancien y appliqué, nouveau y out");
					
					boolean removeY =Yinf.remove(oldY);
					
					coordinates.put(newY, indices);
					coordinates.remove(oldY);
					System.out.println();
					System.out.println("On a enlevé oldY, il devrait etre parti");
					
					System.out.println("oldY" + oldY);
					
					
					//System.out.println(removeY);
					//on parcourt les capacités de Yinf pour savoir à quelle capa diminuer
					int kmax=-1; // ou 0 ?
					System.out.println("begin Yinf");
					for (int p=0;p<Yinf.size();p++) {
						System.out.println("Yinf en p" + Yinf.get(p));
						int [] t = coordinates.get(Yinf.get(p));
						int i0 = t[0];
						int k0 = t[1];
						//System.out.println("p= " +p);
						//System.out.println("Y[p] " + Yinf.get(p));
						//System.out.println("i = " +i0);
						//System.out.println("k = " +k0);
						if (i0==i &&k0 >kmax) { 
							System.out.println("On a trouvé un saut de capacité inférieur, pour l'arete");
							System.out.println("indice i0 " + i0 + " indice k " +k0);
							System.out.println("indice i " + i);
							kmax = k0;
						}
					}
					//System.out.println("end");
					//System.out.println("Kmax");
					//System.out.println(Ek.network.getLink(i).getCapacityValue(kmax));
					//System.out.println(Ek.network.getLink(i).getCapacityValue(kmax+1));
					System.out.println("kmax" + kmax);
					int newC = Ek.network.getLink(i).getCapacityValue(kmax+1);
					System.out.println("newC " + newC);
					int oldC = Ek.network.getLink(i).getCapacity();
					System.out.println("oldC " + oldC);
					if(newC>= oldC) {
						//System.out.println("Lol?");
						//System.out.println("Ancienn y " + oldY);
						//System.out.println("Nouveu y "+ newY);
						//System.out.println("Ancienne capa " + oldC);
						//System.out.println("Nouvelle capa "+ newC);
						
						boolean reload = Ek.IncreaseLinkCapacity(i,newC-oldC);
						if (reload) {
							Ek.EdmondsKarp();
						}
						
					}
					else { Ek.DecreaseLinkCapacity(i, oldC-newC);

					}
					Ek.network.getLink(i).setCapacity(newC);
					//coordinates.put(newY, indices);
					//coordinates.remove(oldY);
					valuesY[j] =newY;
					
				}
				
			}
			
			
			
			
			//
			//   // VERIFIER QUE indices ne meurt pas quand 
			//on eneleves oldY

			//dans la structure des Y inférieurs à gamma[t-1]

			//enlever le oldY. quelle influence sur les capacités ? dépend


		}
		Arrays.sort(valuesY);//Trier valuesY? utile pour chercher
		System.out.println();
		System.out.println("Next step fini");
		System.out.println("Valeurs de Y");
		printTab(valuesY);
		System.out.println();
		System.out.println("To string du graphe");
		System.out.println(Ek.network.toString());
		
	}



	public boolean testIncreaseCap(int i, int k,double y, double gam) {
		System.out.println();
		System.out.println("test Increase Cap ");
		int prevCapacity = Ek.network.getLink(i).getCapacity();
		int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
		System.out.println("prev capa" + prevCapacity);
		if (newCapacity < prevCapacity) { //le saut est inutile, on est deja en dessous de gamma(t-1)
			return false;
		}
		boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
		Ek.network.setCapacity(i, newCapacity);
		if (reload) {
			Ek.EdmondsKarp();
		}
		System.out.println("new capa" + newCapacity);
		int flow = Ek.maxFlowValue;
		boolean test = (flow >= demand); //TC < gamma(t-1)
		
		if (test || y>gam) { //le saut i,k, qu'on a mis temporairement à 0, doit etre remis à sa vraie
					//valeur si le newY est plus grand que gamma(t-1), ou si le flow depasse la demande
			Ek.DecreaseLinkCapacity(i, newCapacity-prevCapacity);
			Ek.network.setCapacity(i, prevCapacity);
		}
		return test;

	}



	//doit chercher les Y qui sont plus grands que gamma_t-1 et plus petits que gamma_t(=gamma),
	//pour les intégréer a la structure de maxFlot
	//on les rajoute dans Yinf et on met à jour Ek
	//values Y est supposé sorted

	public void updateChainGamma (double gamma) {
		int taille = Yinf.size(); //nombre d'élements y inférieurs à gamma(t-1). 0 initial
		//System.out.println(taille);
		int p = taille;
		//System.out.println(valuesY.length);
		System.out.println("update avec Gamma :" + gamma);
		
		while (p<valuesY.length && valuesY[p]<=gamma) { 
			//System.out.println("true");
			double y = valuesY[p]; 
			System.out.println("Valeur à rajouter" +y);
			System.out.println();
			//System.out.println("Une valeur, check if sorted " +valuesY[p]);
			Yinf.add(valuesY[p]);
			System.out.println("Valeur ajoutée" + Yinf.get(p));
			System.out.println();
			int [] indices = coordinates.get(y);
			int i = indices[0];
			int k = indices[1];
			System.out.println("Arete : " +i +" Indice : " + k);
			int prevCapacity = Ek.network.getLink(i).getCapacity();
			int newCapacity = Ek.network.getLink(i).getCapacityValue(k+1);
			Ek.network.setCapacity(i, newCapacity);
			System.out.println("Capacité maj dans network"+ Ek.network.getLink(i).getCapacity());
			boolean reload = Ek.IncreaseLinkCapacity(i,newCapacity-prevCapacity);
			System.out.println("Update le flow ?" + reload);
			if (reload) {
				Ek.EdmondsKarp();
			}
			p++;
			System.out.println("Max Flot " +Ek.maxFlowValue);
			System.out.println();
			//System.out.println("Capacité maj dans reisidual"+ Ek.residual.getLink(i).getCapacity());

		}
		//System.out.println("Max Flot à la fin de l'update " +Ek.maxFlowValue);
	}


	//Si on a fait le calcul du maxFlot à l'instant gam = gamma_t 
	// on le compare à la demande 
	@Override
	public boolean isImportanceGamma(double gam) {
		//System.out.println("max Flot vaut" + Ek.maxFlowValue);
		return Ek.maxFlowValue < demand;
	}

	
	// Le clonage de la table de hashage se fait en regardant les valeurs de Y à cette étape. C'est ca l'erreur ?
	
	
	   
	   public MChainNew clone2() {
		   //MarkovChainRandomDiscreteCapacities image = (MarkovChainRandomDiscreteCapacities) super.clone(); 
		   MChainNew image = new MChainNew(father,streamPermut,
			   		demand);
		   
		   
		   // copier les lambda K
		   
	//	   for (int i=0;i<image.father.getNumLinks();i++) {
	//		   double [] lamb = father.getLambdaValues(i);
	//		   image.father.setLambdaValues(lamb, i);
	//		   image.Ek.network.setLambdaValues(lamb, i);   
	//	   }
		   
		   
		  
		  double [] copy = new double[this.valuesY.length];
		  //System.arraycopy(this.valuesY, 0, copy, 0, valuesY.length);
		  for (int i=0;i<valuesY.length;i++) {
			  copy[i] = valuesY[i];
		  }
		  
		  
		  image.valuesY = copy;
		  //Arrays.sort(image.valuesY);
		  
		  for (int l=0;l<valuesY.length;l++) {
			  double y = this.valuesY[l];
			  //System.out.println(l);
			  //System.out.println("Le y actuel qu'on recopie" +y);
			  //System.out.println("Le y dans le tableau autre" +image.valuesY[l]);
			  int[] t = coordinates.get(y);
			  //System.out.println(t[0]);
			  //System.out.println(t[1]);
			  image.coordinates.put(y, t);	  
			  
			  int[] t0 = image.coordinates.get(y);
			  //System.out.println(t0[0]);
			  //System.out.println(t0[1]);
			  }
		  
	        System.out.println("La table initiale:");
	        for (Map.Entry mapentry : this.coordinates.entrySet()) {
	           System.out.println("clé: "+mapentry.getKey() 
	                              + " | valeur: " + mapentry.getValue());
	        }
	        
	        System.out.println("La table suivante:");
	        for (Map.Entry mapentry : image.coordinates.entrySet()) {
	           System.out.println("clé: "+mapentry.getKey() 
	                              + " | valeur: " + mapentry.getValue());
	        }
		  
		  
		  for (int l=0;l<Yinf.size();l++) {
			  image.Yinf.add(Yinf.get(l));
		  }
		  
	      return image;
	   }
	




	@Override
	//calculer le TC
	//il faut que valuesY soit sorted. Verifier que c'est le cas
	
	public double getImportance() {
		   //GraphFlow copy = father.clone();
		   int p =0;
		   int maxFlow = 0;
		   MaxFlowEdmondsKarp EkCopy = new MaxFlowEdmondsKarp(father);
		   while (maxFlow < demand && p<valuesY.length ) {
			   double y = valuesY[p];
			   System.out.println("valeur de y " + y);
			   int [] indices = coordinates.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   System.out.println("indices " + i + " " + k);
			   LinkFlow EdgeI = EkCopy.network.getLink(i);
			   int prevCapacity = EkCopy.network.getLink(i).getCapacity();
			   System.out.println("ancien flow " + maxFlow);
			   System.out.println("ancienne cap " + prevCapacity);
			   boolean reload =EkCopy.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   EkCopy.network.getLink(i).setCapacity(EdgeI.getCapacityValue(k+1));
			   System.out.println("nouvelle cap dans network " + EkCopy.network.getLink(i).getCapacity());
			   System.out.println("nouvelle cap dans residual " + EkCopy.residual.getLink(i).getCapacity());
			   if (reload) {
				   maxFlow = EkCopy.EdmondsKarp();
			   }
			   System.out.println("nouv flow " + maxFlow);
			   p++;	   
		   }
		   return valuesY[p-1]; // a verifier
	}

	@Override
	public double getPerformance() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	

	
	// Verison de clonage direct, sans passer par valuesY
	@Override
	   public MChainNew clone() {
		   //MarkovChainRandomDiscreteCapacities image = (MarkovChainRandomDiscreteCapacities) super.clone(); 
		   MChainNew image = new MChainNew(father,streamPermut,
			   		demand);
		   
		   
		   //cloner les graphes du EK (on perd les capacités sinon et la structure de maxFlot)
		   image.Ek = this.Ek.clone();
		   
		   // Copier les Les lambda k . Il audra faire une MAJ de EK pour qu'il marche sur des GraphCapa
		   for (int i=0;i<image.father.getNumLinks();i++) {
			   double [] lamb = father.getLambdaValues(i);
			   image.Ek.network.setLambdaValues(lamb, i);
			   image.Ek.residual.setLambdaValues(lamb, i);   
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
	

}
