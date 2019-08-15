package umontreal.ssj.networks.flow;


import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.probdist.ExponentialDist;
import umontreal.ssj.probdist.HypoExponentialDist;
import umontreal.ssj.probdist.HypoExponentialDistEqual;
import umontreal.ssj.probdist.HypoExponentialDistQuick;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.stat.TallyHistogram;
import umontreal.ssj.stat.TallyStore;
import umontreal.ssj.util.Chrono;

public class PMCFlowContinuous {
	private double m_variance; // variance
	private double m_ell; // network reliability
	private TallyStore store; // store the value for each run
	private boolean storeFlag; // true: store value for each run
	private TallyHistogram hist; // store counters of value for each run
	private boolean histFlag; // true: store values for each run in bin counters


   protected GraphFlow father; // same father as in forest
   private int hekind; // HypoExponentialDist flag; see setHypoExpKind
   protected double[] Lam; // compound Lambda's
   Tally criticalLink; // critical link
   // for reverse or direct scan in finding critical link
   protected boolean antiScanFlag;  // true for reverse scan, false otherwise
   protected boolean shockFlag;
   
   public HashMap <Double,int[]> permutation;
   boolean oriented;
   boolean filter;
   double level; //seuil des alpha pour relancer le max Flot
   boolean filterOutside;
   int frequency;
   double seuil;
   
   GraphFlow residual; //NNNN

   

   
   public PMCFlowContinuous(GraphFlow graph) {
      father = graph;
      //father.setSampler(SamplerType.EXPONENTIAL, false);
      criticalLink = new Tally();
      //storeFlag = false;
      //histFlag = false;
      hekind = 1;
      antiScanFlag = false;
      shockFlag = false;
      level = -1.0; //de base, on relance le maxFlot tout le temps dans filter
      frequency = 1; //base, relance le calcul de outsideFlow a chaque fois
      seuil = -1.0; //de base, toujours au dessus du seuil pour outsideFlow
      //residual =graph.residual(); //NNNN
   }
   
   
   /////////////////////NEW
   
   
   // Initailisation des capacites et probas de fa�on sp�cifique(celle de l'article)
   // on peut vouloir donner des capacit�s et probas diff�rentes, il faudra modifier
   
   public void initCapaProbaB(int[] tableauB, double rho, double epsilon) {
	   initB(tableauB);
	   initCapacityValues();
	   initProbabilityValues(rho,epsilon);
   }
   
   //initialiser capacit�s allant de 0 � capaMax, et probabilit�s uniformes
   public void initBasicCapaProba(int capaMax) {
	   int[] capa = new int[capaMax+1];
	   double[] proba = new double[capaMax+1];
	   for (int k=0;k<capaMax+1;k++) {
		   capa[k] =k;
		   proba[k] = 1.0/(capaMax+1) ;
	   }

	   father.setCapacityValues(capa);
	   father.setProbabilityValues(proba);
   }
   
