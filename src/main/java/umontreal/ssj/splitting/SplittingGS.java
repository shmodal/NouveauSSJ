package umontreal.ssj.splitting;



import java.util.LinkedList;
import java.util.List;

//import umontreal.ssj.util.Tools;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.stat.*;


public class SplittingGS {
	
	private double gamma_level ;
	
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

	
	private double doOneChain(MarkovChainWithImportance mother, double[] gamma, int split,
			RandomStream stream) {
		int tau = gamma.length - 1; // number of levels
		final double stau = Math.pow(split, tau - 1);

		LinkedList<MarkovChainWithImportance> list0 = new LinkedList<MarkovChainWithImportance>();
		LinkedList<MarkovChainWithImportance> list1 = new LinkedList<MarkovChainWithImportance>();

		MarkovChainWithImportance chain = mother.clone(); 
		chain.initialState(stream,0);
		gamma_level = gamma[1];
		chain.updateChainGamma(gamma_level);
		if (!chain.isImportanceGamma(gamma_level)) {
			return 0.0;
		}
		list1.add(chain);
		
		int t = 1; // gamma level = t;

		// iterate over all levels of gamma for this chain
		while (t < tau && list1.size() > 0) {
			// select all Markov chains on the list of old survivors
			for (MarkovChainWithImportance chain1 : list1) {
				MarkovChainWithImportance newchain = chain1.clone();
				for (int j = 0; j < split; j++) {
					// Markov sampling; advance split steps
					newchain.nextStep(stream,gamma_level);
					list0.add(newchain);
				}
			}
			list1.clear();

			t++;
			for (MarkovChainWithImportance chain0 : list0) {
				gamma_level = gamma[t];
				chain0.updateChainGamma(gamma_level);
				if (chain0.isImportanceGamma(gamma_level))
					list1.add(chain0);
			}
			list0.clear();
		}

		if (t >= tau) {

			// Count number of final chains that have initial ancestor i
			int r = list1.size();
			list1.clear();
			return r / stau;
		}
		// evolution stopped before T since 0 chain survived
		list1.clear();
		return 0.0;

	}
	
	
	
	public double run(MarkovChainWithImportance mother, int numRun, double[] Gamma, int split,
			RandomStream stream) {
		if (numRun < 2)
			throw new IllegalArgumentException("numRun < 2");
		Chrono timer = new Chrono();
		timer.init();
		Tally values = new Tally(); // unreliability estimates
	
		gamma_level = Gamma[0];
		//mother.setGammaLevel(Gamma[0]);
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
	









	


}
