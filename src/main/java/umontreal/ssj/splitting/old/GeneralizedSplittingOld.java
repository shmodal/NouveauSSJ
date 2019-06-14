package umontreal.ssj.splitting.old;

import umontreal.ssj.*;
import java.util.LinkedList;
import java.util.List;


import umontreal.ssj.networks.*;
import umontreal.ssj.networks.old.GraphOld;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.old.MarkovChainOld;
import umontreal.ssj.networks.staticreliability.old.PMC;
import umontreal.ssj.networks.staticreliability.old.Turnip;
import umontreal.ssj.util.Tools;
//import connectivity.*;
//import util.*;
//import graph.*;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.stat.*;
import umontreal.ssj.stat.list.*;

/**
 * 
 * Implements the generalized splitting algorithm. This algorithm is used to
 * obtain an estimate of a rare event probability via Gibbs sampling. See
 * Zdravko thesis for more details. The main parameters to run the algorithm
 * are:
 * - a Markov chain
 * - a number of Markov chain at the beginning of the simulation
 * - the splitting factors
 * - the gamma levels
 *
 * 
 */
public class GeneralizedSplittingOld {
	
	/**
	 * Estimate of rare-event probability.
	 */
	private double m_estimate; // estimate of rare-event probability
	/**
	 * Estimate of variance.
	 */
	private double var; // estimate of variance
	/**
	 * Stores the value for each run.
	 */
	private TallyStore store; // store the value for each run
	/**
	 * If true, stores value for each run. False is when we only want to
	 * measure the time needed to run the algorithm.
	 */
	private boolean storeFlag; // true: store value for each run
	/**
	 * Stores counters of value for each run.
	 */
	private TallyHistogram hist; // store counters of value for each run
	/**
	 * If true, stores number of values in bin counters.
	 */
	private boolean histFlag; // true: store number of values in bin counters
	/**
	 * One Tally for each edge of the graph.
	 */
	private ListOfTallies<TallyStore> edgeTallies; // one Tally for each edge
	/**
	 * If true, use Tally for each edge.
	 */
	private boolean edgeFlag; // true: use Tally for each edge
	/**
	 * UNUSED
	 */
	private TallyStore store0;
	/**
	 * UNUSED
	 */

	private TallyStore store1;
	/**
	 * UNUSED 
	 * If true: keep state for turnip test
	 */
	private boolean turnFlag; // true: keep state for turnip test

	/**
	 * UNUSED 
	 * If turnFlag is true, stores PMC class to compare results and length of run.
	 */
	private PMC turn;

	/**
	 * Gives an estimate of rare-event probability by the splitting algorithm
	 * with Gibbs sampling and fixed gamma levels. Does one run with one Markov
	 * chain.
	 * 
	 * @param mother
	 *           initial Markov chain to be cloned
	 * @param gamma
	 *           gamma levels
	 * @param split
	 *           splitting factor
	 * @param stream
	 *           random stream
	 * @return estimate
	 */
	private double doOneChain(MarkovChainOld mother, double[] gamma, int split,
			RandomStream stream) {
		int tau = gamma.length - 1; // number of levels
		final double stau = Math.pow(split, tau - 1);

		List<MarkovChainOld> list0 = new LinkedList<MarkovChainOld>();
		List<MarkovChainOld> list1 = new LinkedList<MarkovChainOld>();

		MarkovChainOld chain = mother.clone();
		chain.initialState(stream);
		chain.setGammaLevel(gamma[1]);
		if (!chain.isImportanceGamma(gamma[1])) {
			return 0.0;
		}
		list1.add(chain);

		int t = 1; // gamma level = t;

		// iterate over all levels of gamma for this chain
		while (t < tau && list1.size() > 0) {
			// select all Markov chains on the list of old survivors
			for (MarkovChainOld chain1 : list1) {
				MarkovChainOld newchain = chain1.clone();
				for (int j = 0; j < split; j++) {
					// Markov sampling; advance split steps
					newchain.nextStep(stream);
					list0.add(newchain);
				}
			}
			list1.clear();

			t++;
			for (MarkovChainOld chain0 : list0) {
				chain0.setGammaLevel(gamma[t]);
				if (chain0.isImportanceGamma(gamma[t]))
					list1.add(chain0);
			}
			list0.clear();
		}

		if (t >= tau) {
			// if (edgeFlag)
			// addEdgesTallies (list1);
			// if (storeFlag)
			//	addStores(list1);
			// if (turnFlag)
			// callTurnip (list1);

			// Count number of final chains that have initial ancestor i
			int r = list1.size();
			list1.clear();
			return r / stau;
		}
		// evolution stopped before T since 0 chain survived
		list1.clear();
		return 0.0;
	}