	/**
	 * Do one simulation to find estimation of unreliability function of demand (between [demandLow,demandHigh])
	 *
	 * @param stream
	 *           a stream to draw random variables
	 * @param demandLow
	 *           demand flow lower bound 
	 * @param demandHigh
	 *           demand flow upper bound
	 */
   protected ArrayList<ArrayList<Double>> doOneRun(RandomStream stream,int demandLow,int demandHigh) {
	   ArrayList<Double> demandLevels=new ArrayList<Double>();
	   ArrayList<Double> unreliabilities=new ArrayList<Double>();
	   
	   trimCapacities(demandHigh);
	   
	   drawY(stream); // initialise les lambda et tire les Yi, pour toutes les ar�tes i
	   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
	   double [] valuesY = Y_values();  // j'ai les Y tri�s, et les hash maps comme il faut
	   
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
	   // pas rajout�es comme lien et j ne doit pas etre incr�ment� 
	   
	   
	   while (maxFlow < demandLow && j <Lam.length-1 && p<K) {

		   double y = valuesY[p]; // Y(pi(j))
		   int [] indices = permutation.get(y);
		   int i = indices[0];
		   int k = indices[1];
		   LinkFlow EdgeI = father.getLink(i);
		   int s = EdgeI.getJump(k);
		   if (s==1) {
			   double l = EdgeI.getLambdaTilde(k);
			   
			   Lam[j+1] = Lam[j] - l; 
			   father.setJump(i, k, 0);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   
			   
			  father.setCapacity(i, EdgeI.getCapacityValue(k+1)); 
			   if (reload) {
				   maxFlow = Ek.EdmondsKarp();
			   }

			   j++; 
		   }
		   
		   p++;	   
	   }
	   
	   int curMaxFlow=maxFlow;
	   
	   criticalLink.add(j);
	   
	   double ell = computeBarF(Lam,j);
	   //we add the firts couple (MaxFlow,W) to our estimator
	   demandLevels.add(new Double(maxFlow));
	   unreliabilities.add(ell);
	   
	   while (maxFlow < demandHigh && j <Lam.length-1 && p<K) {
		   double y = valuesY[p]; // Y(pi(j))
		   int [] indices = permutation.get(y);
		   int i = indices[0];
		   int k = indices[1];
		   LinkFlow EdgeI = father.getLink(i);
		   int s = EdgeI.getJump(k);
		   if (s==1) {
			   double l = EdgeI.getLambdaTilde(k);
			   
			   Lam[j+1] = Lam[j] - l; 
			   father.setJump(i, k, 0);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   
			   
			   father.setCapacity(i, EdgeI.getCapacityValue(k+1)); 
			   if (reload) {
				   curMaxFlow=Ek.maxFlowValue;
				   maxFlow = Ek.EdmondsKarp();
				   if(maxFlow>curMaxFlow) {
					   //interested in the probability of the atom
					   ell = computeBarF(Lam,j)-ell;
					   //we add the couple (MaxFlow,W) to our estimator
					   demandLevels.add(new Double(maxFlow));
					   unreliabilities.add(ell);
					   
					   curMaxFlow=maxFlow;
				   }
			   }

			   j++; 
		   }
		   
		   p++;	   
	   }
	  
	   ArrayList<ArrayList<Double>> l=new ArrayList<ArrayList<Double>>();
	   l.add(demandLevels);
	   l.add(unreliabilities);
	   return l;
   }
   
   
   public void run(int n, RandomStream stream,int demandLow, int demandHigh) {
	      Chrono timer = new Chrono();
	      timer.init();
	      Tally values = new Tally(); // unreliability estimates
	      ArrayList<ArrayList<Double>> x;
	      ArrayList<Double> demandLevels=new ArrayList<Double>();
	      ArrayList<Double> unreliabilities=new ArrayList<Double>();
	      for (int j = 0; j < n; j++) {
	    	  //stream.resetNextSubstream();
	    	 
	    	 x = doOneRun(stream,demandLow,demandHigh);
	    	 for(int i=0;i<x.get(0).size();i++) {
	    		 demandLevels.add(x.get(0).get(i));
	    	 }
	    	 for(int i=0;i<x.get(1).size();i++) {
	    		 unreliabilities.add(x.get(1).get(i));
	    	 }
	    	 
	      }
	      Double[] tabDemands=demandLevels.toArray(new Double[demandLevels.size()]);
	      Double[] tabUnrelia=unreliabilities.toArray(new Double[unreliabilities.size()]);
	      ArrayIndexComparator comparator = new ArrayIndexComparator(tabDemands);
	      Integer[] indexes = comparator.createIndexArray();
	      Arrays.sort(indexes, comparator);
	      for (int i = 0; i < indexes.length; i++)
	      {
	    	  System.out.println(indexes[i]);
	      }
	      
	      
	   }
   
      
   
   protected void printTab(double[] t) {
	   int m = t.length;
	   for (int i =0;i<m;i++) {
		   System.out.println(t[i]);
	   }
   }
   protected void printTab(int[] t) {
	   int m = t.length;
	   for (int i =0;i<m;i++) {
		   System.out.println(t[i]);
	   }
   }
   
   
   

   
   public int[] buildX() {
	   int m = father.getNumLinks();
	   int[] X = new int[m];
	   for (int i=0;i<m;i++) {
		   LinkFlow EdgeI = father.getLink(i);
		   X[i] = EdgeI.getMinCapacity();
		   //int[] capa = EdgeI.getCapacityValues();
		   //X[i] = capa[0];
	   }
	   return X;
   }
   
   public void setGraphCapa (int [] X) {
	   int m= father.getNumLinks();
	   for (int i = 0;i<m;i++) {
		   father.setCapacityValues(X);
	   }
   }
   
   
   // Fixer les bi pour chaque
   
   public void initB(int[] tableauB) {
	   int numberOfLinks = father.getNumLinks();
	   for (int i = 0; i< numberOfLinks; i++) {
		   father.setB(i,tableauB[i]);
		   //System.out.println("Bi = " + father.getB(i));
	   } 
   }
   
