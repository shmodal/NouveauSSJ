package umontreal.ssj.networks.flow;

import java.util.Arrays;
import java.util.HashMap;

import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.rng.RandomStream;



public class PMCNonOriented extends PMC {

	public PMCNonOriented(GraphFlow graph) {
		super(graph);
	}
	
		@Override
	public void drawY(RandomStream stream) {
		  //le graphe est non orienté. On fait des simulations sur uniquement
			// les m/2 premiers liens les autres c'est juste symétrie.
			//on initialise les lambda partout
		int m = father.getNumLinks();
		   for (int i=0;i<m;i++) {
			   //System.out.println("Arête " + i);
			   father.initLinkLambda(i);
			   //System.out.println(father.getLambdaValues(i).length);
			   
			   if (i<(m/2)) {
			   double [] lamb = father.getLambdaValues(i);
			   //if (i==0) {System.out.println("Tab Lambda");
				//   printTab(lamb);}
			   //System.out.println("Bjr");
			   double [] ValuesY = new double[lamb.length];
			   //System.out.println(ValuesY.length);
			   for (int j=0;j< ValuesY.length;j++) {
				   //System.out.println("j");
				   double lambda = lamb[j];
				   ValuesY[j] = ExponentialDist.inverseF(lambda, stream.nextDouble());
			   }
			   father.setValuesY(ValuesY, i);
			   father.setValuesY(ValuesY, i+m/2); // les Y simulés pour l'autre
			   //System.out.println("Tab des Y, arete " + i);
			   //printTab(ValuesY);
			   //if (i==0) {System.out.println("Tab des Y");
			//	   printTab(ValuesY);}
		   }
		}
	   }
		
		
	   @Override
	   public double[] Y_values() {
		   int taille = 0;
		   permutation = new HashMap <Double,int[]>();
		   int m = father.getNumLinks();
		   for (int i=0;i<m/2;i++) {
			   //System.out.println(father.getLink(i).numberJumps);
			   taille += father.getLink(i).numberJumps; // inialisation deja faite non ?
		   }
		   double [] valuesY = new double[taille];
		   int compteur = 0;
		   for (int i = 0;i<m/2;i++) {
			   LinkFlow edgeI = father.getLink(i);
			   int n = edgeI.getJumpLength(); // Inutile ?
			   for (int k=0;k<n;k++) {
				   int s = edgeI.getJump(k);
				   if (s ==1) {  //saut activé
					   //double [] tabY = edgeI.getValuesY(); // Inefficace, prendre juste la valeur Yi,k
					   double val = edgeI.getY(k);
					   valuesY[compteur] = val;
					   compteur++;
					   int [] t = new int[2];
					   t[0] = i;
					   t[1] = k;
					   //t[2] = -1; //to be set
					   permutation.put(val,t);
				   }
			   }
		   }
		   //System.out.println("Valeur Y en 0" + valuesY[0]);
		   //System.out.println(valuesY[1]);
		   Arrays.sort(valuesY);
		   return valuesY;
		   //for (int j=0;j<valuesY.length;j++) {
		//	   			int [] t = permutation.get(valuesY[j]);
			   			//t[2] = j;
			   			//permutation.replace(valuesY[j],t);
			   			
			   				// j = Pi(i,k), on cherche lequel est-ce
			   
		   
	   }
	   	