	/**
	 * Gives an estimate of rare-event probability by the splitting algorithm
	 * with Gibbs sampling and fixed gamma levels.
	 * 
	 * @param mother
	 *           mother of all Markov chains
	 * @param numRun
	 *           number of runs
	 * @param Gamma
	 *           gamma levels
	 * @param split
	 *           splitting factor
	 * @return duration of the run
	 */
	public double run(MarkovChainOld mother, int numRun, double[] Gamma, int split,
			RandomStream stream) {
		if (numRun < 2)
			throw new IllegalArgumentException("numRun < 2");
		Chrono timer = new Chrono();
		timer.init();
		Tally values = new Tally(); // unreliability estimates

		mother.setGammaLevel(Gamma[0]);
		for (int i = 0; i < numRun; i++) {
			double x = doOneChain(mother, Gamma, split, stream);
			values.add(x);
			if (storeFlag)
				store.add(x);
			if (histFlag)
				hist.add(Math.log10(x));
		}

		double ell = m_estimate = values.average();
		var = values.variance();
		double sig = Math.sqrt(var);
		double relvar = var / (ell * ell); // relative variance

		System.out.printf("barW_n      = %g%n", m_estimate);
		System.out.printf("S_n         = %g%n", sig);
		System.out.printf("var = S_n^2 = %g%n", var);
		System.out.printf("var/n       = %g%n", var / numRun);
		System.out.printf("rel var(W)  = %g%n%n", relvar);
		System.out.println(values.formatCINormal(0.95, 4));
		// System.out.print("num obs = " + values.numberObs());

		double relerr = sig / (ell * Math.sqrt(numRun)); // relative error
		System.out.printf("rel err(barW_n) = %g%n", relerr);

		double cro = timer.getSeconds();
		double tem = cro * var / numRun;
		System.out.printf("time*var/n      = %g%n", tem);
		System.out.printf("time*var/(n*barW_n^2) = %g%n%n", tem / (ell * ell));
		System.out.printf("CPU time:   %.1f  sec%n%n%n", cro);

		//if (edgeFlag)
		//	printEdgesTallies(mother.getForest().getFather());
		//if (storeFlag) {
		//	printStores();
		// calcCorrelation();
		// }

		return cro;
	}

	/**
	 * Returns the variance after a run.
	 * 
	 * @return the variance
	 */
	public double getVariance() {
		return var;
	}

	/**
	 * Returns the estimate of the network reliability.
	 * 
	 * @return the estimate
	 */
	public double getEstimate() {
		return m_estimate;
	}

	/**
	 * Store the estimate W for each run in a TallyStore. The
	 * TallyStore has a maximum capacity of <i>n</i> values. Sets <i>flag</i>
	 * <b>true</b> to store each value, <b>false</b> otherwise. Usually, values
	 * for each run are not stored and only the average is kept.
	 * 
	 * @param flag
	 *           if true, store the values, otherwise not
	 * @param n
	 *           number of values to store (number of runs)
	 */
	public void initStore(boolean flag, int n) {
		storeFlag = flag;
		if (flag) {
			store = new TallyStore(n);
			// store0 = new TallyStore(n);
			// store1 = new TallyStore(n);
		} else
			store = null;
	}

	/**
	 * Gets all estimates W's stored in the TallyStore.
	 */
	public TallyStore getStore() {
		return store;
	}

