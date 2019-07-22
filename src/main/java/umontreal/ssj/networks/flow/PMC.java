package umontreal.ssj.networks.flow;


import umontreal.ssj.rng.*;
import umontreal.ssj.probdist.*;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.*;
import java.util.HashMap;
import java.util.Arrays;



/**
 * This class implements the Permutation Monte Carlo method to estimate the
 * reliability of a network. It uses the constructive schema, i.e., it assumes
 * that initially, all links are failed and then add working links until the
 * network becomes operational, i.e., the subset of nodes V0 becomes connected.
 *
 * @since 2011
 */
public class PMC {

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

   
   public PMC(GraphFlow graph) {
      father = graph;
      //father.setSampler(SamplerType.EXPONENTIAL, false);
      criticalLink = new Tally();
      //storeFlag = false;
      //histFlag = false;
      hekind = 1;
      antiScanFlag = false;
      shockFlag = false;
   }
   
   
   /////////////////////NEW
   
   
   // Initailisation des capacites et probas de façon spécifique(celle de l'article)
   // on peut vouloir donner des capacités et probas différentes, il faudra modifier
   
   public void initCapaProbaB(int[] tableauB, double rho, double epsilon) {
	   initB(tableauB);
	   initCapacityValues();
	   initProbabilityValues(rho,epsilon);
   }
   

   
   
   
   protected double doOneRun(RandomStream stream,int demand,boolean flag) {
	   //initCapaProbaB(tableauB,rho,epsilon); //initialise bi, c_{i,k} et r_{i,k}
	   trimCapacities(demand);
	   //int m= father.getNumLinks();
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
			   
			   int prevCapacity = father.getLink(i).getCapacity();
			   
			   
			   // ANCIEN pour j++ qui est au debut de l'algo
			   //Lam[j] = Lam[j-1] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
			   
			   
			   
			   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
			   //System.out.println(Lam[j]);
			   // pour que Lam[j] toujours défini
			   father.setJump(i, k, 0);
			   //System.out.println();
			   //System.out.println("MAJ d'une capacité");
			   //System.out.println("Ancienne capacité de l'arete " + i);
			   //System.out.println();
			   //System.out.println(father.getLink(i).getCapacity());
			   //printTab(EdgeI.getCapacityValues());
			   
			   // Oui, probleme d'indices. il ne met jamais la capacité 8 alors qu'il devrait la mettre tout le temps.
			   // Si , k : k entre 0 et bi-1. Mais k de l'algo, entre 1 et bi
			   
			   //father.setCapacity(i, EdgeI.getCapacity(k+1));
			   
			   boolean reload =Ek.IncreaseLinkCapacity(true,i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			 //father.setCapacity(i, EdgeI.getCapacity(k+1));
			   
			   
			   //father.setCapacity(i, EdgeI.getCapacityValue(k+1));
			   
			   
			  // System.out.println();
			  // System.out.println("Capa indice k " +EdgeI.getCapacity(k));
			   //System.out.println("Capa indice k+1 " +EdgeI.getCapacity(k+1));
			   
			   //System.out.println("Nouvellecapacité de l'arete " + i);
			   //System.out.println();
			   //System.out.println(father.getLink(i).getCapacity());

			   //father.setCapacity(i, EdgeI.getCapacity(k+1));
			   
			   //X[i] = EdgeI.getCapacity(k); // vérifier pour l'indice
			   //Filter()
			   
			   if (reload) {
				   maxFlow = Ek.EdmondsKarp();
			   }
			   
			   
//////////////////////////ANCIEN CODE //////////
		   //father.setCapacity(i, EdgeI.getCapacityValue(k+1));
		   //father.setCapacity(i + (m/2), EdgeI.getCapacityValue(k+1));
		   
		   //Ek= new MaxFlowEdmondsKarp(father);
		   //maxFlow = Ek.EdmondsKarp();
//////////////////FIN ANCIEN CODE	
			   
			   
			  // System.out.println("calcul maxFlow");
			   
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
   
   public double run(int n, RandomStream stream,int demand,boolean flag) {
	      Chrono timer = new Chrono();
	      timer.init();
	      Tally values = new Tally(); // unreliability estimates
	      for (int j = 0; j < n; j++) {
	         double x = doOneRun(stream,demand,flag);
	         if (storeFlag)
	            store.add(x);
	         if (histFlag)
	            hist.add(Math.log10(x));
	         values.add(x);
	      }

	      m_ell = values.average();
	      m_variance = values.variance();
	      double sig = Math.sqrt(m_variance);
	      double relvar = m_variance / (m_ell * m_ell); // relative variance
	      double relerr = sig / (m_ell * Math.sqrt(n)); // relative error
	      System.out.printf("barW_n      = %g%n", m_ell);
	      System.out.printf("S_n         = %g%n", sig);
	      System.out.printf("var = S_n^2 = %g%n", m_variance);
	      System.out.printf("var/n       = %g%n", m_variance / n);
	      System.out.printf("rel var(W)  = %g%n%n", relvar);
	      System.out.println(values.formatCINormal(0.95, 4));
	      System.out.printf("rel err(barW_n) = %g%n", relerr);

	      double cro = timer.getSeconds();
	      double tem = cro * m_variance / n;
	      System.out.printf("time*var/n      = %g%n", tem);
	      System.out.printf("time*var/(n*barW_n^2) = %g%n%n", tem
	            / (m_ell * m_ell));
	      if (shockFlag)
	         System.out.printf("rank crit. shock = %g%n", criticalLink.average());
	      else
	         System.out.printf("rank crit. link = %g%n", criticalLink.average());
	      printHypoExpFlag();
	      System.out.printf("CPU time:   %.1f  sec%n%n%n", cro);
	      return relerr;
	   }
      
   
   private void printTab(double[] t) {
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
   
   
   // Pour le moment, ED Karps construit un graphe à chaque itération
   //dans le futur, modifier EK pour juste prendre en compte un vecteur de capcités ?
   
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
   
   // init les capacités pour chaque lien, avec pour capacité 0... (Bi-1)
   
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
   
   
   
   
   
   /** Computes the values of <tt>Lambda_i,k</tt> and sets them in each link i.
    * Then, it draws the values of <tt>Y_i,k</tt> and also sets them.
    * @param stream
    */
   
   // Attention, il faut verifie rplus tard que toutes les valeurs de Lamb sont initialisées 
   // à un moment(dans l'étape finale du pseudo code)
   
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
   
   public void drawY(RandomStream stream) {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   //System.out.println("Arête " + i);
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
		   //if (i==0) {System.out.println("Tab des Y");
		//	   printTab(ValuesY);}
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
		   father.getLink(i).setCapacityValue(b,Math.min(demand,tab[b]) );
		   //int [] copy = new int[tab.length];
		   //System.arraycopy(tab, 0, copy, 0, tab.length);
		   //copy[b] =Math.min(demand,tab[b]);
		   //tab[b] = Math.min(demand,tab[b]);
		   //father.setCapacityValues(tab);
		   //father.setCapacityValues(copy);
	   }
   }
   
   // travail sur chacun des Links
   // les capacités ont été trim, les Y ont été tirés, 
   // init le tableau S, le tableau lambdaTildeValues et nJumps(utilisé pour ensuite créer le bon
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
    * Sets the direction of scan to find the critical link that fails the
    * network. If <tt>true</tt>, use reverse scanning (also called anti
    * scanning), otherwise use direct scanning.
    * @param flag reverse scan flag
    */
   public void setAntiScan(boolean flag) {
      antiScanFlag = flag;
   }

   /**
    * Returns <tt>true</tt> if the anti scan flag is set, <tt>false</tt> otherwise.
    * @see #setAntiScan
    * @return the reverse scan flag
    */
   public boolean isAntiScan () {
      return antiScanFlag;
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
