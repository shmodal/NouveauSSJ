package umontreal.ssj.networks.staticreliability;

//import sampling.SamplerType;
//import graph.*;
import umontreal.ssj.rng.*;
import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.LinkReliability;
import umontreal.ssj.probdist.*;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.*;

/**
 * This class implements the Permutation Monte Carlo method to estimate the
 * reliability of a network. It uses the constructive schema, i.e., it assumes
 * that initially, all links are failed and then add working links until the
 * network becomes operational, i.e., the subset of nodes V0 becomes connected.
 *
 * The results are stored in a Tally named store and then reliability and variance are computed.
 */



public class PMC {

	/**
	 * Variance of the results obtained by Monte Carlo runs.
	 */
   private double m_variance; // variance
   /**
    * Network reliability.
    */
   private double m_ell; // network reliability
   /**
    * Stores the value for each run.
    */
   private TallyStore store; // store the value for each run
   
   /**
    * If true, stores the value for each run.
    */
   private boolean storeFlag; // true: store value for each run
   /**
    * Stores counters of value for each run.
    */
   private TallyHistogram hist; // store counters of value for each run
   /**
    * If true, stores values for each run in bin counters.
    */
   private boolean histFlag; // true: store values for each run in bin counters
   /**
    * Forest associated with the network.
    */
   protected GraphWithForest forest;
   /**
    * Same father as in forest.
    */
   protected GraphReliability father; // same father as in forest
   
   /**
    * HypoExponentialDist flag (choice of numerical computation for the network
    * reliability), see setHypoExpKind.
    */
   private int hekind; // HypoExponentialDist flag; see setHypoExpKind
   /**
    * Array of Lambda(i), used to compute repair time of the graph.
    */
   protected double[] Lam; // compound Lambda's
   
   Tally criticalLink; // critical link
   // for reverse or direct scan in finding critical link
   /**
    * for reverse or direct scan in finding critical link. 
    * True for reverse scan, false otherwise. False for this class.
    */
   protected boolean antiScanFlag;  // true for reverse scan, false otherwise
   /**
    * for using shocks. False for this class.
    */
   protected boolean shockFlag;

   /**
    * Constructor. By default, hekind = 1 (quick but sometimes
    * numerically unstable formula to compute reliability), exponential sampler
    */
   
   public PMC(GraphReliability graph, GraphWithForest forest) {
      father = graph;
      this.forest = forest;
      father.setSampler(SamplerType.EXPONENTIAL, false);
      criticalLink = new Tally();
      storeFlag = false;
      histFlag = false;
      int m = father.getNumLinks();
      Lam = new double[m + 1];
      hekind = 1;
      antiScanFlag = false;
      shockFlag = false;
   }
   
   /**
    * Runs the PMC algorithm n times and computes the gamma levels.
    *
    * @param mother mother of all Markov chains created by the ADAM Algorithm
    * @param N constant number of Markov chains at the beginning of each level
    * @param s splitting factor
    * @param stream to advance the Markov chains
    * @param firstGammaLevel the first level
    * @param lastGammaLevel the target level
    */
   

   public double run(int n, RandomStream stream) {
      Chrono timer = new Chrono();
      timer.init();
      Tally values = new Tally(); // unreliability estimates
      for (int j = 0; j < n; j++) {
         double x = doOneRun(stream);
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

   public double doOneRun(RandomStream stream) {
      // Draw independent repair times
      drawRepairTimes(stream);
      int m = father.getNumLinks();
      double[] A = new double[m];
      double[] res = forest.getRepairTime(A);
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
    * Draws repair times (stored as links weights) from the exponential distribution. The
    * rate parameters (lambda) of the exponential distribution must have been
    * precomputed(stored in parameter) for each link before calling this method.
    *
    * @param stream
    */
   protected void drawRepairTimes(RandomStream stream) {
      int m = father.getNumLinks();
      double w, lambda;
      for (int j = 0; j < m; j++) {
         LinkReliability link = father.getLink(j);
         lambda = link.getParam();
         w = ExponentialDist.inverseF(lambda, stream.nextDouble());
         forest.setWeight(j, w);
      }
   }

   /**
    * Computes rates Lambda(i) such that Lambda(i) corresponds to
    * the sum of lambda(link), over links that have not been repaired yet.
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
         x += father.getLink(r).getParam();
      }

      Lam[b] = x;
      // now the links with small weights
      for (int j = b - 1; j >= 0; j--) {
         r = link[j];
         Lam[j] = Lam[j + 1] + father.getLink(r).getParam();
      }
      return Lam;
   }


   /**
    * Computes P(A_1+...+A_b1> 1) exactly, where
    * A_i ~ Exp(Lambda(i)) independently; Lambda has to be decreasing (sorted)
    * sequence. See the book \cite{pGER10a} on page 188 formula 5 for the
    * convolution of exponentials. b = b1 - 1.
    * It computes the result depending on the value of <tt>hekind</tt> (HypoExpKind).
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
    * Returns the variance of the network reliability.
    *
    * @return the variance
    */
   public double getVariance() {
      return m_variance;
   }

   /**
    * Returns the estimate of the network reliability.
    *
    * @return the estimate
    */
   public double getEstimate() {
      return m_ell;
   }

   @Override
   public String toString () {
      return getClass().getSimpleName();
   }


   /**
    * Creates a TallyStore to store the estimate of the unreliability W for each
    * run. The TallyStore has a maximum capacity of <i>n</i> values. One sets
    * <i>flag</i> <b>true</b> if one wants to store each value W, <b>false</b>
    * otherwise. By default, values of W for each run are not stored and only
    * the average is kept.
    *
    * @param flag
    *           if true, store the estimates W, otherwise not
    * @param n
    *           number of values to store (number of runs)
    */
   public void initStore(boolean flag, int n) {
      storeFlag = flag;
      if (flag)
         store = new TallyStore(n);
      else
         store = null;
   }

   /**
    * Returns the values stored in the TallyStore.
    */
   public TallyStore getStore() {
      return store;
   }

   /**
    * Creates bin counters for the log10 of the estimate of the unreliability W
    * for each run. There are <i>s</i> bins dividing the interval [a, b] in
    * equal segements; the counters count how many values of W fall in each
    * segment (or bin). This is useful if one wants to create a histogram of the
    * values of W. If <i>flag</i> is <b>true</b>, use the bin counters,
    * otherwise not. By default, the bin counters are not used.
    *
    * @param flag
    *           if true, bin counters are used, otherwise not
    * @param a
    *           left boundary of first interval
    * @param b
    *           right boundary of last interval
    * @param s
    *           number of bins
    */
   public void initHistogram(boolean flag, double a, double b, int s) {
      histFlag = flag;
      if (flag) {
         hist = new TallyHistogram(a, b, s);
      } else {
         hist = null;
      }
   }

   /**
    * Returns the histogram tally.
    */
   public TallyHistogram getHistogram() {
      return hist;
   }

   public void setForest(GraphWithForest forest) {
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
   
   /**
    * Reduces the length of Lambda to b.
    */

   double[] trimLam (double[] Lambda, int b) {
      double[] tLam = new double[b];
      System.arraycopy (Lambda, 0, tLam, 0, b);
      return tLam;
   }
}