	/**
	 * Bin counters for the log10 of the estimate for each run. There are <i>s</i>
	 * bins dividing the interval [a, b] equally. If <i>flag</i> is <b>true</b>,
	 * use bin counters, otherwise not. By default, the bin counters are not
	 * used.
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
	 * Gets the histogram.
	 */
	public TallyHistogram getHistogram() {
		return hist;
	}
	
	/**
	 * Inits Turnip if necessary
	 */

	public void initTurn(boolean flag, GraphOld graph, GraphWithForest forest) {
		turnFlag = flag;
		if (flag)
			turn = new Turnip(graph, forest);
		else
			turn = null;
	}

	/**
	 * Creates Tally's to keep statistics for each edge of the graph
	 * 
	 * @param flag
	 * @param graph
	 */
	public void initEdgeTallies(boolean flag, GraphOld graph) {
		edgeFlag = flag;
		if (flag) {
			// Tally's to keep statistics for each edge of the graph
			int numLinks = graph.getNumLinks();
			edgeTallies = ListOfTallies.createWithTallyStore(numLinks);
		} else
			edgeTallies = null;
	}

	/**
	 * Adds the length of each edge to its associated Tally. The edges are those
	 * of the forest associated to each <tt>chain</tt> of the list.
	 * 
	 * @param list
	 *           of chains
	 */
	private void addEdgesTallies(List<MarkovChainOld> list) {
		for (MarkovChainOld chain : list) {
			double[] W = chain.getForest().getWeight();
			edgeTallies.add(W);
		}
	}

	/**
	 * Prints average lengths of edges and correlation between edge lengths.
	 * 
	 * @param graph
	 */
	private void printEdgesTallies(GraphOld graph) {
		int numLinks = graph.getNumLinks();
		double[] ave = new double[numLinks];
		edgeTallies.average(ave);
		for (int i = 0; i < numLinks; i++) {
			System.out.printf("Edge %2d     mu = %g%n", i, ave[i]);
		}
		System.out.printf("%nCorrelations:%n");
		for (int i = 0; i < numLinks; i++) {
			for (int j = 0; j < numLinks; j++) {
				double x = edgeTallies.correlation(i, j);
				System.out.printf(" %2d   %2d   %12.5g%n", i + 1, j + 1, x);
			}
		}
	}

	/**
	 * UNUSED 
	 */
	private void addStores(List<MarkovChainOld> list) {
		double x;
		for (MarkovChainOld chain : list) {
			double[] W = chain.getForest().getWeight();
		//	x = (W[0] + W[1] + W[2]) / 3.0;
			x = Math.min(W[0], W[1]);   x = Math.min(x, W[2]);
			store0.add(x);
		//	x = (W[27] + W[28] + W[29]) / 3.0;
			x = Math.min(W[27], W[28]);   x = Math.min(x, W[29]);
			store1.add(x);
		}
	}

	private void printStores() {
		System.out.println(" Store_0:\n" + store0.toString());
		System.out.println("----------------------------------------\n ");
		System.out.println(" Store_1:\n" + store1.toString());
	}

	private void calcCorrelation () {
		double sig0 = store0.variance();
		System.out.println(" Store_0 var = " + sig0);
		double sig1 = store1.variance();
		System.out.println(" Store_1 var = " + sig1);
		double covar = store0.covariance(store1);
		System.out.println(" Covar = " + covar);
		double cor = covar / (Math.sqrt(sig0)*Math.sqrt(sig1));
		System.out.println(" Correlation (R1,R2) = " + cor);		
	}
	
	
	/**
	 * Calls turnip on the states obtained at the last GS level	
	 */
	private void callTurnip(List<MarkovChainOld> list, int split) {
		for (MarkovChainOld chain : list) {
			GraphWithForest forest = chain.getForest();
			turn.setForest(forest);
			double x = 0;
		//	x = ((Turnip) turn).doOneRun();
			hist.add(Math.log10(x));
			System.out.println(x);
		}
	}
}
