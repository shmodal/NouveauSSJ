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
 * @author Richard simard
 * @since 2011
 */
public class PMC {

	private double m_variance; // variance
	private double m_ell; // network reliability
	private TallyStore store; // store the value for each run
	private boolean storeFlag; // true: store value for each run
	private TallyHistogram hist; // store counters of value for each run
	private boolean histFlag; // true: store values for each run in bin counters


   protected Forest forest;
   protected GraphWithCapacity father; // same father as in forest
   private int hekind; // HypoExponentialDist flag; see setHypoExpKind
   protected double[] Lam; // compound Lambda's
   Tally criticalLink; // critical link
   // for reverse or direct scan in finding critical link
   protected boolean antiScanFlag;  // true for reverse scan, false otherwise
   protected boolean shockFlag;
   
   public int[] tabS ;  // tableau S pour les termes Y(Pi(j)), initialisé à 1
   public HashMap <Double,int[]> permutation;

   
   public PMC(GraphWithCapacity graph, Forest forest) {
      father = graph;
      this.forest = forest;
      //father.setSampler(SamplerType.EXPONENTIAL, false);
      criticalLink = new Tally();
      //storeFlag = false;
      //histFlag = false;
      int m = father.getNumLinks();
      //Lam = new double[m + 1];  on s'en servira pas
      hekind = 1;
      antiScanFlag = false;
      shockFlag = false;
   }
   
   
   /////////////////////NEW
   
   
   public void initCapaProbaB(int[] tableauB, double rho, double epsilon) {
	   initB(tableauB);
	   initCapacityValues();
	   initProbabilityValues(rho,epsilon);
   }
   
   protected double testRun(RandomStream stream,int demand,boolean flag, int[] tableauB,double rho,double epsilon) {
	   initCapaProbaB(tableauB,rho,epsilon); //initialise bi, ci,k et ri,k
	   trimCapacities(demand);
	   drawY(stream); // initialise les lambda
	   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
	   double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut
	   int K = valuesY.length;
	   initLamb(K);
	   int j = 0;
	   int[] X = buildX();
	   while (maxFlow(X) < demand) {
		   j++;
		   double y = valuesY[j]; // Y(pi(j))
		   int [] indices = permutation.get(y);
		   int i = indices[0];
		   int k = indices[1];
		   LinkFlow EdgeI = father.getLink(i);
		   int s = EdgeI.getJump(k);
		   if (s==1) {
			   double l = EdgeI.getLambdaTilde(k);
			   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
			   // pour que Lam[j] toujours défini
			   father.setJump(i, k, 0);
			   X[i] = EdgeI.getCapacity(k); // vérifier pour l'indice
			   //Filter()
		   } 
		   criticalLink.add(j);  

	   }
	   double ell = computeBarF(Lam,j);
	   return ell;
   }
   
   
   // Temporaire
   private int maxFlow(int [] X) {
	   return 0;
   }
   
   public int[] buildX() {
	   int m = father.getNumLinks();
	   int[] X = new int[m];
	   for (int i=0;i<m;i++) {
		   Link EdgeI = father.getLink(i);
		   int[] capa = EdgeI.getCapacityValues();
		   X[i] = capa[0];
	   }
	   return X;
   }
   
   
   // Fixer les bi pour chaque
   
   public void initB(int[] tableauB) {
	   int numberOfLinks = father.getNumLinks();
	   for (int i = 0; i< numberOfLinks; i++) {
		   father.setB(i,tableauB[i]);
	   } 
   }
   public void initCapacityValues() {
	   int numberOfLinks = father.getNumLinks();
	   for (int i = 0; i< numberOfLinks; i++) {
		   int b = father.getB(i);
		   int [] tabCapa = new int[b+1];
		   for (int j=0;j<b;j++) {
			   tabCapa[j] = j;
		   }
		   father.setCapacityValues(i, tabCapa);
	   }   
   }
   
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
   

   
   public void initLamb(int k) {
	   double[] Lambda = new double[k];
	   int m = father.getNumLinks();
	   double l = 0.0;
	   for (int i=0;i<m;i++) {
		   Link EdgeI = father.getLink(i);
		   l += EdgeI.sommeLambda;
	   }
	   Lambda[0] = l;
	   Lam = Lambda;
   }
   
