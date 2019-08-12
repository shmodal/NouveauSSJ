package umontreal.ssj.networks.flow;


import umontreal.ssj.rng.*;
import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.probdist.*;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.*;
import java.util.HashMap;
import java.util.Arrays;



/** PMC,sans filtre, avec filtre
 *
 * 
 */
public class PMCFlow {

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

   

   
   public PMCFlow(GraphFlow graph) {
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
   
   
   // Initailisation des capacites et probas de façon spécifique(celle de l'article)
   // on peut vouloir donner des capacités et probas différentes, il faudra modifier
   
   public void initCapaProbaB(int[] tableauB, double rho, double epsilon) {
	   initB(tableauB);
	   initCapacityValues();
	   initProbabilityValues(rho,epsilon);
   }
   
   //initialiser capacités allant de 0 à capaMax, et probabilités uniformes
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
   
   
   protected double doOneRun(RandomStream stream,int demand) {
	   trimCapacities(demand);
	   
	   drawY(stream); // initialise les lambda et tire les Yi, pour toutes les arêtes i
	   initJumpAndIndexes();//initialise le tableau S, les lambdatilde
	   double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut
	   
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
	   //MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father,residual);
	   int maxFlow = Ek.EdmondsKarp();
	   
	   int p=0; // parcours du tableau des valeurs de Y
	   // Il est necessaire de distinguer p et j, car comme on filter, on tombe sur des
	   // valeurs Y dans le talbeau Y_values, mais dont le Si,k est nul. Elles ne sont donc
	   // pas rajoutées comme lien et j ne doit pas etre incrémenté   
	   while (maxFlow < demand && j <Lam.length-1 && p<K) {

		   double y = valuesY[p]; // Y(pi(j))
		   //System.out.println();
		   //System.out.println("Valeur de Ypi(J)"+ y);
		   int [] indices = permutation.get(y);
		   //System.out.println();
		   //System.out.println("Indices récupérés");
		   int i = indices[0];
		   int k = indices[1];
		   //System.out.println("i : " + i + "   k : " +k);
		   LinkFlow EdgeI = father.getLink(i);
		   int s = EdgeI.getJump(k);
		   if (s==1) {
			   //System.out.println();
			   //System.out.println("Jump détecté");
			   double l = EdgeI.getLambdaTilde(k);
			   
			   Lam[j+1] = Lam[j] - l;  // Veifier que FIlterSIngle et FIlterall font bien leur taf
			   father.setJump(i, k, 0);
			   int prevCapacity = father.getLink(i).getCapacity();
			   boolean reload =Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) - prevCapacity  );
			   
			   
			  father.setCapacity(i, EdgeI.getCapacityValue(k+1)); 
			   if (reload) {
				   maxFlow = Ek.EdmondsKarp();
			   }

			   if (filter && maxFlow > level*demand) {
		   double sumLamb = FilterSingle(i,k,demand);
		   if (sumLamb >=0) {
			   Lam[j+1] = Lam[j] - sumLamb; // A VERIFIER
		   }
			   }
			   j++; 
		   }
		   
	   p++;	   
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
   
	// 
	
	protected double doOneRunFilterOutside(RandomStream stream,int demand) {
		trimCapacities(demand);
		int m= father.getNumLinks();
		for (int i=0;i<m;i++) {
			father.getLink(i).outsideFlow=0;
		}

		drawY(stream); // initialise les lambda et tire les Yi, pour toutes les arêtes i
		initJumpAndIndexes();//initialise le tableau S, les lambdatilde
		double [] valuesY = Y_values();  // j'ai les Y triés, et les hash maps comme il faut
		int K = valuesY.length; // LE tableau LAM est de taille : 1(le lambda 1 initial) + le nombre de sauts
		initLamb(K+1);

		int[] X = buildX();
		father.setCapacity(X);
		MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
		int maxFlow = Ek.EdmondsKarp();

		GraphFlow copy = prepareMultiFlow();
		MaxFlowEdmondsKarp EkOut = new MaxFlowEdmondsKarp(copy);

		int j = 0;
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
				Lam[j+1] = Lam[j] - l; 
				father.setJump(i, k, 0);
				

				int prevCapacity = father.getLink(i).getCapacity();
				boolean reload = Ek.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) -prevCapacity);
				father.setCapacity(i, EdgeI.getCapacityValue(k+1));

				if (reload) {
					maxFlow = Ek.EdmondsKarp();
				}
				///For FilterOutside
				prevCapacity = EkOut.network.getLink(i).getCapacity();
				reload = EkOut.IncreaseLinkCapacity(i,EdgeI.getCapacityValue(k+1) -prevCapacity);
				EkOut.network.setCapacity(i, EdgeI.getCapacityValue(k+1));
				
				if (reload) {
					EkOut.EdmondsKarp();
				}
				
				if (p%frequency ==0 && p>(seuil*K) && maxFlow > level*demand) { //mise a jour updateFlow. //Proposer aussi p depasse un seuil ? ?

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
				j++;
			}
			p++;

		}
		//System.out.println("MaxFLow : " + maxFlow);
		criticalLink.add(j);
		double ell = computeBarF(Lam,j);
		return ell;
	}
   
   
   
   
   
   public double run(int n, RandomStream stream,int demand) {
	      Chrono timer = new Chrono();
	      timer.init();
	      Tally values = new Tally(); // unreliability estimates
	      for (int j = 0; j < n; j++) {
	    	  //stream.resetNextSubstream();
	    	  double x;
	    	  if (filterOutside) {x = doOneRunFilterOutside(stream,demand);}
	    	  else {x = doOneRun(stream,demand);}
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
   
   
   
   
   // Attention, faire le filtrage seulement à partir du Si,k 
   //(les capacités qui sont supérieures à celle actuelle)
   
   
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
   
	
   public double FilterOutside(int i, int k, int demand) {
	   LinkFlow EdgeI = father.getLink(i);
	   int outsideFlow = EdgeI.outsideFlow;
	   int newCapa = EdgeI.getCapacity();
	   if (outsideFlow + newCapa >= demand) {
		   int b = EdgeI.getB();
		   double sumLamb = 0.;
		   for (int j=k+1;j<b;j++) {
			   int s= EdgeI.getJump(j);
			   if(s==1) {
				   sumLamb += EdgeI.getLambdaTilde(j);
				   EdgeI.setJump(j, 0);
			   }
		   
	   }
		   return sumLamb;
	   }
	   else {return (-1.0);}
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
   
   
   
   
   

   
   // Attention, il faut verifie rplus tard que toutes les valeurs de Lamb sont initialisées 
   // à un moment(dans l'étape finale du pseudo code)
   
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
   }
   
   // on prend le graphe de base. On rajoute 2 sommets supplémentaires. On cree un EK 
   // dont c'est sorce et sink. On les relie à toutes les sommets(2 sens ?) avec
   // capa nulle initialement.
   //on retient l'ancien lien où on a modifié les capacités, et on les update à 0.
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