   // init les capacit�s pour chaque lien, avec pour capacit� 0... (Bi-1)
   
   public void initCapacityValues() {
	   int numberOfLinks = father.getNumLinks();
	   for (int i = 0; i< numberOfLinks; i++) {
		   int b = father.getB(i);
		   int [] tabCapa = new int[b+1];
		   for (int j=0;j<b+1;j++) {
			   tabCapa[j] = j;
		   }
		   father.setCapacityValues(i, tabCapa);
		 //System.out.println("TabCapa = ");
		 // printTab(father.getCapacityValues(i));
	   }   
   }
   
   
   // init les probas comme l'a fait rohan shah
   
   public void initProbabilityValues(double rho, double epsilon) {
	   int numberOfLinks = father.getNumLinks();
	   for (int i = 0; i< numberOfLinks; i++) {
		   int b = father.getB(i);
		   double [] tabProba = new double[b+1];
		   tabProba[0] = epsilon*Math.pow(rho, b-1);
		   double sum = tabProba[0];
		   for (int k=1;k<b;k++) {
			   tabProba[k] = tabProba[k-1]/rho;
			   sum += tabProba[k];
		   }
		   tabProba[b] = 1.0-sum;
		   father.setProbabilityValues(i, tabProba);
	   }   
   }
   
   
   
   
   

   
   // Attention, il faut verifie rplus tard que toutes les valeurs de Lamb sont initialis�es 
   // � un moment(dans l'�tape finale du pseudo code)
   
   public void initLamb(int k) {
	   double[] Lambda = new double[k];
	   int m = father.getNumLinks();
	   double l = 0.0;
	   for (int i=0;i<m;i++) {
		   LinkFlow EdgeI = father.getLink(i);
		   l += EdgeI.sommeLambda;
	   }
	   Lambda[0] = l;
	   Lam = Lambda;
   }
   
   /** Computes the values of <tt>Lambda_i,k</tt> and sets them in each link i.
    * Then, it draws the values of <tt>Y_i,k</tt> and also sets them.
    * @param stream
    */
   
