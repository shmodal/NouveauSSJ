package umontreal.ssj.networks.verifPMC;

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
			   
			   if (i<(m/2)) {
			   double [] lamb = father.getLambdaValues(i);
			   //if (i==0) {System.out.println("Tab Lambda");
				//   printTab(lamb);}
			   double [] ValuesY = new double[lamb.length];
			   for (int j=0;j< ValuesY.length;j++) {
				   //System.out.println("j");
				   double lambda = lamb[j];
				   ValuesY[j] = ExponentialDist.inverseF(lambda, stream.nextDouble());
			   }
			   father.setValuesY(ValuesY, i);
			   father.setValuesY(ValuesY, i+m/2); // les Y simulés pour l'autre
			   //System.out.println("Tab des Y, arete " + i);
			   //printTab(ValuesY);
		   }
		}
	   }
		
		
		// Dans valuesY, pour chaque Y, on retient le i et k tel que i< m/2 
		//(le premier sens des arêtes)
		
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
	   }
	   	
	   
	   
	   // Filter + update Max Flow
	   @Override
	   protected double doOneRun(RandomStream stream,int demand) {
		   //double alpha = 0.4;
		   trimCapacities(demand);
		   int m= father.getNumLinks();
		   		   
		   drawY(stream); // initialise les lambda et tire les Yi, pour toutes les arêtes i
		   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
		   double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut

		   int K = valuesY.length;
		   // LE tableau LAM est de taille : 1(le lambda 1 initial) + le nombre de sauts
		   
		   initLamb(K+1);
		   
		   int j = 0;
		   int[] X = buildX();
		   father.setCapacity(X);
		   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
		   int maxFlow = Ek.EdmondsKarp();
		  
		    
		   int p=0; // parcours du tableau des valeurs de Y
		   // Il est necessaire de distinguer p et j, car comme on filter, on tombe sur des
		   // valeurs Y dans le talbeau Y_values, mais dont le Si,k est nul. Elles ne sont donc
		   // pas rajoutées comme lien et j ne doit pas etre incrémenté
		   while (maxFlow < demand && j <Lam.length-1 && p<K) {
			   
			   double y = valuesY[p]; // Y(pi(j))
			   int [] indices = permutation.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int s = EdgeI.getJump(k);
			   
			   if (s==1) {
				   double l = EdgeI.getLambdaTilde(k); 
				   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
				   father.setJump(i, k, 0);
				   father.setJump(i+(m/2), k, 0);

	   
				   int prevCapacity = father.getLink(i).getCapacity();
				   boolean reload1 = Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) -prevCapacity);
				   father.setCapacity(i, EdgeI.getCapacityValue(k+1));
				   
				   boolean reload2 = Ek.IncreaseLinkCapacity(i+ (m/2),EdgeI.getCapacityValue(k+1) -prevCapacity);
				   father.setCapacity(i + (m/2), EdgeI.getCapacityValue(k+1));
				   if (reload1 || reload2) {
					   maxFlow = Ek.EdmondsKarp();
				   }
				   
 				      
				   //if (filter) {
				  if (filter && maxFlow > level*demand) {
	////////////////////FILTERING/////////////
					   //double sumLamb = FilterSingle(i,demand);
					   double sumLamb = FilterSingle(i,k,demand);
					   if (sumLamb >=0) {
						   Lam[j+1] = Lam[j+1] - sumLamb;
					   }
				   }
				   j++;
			   }
			   p++;
		   	   
		   }
		   //System.out.println("MaxFLow : " + maxFlow);
		   criticalLink.add(j);
		   
		   double ell = computeBarF(Lam,j);
		   return ell;
	   }
	   
	   
   
	   // Attention, faire le filtrage seulement à partir du Si,k 
	   //(les capacités qui sont supérieures à celle actuelle)
	   
	   @Override 
	   public double FilterSingle(int i, int k, int demand) {
		   int m = father.getNumLinks();
		   //System.out.println("Filtrage arête " + i);
		   LinkFlow EdgeI = father.getLink(i);
		   int source = EdgeI.getSource();
		   int target = EdgeI.getTarget();
		   MaxFlowEdmondsKarp EkFilter= new MaxFlowEdmondsKarp(father);
		   EkFilter.source = source;
		   EkFilter.sink = target;
		   int f = EkFilter.EdmondsKarp();
		   if (f>=demand) {
			   int b = EdgeI.getB();
			   double sumLamb = 0.;
			   for (int j=k+1;j<b;j++) {
				   int s= EdgeI.getJump(j);
				   if(s==1) {
					   sumLamb += EdgeI.getLambdaTilde(j);
					   EdgeI.setJump(j, 0);
					   father.getLink(i+ m/2 ).setJump(j, 0);
				   }
			   }
			return sumLamb;   
		   }
		   else {return (-1.0);}
		   }
	   
	   
	   
	   // Ne pas prendre en compte les arêtes symétriques( i >) m/2)
	   @Override
	   public void initLamb(int k) {
		   double[] Lambda = new double[k];
		   int m = father.getNumLinks();
		   double l = 0.0;
		   for (int i=0;i<(m/2);i++) {
			   LinkFlow EdgeI = father.getLink(i);
			   l += EdgeI.sommeLambda;
		   }
		   Lambda[0] = l;
		   Lam = Lambda;
	   }
	   
	   
	   
	   
	   // Filter + update Max Flow
	   @Override
	   protected double doOneRunFilterOutside(RandomStream stream,int demand) {
		   //double alpha = 0.4;
		   trimCapacities(demand);
		   int m= father.getNumLinks();
		   
		   for (int i=0;i<m;i++) {father.getLink(i).outsideFlow=0;}
		   		   
		   drawY(stream); // initialise les lambda et tire les Yi, pour toutes les arêtes i
		   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
		   double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut

		   int K = valuesY.length;
		   // LE tableau LAM est de taille : 1(le lambda 1 initial) + le nombre de sauts
		   
		   initLamb(K+1);
		   
		   int j = 0;
		   int[] X = buildX();
		   father.setCapacity(X);
		   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
		   int maxFlow = Ek.EdmondsKarp();
		  
		   GraphFlow copy = prepareMultiFlow();
		   MaxFlowEdmondsKarp EkOut = new MaxFlowEdmondsKarp(copy);
		   
		   int p=0; // parcours du tableau des valeurs de Y
		   // Il est necessaire de distinguer p et j, car comme on filter, on tombe sur des
		   // valeurs Y dans le talbeau Y_values, mais dont le Si,k est nul. Elles ne sont donc
		   // pas rajoutées comme lien et j ne doit pas etre incrémenté
		   while (maxFlow < demand && j <Lam.length-1 && p<K) {
			   //System.out.println(K);
			   double y = valuesY[p]; // Y(pi(j))
			   int [] indices = permutation.get(y);
			   int i = indices[0];
			   int k = indices[1];
			   LinkFlow EdgeI = father.getLink(i);
			   int s = EdgeI.getJump(k);
			   
			   if (s==1) {
				   double l = EdgeI.getLambdaTilde(k); 
				   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
				   father.setJump(i, k, 0);
				   father.setJump(i+(m/2), k, 0);

	   
				   int prevCapacity = father.getLink(i).getCapacity();
				   boolean reload1 = Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) -prevCapacity);
				   father.setCapacity(i, EdgeI.getCapacityValue(k+1));
				   
				   boolean reload2 = Ek.IncreaseLinkCapacity(i+ (m/2),EdgeI.getCapacityValue(k+1) -prevCapacity);
				   father.setCapacity(i + (m/2), EdgeI.getCapacityValue(k+1));
				   if (reload1 || reload2) {
					   maxFlow = Ek.EdmondsKarp();
				   }
				   /////
				   prevCapacity = EkOut.network.getLink(i).getCapacity();
				   reload1 = EkOut.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) -prevCapacity);
				   EkOut.network.setCapacity(i, EdgeI.getCapacityValue(k+1));
				   
				   reload2 = EkOut.IncreaseLinkCapacity(i+ (m/2),EdgeI.getCapacityValue(k+1) -prevCapacity);
				   EkOut.network.setCapacity(i + (m/2), EdgeI.getCapacityValue(k+1));
				   if (reload1 || reload2) {
					   EkOut.EdmondsKarp();
				   }
				   
				   //System.out.println("Capa avant Filter " +EdgeI.getCapacity());
				   
				   if (filter) {   
				   //if (filter && maxFlow > level*demand) {
	
					   double sumLamb = FilterSingle(i,demand);
					   if (sumLamb >=0) {
						   Lam[j+1] = Lam[j+1] - sumLamb; // A VERIFIER
					   }
				   }
				   
				   if (filterOutside) { 
					   if (p%5 ==0 && p>(0.8*K)) { //mise a jour updateFlow. //Proposer aussi p depasse un seuil ? ?
						   
						   int source = EdgeI.getSource();
						   int sink = EdgeI.getTarget();
						   int n =father.getNumNodes();
						   
						   EkOut.DecreaseLinkCapacity(i,EdgeI.getCapacity());
						   boolean b =EkOut.IncreaseLinkCapacity( EkOut.network.getLinkWithSourceAndSinkNodes(n, source), 1000*demand);
						   boolean c =EkOut.IncreaseLinkCapacity( EkOut.network.getLinkWithSourceAndSinkNodes(sink, n+1), 1000*demand);
						   
						   if (b || c) {EkOut.EdmondsKarp();}
						   EdgeI.outsideFlow = EkOut.maxFlowValue;

						   EkOut.DecreaseLinkCapacity( EkOut.network.getLinkWithSourceAndSinkNodes(n, source), 1000*demand);
						   EkOut.DecreaseLinkCapacity( EkOut.network.getLinkWithSourceAndSinkNodes(sink, n+1), 1000*demand);

						   //EkOut.DecreaseLinkCapacity(i,EdgeI.getCapacity()); // on la met à 0
						   
						   //System.out.println("Filter outside");
						   //System.out.println("Capa de l'arete " +EdgeI.getCapacity());
					   
					   
					   }
					   double sumLamb = FilterOutside(i,k,demand);
					   if (sumLamb >=0) {
						   Lam[j+1] = Lam[j+1] - sumLamb; // A VERIFIER
					   }
					   
				   }
				   j++;
			   }
			   p++;
		   	   
		   }
		   //System.out.println("MaxFLow : " + maxFlow);
		   criticalLink.add(j);
		   
		   double ell = computeBarF(Lam,j);
		   return ell;
	   }
	
	   
	   
	   
	   
}