   public void drawY(RandomStream stream) {
	   int m = father.getNumLinks();
	   for (int i=0;i<m;i++) {
		   father.initLinkLambda(i);
		   double [] lamb = father.getLambdaValues(i);
		   double [] ValuesY = new double[lamb.length];
		   for (int j=0;i< ValuesY.length;j++) {
			   double lambda = lamb[j];
			   ValuesY[j] = ExponentialDist.inverseF(lambda, stream.nextDouble());
		   }
		   father.setValuesY(ValuesY, i);
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
		   tab[b] = Math.max(demand,tab[b]);
		   father.setCapacityValues(tab);
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
		   taille += father.getLink(i).numberJumps; // inialisation deja faite non ?
	   }
	   double [] valuesY = new double[taille];
	   int compteur = 0;
	   for (int i = 0;i<m;i++) {
		   Link edgeI = father.getLink(i);
		   int n = edgeI.getJumpLength();
		   for (int k=0;k<n;k++) {
			   int s = edgeI.getJump(k);
			   if (s ==1) {  //saut activé
				   double [] tabY = edgeI.getValuesY();
				   valuesY[compteur] = tabY[k];
				   compteur ++;
				   int [] t = new int[2];
				   t[0] = i;
				   t[1] = k;
				   //t[2] = -1; //to be set
				   permutation.put(tabY[k],t);
			   }
		   }
	   }
	   Arrays.sort(valuesY);
	   return valuesY;
	   //for (int j=0;j<valuesY.length;j++) {
	//	   			int [] t = permutation.get(valuesY[j]);
		   			//t[2] = j;
		   			//permutation.replace(valuesY[j],t);
		   			
		   				// j = Pi(i,k), on cherche lequel est-ce
		   
	   
   }
   
   
   
   
   
   ////////// NOT NEW

   

   protected double doOneRun(RandomStream stream) {
      // Draw independent repair times
      drawRepairTimes(stream);  // ca modifie dans weight
      int m = father.getNumLinks();
      double[] A = new double[m];
      double[] res = forest.getRepairTime(A);  // REFLECHIR ET MODIFIER
      // get repair time peut poser probleme, parce que il se contente de find link à partir 
      // des weights. Or ici on travaille avec des weights finis
      int b = (int) (res[1]); // sorted rank of critical link
      criticalLink.add(b + 1); // critical shock; b counts from 0 in program
      int[] L = forest.findLinkIndices(A);
      Lam = computeRates(L, b);
      double ell = computeBarF(Lam, b + 1);
      return ell;
   }


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
    * Draw repair times (or links weight) from the exponential distribution. The
    * rate parameters (lambda) of the exponential distribution must have been
    * precomputed for each link before calling this method.
    *
    * @param stream
    */
   protected void drawRepairTimes(RandomStream stream) {
      int m = father.getNumLinks();
      double w, lambda;
      for (int j = 0; j < m; j++) {
         Link link = father.getLink(j);
         lambda = link.getParam();
         w = ExponentialDist.inverseF(lambda, stream.nextDouble());
         forest.setWeight(j, w);
      }
   }

   /**
    * Computes rates such that Lam(E_i) = sum lambda(link) over links that are
    * (see eq. 16.5 in Botev handbook paper).
    *
    * @param link
    *           link numbers sorted according to weights
    * @param b
    *           critical link rank at which network becomes operational
    * @return the rates
    */
   private double[] computeRates(int[] link, int b) {
      int m = link.length;
      double x = 0;
      int r; // link number
      for (int j = b; j < m; j++) {
         // Sum lambda for links with weight > weight of link[b]
         r = link[j];
         // assume Exponential sampler: param = lambda
         x += father.getLink(r).getParam();  // A MODIFIER le GET PARAM recupere reliabilty
      }

      Lam[b] = x;
      // now the links with small weights
      for (int j = b - 1; j >= 0; j--) {
         r = link[j];
         Lam[j] = Lam[j + 1] + father.getLink(r).getParam();  // A MODIFIER Le getPARAM recupere
      }
      return Lam;
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


   public void setForest(Forest forest) {
      this.forest = forest;
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