   public void drawY(RandomStream stream) {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   //System.out.println("Ar�te " + i);
		   father.initLinkLambda(i);
		   //System.out.println(father.getLambdaValues(i).length);
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
		   //System.out.println("Tab des Y, arete " + i);
		   //printTab(ValuesY);
	   }
   }
   
   
   /**
    * Resets the maximum capacities of links to <tt>demand</tt>.
    * It is useless to have <tt>c_(i,bi) > demand</tt>.
    * @param demand fixed demand we have
    */
   
   public void trimCapacities(int demand) {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   int b = father.getB(i);
		   int [] tab = father.getCapacityValues(i);
		   for (int k=0;k<tab.length;k++) {
			   father.getLink(i).setCapacityValue(k,Math.min(demand,tab[k]) );
			   }
		   //int [] copy = new int[tab.length];
		   //System.arraycopy(tab, 0, copy, 0, tab.length);
		   //copy[b] =Math.min(demand,tab[b]);
		   //tab[b] = Math.min(demand,tab[b]);
		   //father.setCapacityValues(tab);
		   //father.setCapacityValues(copy);
	   }
   }
   
   // travail sur chacun des Links
   // les capacit�s ont �t� trim, les Y ont �t� tir�s, 
   // init le tableau S, le tableau lambdaTildeValues et nJumps(utilis� pour ensuite cr�er le bon
   // tableau
   
   public void initJumpAndIndexes() {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   father.initJumpAndIndexes(i);
	   }
   }
	   
   
   
   
   // recuperer tous les Y, les trier, et les mettre dans hash map
   // est ce vraiment plus rapide que de se contenr de rechercher dans toutes les aretes ?
   //ensuite O(8m) au lieu de O(m)
   	
   
   // initialiser la HashMap
   
   
   /// INITIALISATION DEJA FAITE (dans le main ou doOne RUn)
   public double[] Y_values() {
	   int taille = 0;
	   permutation = new HashMap <Double,int[]>();
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   //System.out.println(father.getLink(i).numberJumps);
		   taille += father.getLink(i).numberJumps; // inialisation deja faite non ?
	   }
	   double [] valuesY = new double[taille];
	   int compteur = 0;
	   for (int i = 0;i<m;i++) {
		   LinkFlow edgeI = father.getLink(i);
		   int n = edgeI.getJumpLength(); // Inutile ?
		   for (int k=0;k<n;k++) {
			   int s = edgeI.getJump(k);
			   if (s ==1) {  //saut activ�
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
   
   // on prend le graphe de base. On rajoute 2 sommets suppl�mentaires. On cree un EK 
   // dont c'est sorce et sink. On les relie � toutes les sommets(2 sens ?) avec
   // capa nulle initialement.
   //on retient l'ancien lien o� on a modifi� les capacit�s, et on les update � 0.
   // puis on increase les capa des nouvelles aretes
   public GraphFlow prepareMultiFlow() {
	   GraphFlow copy = father.clone();
	   int n = copy.getNumNodes();
	   copy.addNode(new NodeBasic(n));  //noeud source
	   copy.addNode(new NodeBasic(n+1));  // noeud target
	   int m = copy.getNumLinks();
	   for (int k=0;k<n;k++) {
		   copy.addLink(new LinkFlow(m,n,k,0)); m++;
	   }
	   for (int k=0;k<n;k++) {
		   copy.addLink(new LinkFlow(m,k,n+1,0)); m++;
	   }
	   copy.source = n; copy.target = n+1;
	   return copy;
	   
	   
   }
   
   
   
   
   
   ////////// NOT NEW

   



   /**
    * Chooses the algorithm used to compute the probabilities for the 
    * HypoExponential distribution. <br />
    * If <tt>flag</tt> = 0, use the special case of all equidistant Lambdas,
    * valid only for PMC, not for turnip.<br />
    * If <tt>flag</tt> = 1, use the quick but sometimes
    * numerically unstable formula to compute the distribution.<br />
    * If <tt>flag</tt> = 2, use the slow numerically stable matrix formula.<br />
    * If <tt>flag</tt> = 3, use the slower numerically stable matrix formula,
    * accurate also for cdf values close to 0.<br />
    * If <tt>flag</tt> = -1, do not compute the probability: thus the result 
    * will be meaningless. This is done when we want to compare the speed of the
    * different algorithms without computing the probabilities. In many cases,
    * computing the matrix exponential for the probabilities takes much longer
    * than solving the problem itself.
    * The default value of this flag is 1.
    *
    * @param flag choose algorithm to compute probabilities
    */
   public void setHypoExpKind (int flag) {
      hekind = flag;
   }


   /**
    * Returns the flag indicating which algorithm is used to compute the
    * hypoexponential distribution.
    * @see #setHypoExpKind
    */
   public int getHypoExpKind () {
      return hekind;
   }

   /**
    Prints which version of the HypoExponential distribution is used
    to compute probabilities.
    */
   public void printHypoExpFlag() {
      System.out.print("HypoExponential distribution: ");    
      
      switch (hekind) {
      case 0: System.out.println("equal"); break;
      case 1: System.out.println("quick"); break;
      case 2: System.out.println("matrix"); break;
      case 3: System.out.println("matrix-0"); break;
      default: System.out.println("uncalled");
      }
   }



   /**
    * function ell=convolution(t,Lam) computes P(A_1+...+A_b1> 1) exactly, where
    * A_i ~ Exp(Lambda(i)) independently; Lambda has to be decreasing (sorted)
    * sequence. See the book \cite{pGER10a} on page 188 formula 5 for the
    * convolution of exponentials. b = b1 - 1.
    *
    * @param Lambda
    *           parameters of the waiting times
    * @param b critical rank
    */
   public double computeBarF(double[] Lambda, int b) {
      double[] tLam = trimLam(Lambda, b);
      int flag = getHypoExpKind();
      
      switch (flag) {
      case 0: 
         // WARNING: n is the number of links or the number of shocks; I 
         // have reserved n+1 element in Lambda; should reserve exactly n?
         int n = Lambda.length - 1;
         double h = Lambda[0] - Lambda[1];
         return HypoExponentialDistEqual.barF(n, b, h, 1.0);
      case 1: return HypoExponentialDistQuick.barF(tLam, 1.0);
      case 2: return HypoExponentialDist.barF(tLam, 1.0);
      default: return -1.0;
      }
   }




   /**
    * Sets all elements of Lam to 0.
    */
   public void zeroLam () {
      int m = Lam.length;
      for (int j = 0; j < m; j++)
         Lam[j] = 0;
   }

   double[] trimLam (double[] Lambda, int b) {
      double[] tLam = new double[b];
      System.arraycopy (Lambda, 0, tLam, 0, b);
      return tLam;
   }
}