	   protected double doOneRun(RandomStream stream,int demand,boolean flag) {
		   //initCapaProbaB(tableauB,rho,epsilon); //initialise bi, c_{i,k} et r_{i,k}
		   trimCapacities(demand);
		   int m= father.getNumLinks();
		   // verif
		   //System.out.println("les capas de 0");
		   //printTab(father.getCapacityValues(0));
		   
		   
		   drawY(stream); // initialise les lambda et tire les Yi, pour toutes les arêtes i
		   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
		   double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut
		   //System.out.println("Valeurs de Y triées");
		   //printTab(valuesY);
		   
		   
		   int K = valuesY.length;
		   // LE tableau LAM est de taille : 1(le lambda 1 initial) + le nombre de sauts
		   
		   initLamb(K+1);
		   //System.out.println("Tableau des Lam initial");
		   //System.out.println();
		   //printTab(Lam);
		   //System.out.println();
		   
		   int j = 0;
		   int[] X = buildX();
		   father.setCapacity(X);
		   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
		   int maxFlow = Ek.EdmondsKarp();
		   
		   //System.out.println();
		   //System.out.println("maxFlow initial" +maxFlow );
		   
		   
		   // JE PENSE ERREUR / IL FAUT PAS INCREMENTER J AU DEBUT
		   // ARTICLE, S(PI(1), Y PI(1)  ca fait ingorer le 1er terme du tableau Y a chaque fois,
		   // il manque un jump
		   
		   while (maxFlow < demand && j <Lam.length-1) {
			   
			   
			   //System.out.println(" j avant : " + j);
			   //j++;
			   //System.out.println(" j après : " + j);
			   double y = valuesY[j]; // Y(pi(j))
			   //System.out.println();
			   //System.out.println("Valeur de Ypi(J)"+ y);
			   int [] indices = permutation.get(y);
			   //System.out.println();
			   //System.out.println("Indices récupérés");
			   int i = indices[0];
			   int k = indices[1];
			   //System.out.println("i : " + i + "   k : " +k);
			   LinkFlow EdgeI = father.getLink(i);
			   
			   //System.out.println("Capacites arete i=0");
			   //printTab(father.getLink(3).getCapacityValues());
			   //System.out.println("Capacites arete i=3");
			   //if (i==3) {printTab(EdgeI.getCapacityValues());}
			   int s = EdgeI.getJump(k);
			   if (s==1) {
				   //System.out.println();
				   //System.out.println("Jump détecté");
				   double l = EdgeI.getLambdaTilde(k);
				   
				   
				   // ANCIEN pour j++ qui est au debut de l'algo
				   //Lam[j] = Lam[j-1] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
				   
				   
				   
				   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
				   //System.out.println(Lam[j]);
				   // pour que Lam[j] toujours défini
				   father.setJump(i, k, 0);
				   
				   father.setJump(i+(m/2), k, 0);
				   
				   //System.out.println();
				   //System.out.println("MAJ d'une capacité");
				   //System.out.println("Ancienne capacité de l'arete " + i);
				   //System.out.println();
				   //System.out.println(father.getLink(i).getCapacity());
				   //printTab(EdgeI.getCapacityValues());
				   
				   // Oui, probleme d'indices. il ne met jamais la capacité 8 alors qu'il devrait la mettre tout le temps.
				   // Si , k : k entre 0 et bi-1. Mais k de l'algo, entre 1 et bi
				   
				   //father.setCapacity(i, EdgeI.getCapacity(k+1));
				   father.setCapacity(i, EdgeI.getCapacityValue(k+1));
				   father.setCapacity(i + (m/2), EdgeI.getCapacityValue(k+1));
				  // System.out.println();
				  // System.out.println("Capa indice k " +EdgeI.getCapacity(k));
				   //System.out.println("Capa indice k+1 " +EdgeI.getCapacity(k+1));
				   
				   //System.out.println("Nouvellecapacité de l'arete " + i);
				   //System.out.println();
				   //System.out.println(father.getLink(i).getCapacity());

				   //father.setCapacity(i, EdgeI.getCapacity(k+1));
				   
				   //X[i] = EdgeI.getCapacity(k); // vérifier pour l'indice
				   //Filter()
				   Ek= new MaxFlowEdmondsKarp(father);
				  // System.out.println("calcul maxFlow");
				   maxFlow = Ek.EdmondsKarp();
				   //System.out.println("MaxFLow : " + maxFlow);
			   }
			   
		   j++;	   
		   }
		   //System.out.println("MaxFLow : " + maxFlow);
		   criticalLink.add(j);
		   //System.out.println("Tableau des grands Lambda ");
		   //printTab(Lam);
		   //System.out.println();
		   
		   double ell = computeBarF(Lam,j);
		   //System.out.println("ell" + ell);
		   
		   return ell;
	   }
	   
	   
}
