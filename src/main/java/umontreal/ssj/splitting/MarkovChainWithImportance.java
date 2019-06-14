package umontreal.ssj.splitting;

import umontreal.ssj.rng.*;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.hups.PointSet;
import umontreal.ssj.hups.PointSetRandomization;
import umontreal.ssj.markovchainrqmc.MarkovChain;

/**
 * Subclass of MarkovChain (class in umontreal.ssj.markovchainrqmc) 
 * Abstract class, extended for example by MarkovChainNetworkReliability and al. 
 * Implements one step of Markov chain for use in splitting algorithms, with importance function.
 * Sub-classes may override the test of whether the importance function is greater than a gamma 
 * level, if it is more efficient. (case of static reliability networks).
 * 
 */

public abstract class MarkovChainWithImportance extends MarkovChain implements Cloneable {
	   //protected RandomStream streamPermut; // for random permutations of links
	   	   
	   public MarkovChainWithImportance() {
		   super();   
		   //this.streamPermut = streamPermut;
		   }
	   
	   @Override
	   public MarkovChainWithImportance clone() {
	      MarkovChainWithImportance image = null;
	      try {
	         image = (MarkovChainWithImportance) super.clone();
	      } catch (CloneNotSupportedException e) {
	      }
	      //image.streamPermut = streamPermut;

	      return image;
	   }
	   
	   /**
	    * Initializes the Markov chain, i.e. does one step of the Markov chain.
	    * @param stream of U(0,1) random numbers
	    * @param gamma gamma level, 0 in most of the cases (unconditional sampling)
	    * 
	    */
	   
	   public void initialState(RandomStream stream, double gamma) {
	      nextStep(stream,gamma);

	   }
	   
	   /**
	    * Does one step of the Markov chain.
	    * 
	    * @param stream of U(0,1) random numbers
	    * @param gamma current gamma level
	    *           
	    */
	   public abstract void nextStep(RandomStream stream,double gamma);
	   
	   /**
	    * Get importance function of the Markov chain.
	    * Doit forcément renvoyer un double ?
	    * 
	    */
	   
	   public abstract double getImportance();
	   
	   
	   /**
	    * Tests if the Markov Chain is above the given gamma level.
	    * 
	    * @param gam gamma level
	    *           
	    * @return true if importance function is above gamma
	    */
	   
	   // Can be overridden if there is a more effective way to compute it
	   // For example, in MarkovChainConnectivyState, a forest is retained
	   public boolean isImportanceGamma(double gam) {
		   return getImportance()>gam;
	   }
	   
	   
	   //update the Markov chain according to the gamma, puisqu'on ne retient plus gamma
	   // dans la MC.
	   // peut ne rien faire si ya pas besoin.
	   // Ici, par ex dans connectiivitystate, ca met à jour la foret pour optimiser
	   
	   /**
	    * Updates parameters of the MarkovChain according to the gamma level. 
	    * By default, does nothing. Can be overriden in subclasses.
	    * 
	    * @param gamma level
	    */
	   public void updateChainGamma(double gamma) {
		   
	   }
	   
	   
	   // RQMC functions updated
	   
	   

	   /*Not to be used */
	   public void initialState() {};
	   /*Not to be used */
	   public void nextStep (RandomStream stream) {};
	   /*Not to be used */
	   public void simulSteps (int numSteps, RandomStream stream) {};
	   /*Not to be used */
	   public void simulSteps (RandomStream stream) {}
	   /*Not to be used */
	   public void simulRuns (int n, int numSteps, RandomStream stream,
               Tally statRuns) {}
	   /*Not to be used */
	   public void simulRunsWithSubstreams (int n, int numSteps,
               RandomStream stream, Tally statRuns) {}
	   /*Not to be used */
	   public double simulMC (int n, int numSteps) {return 0;}
	   /*Not to be used */
	   public double simulMC (int n) {return 0;}
	   /*Not to be used */
	   public void simulRepMC (int n, int numSteps, int m, Tally t) {}
	   /*Not to be used */
	   public void simulRepMC (int n, int m, Tally t) {}
	   /*Not to be used */
	   public void simulRQMC (PointSet p, int m, int numSteps,
	                          PointSetRandomization rand, Tally statReps) {}
	   /*Not to be used */
	   public String simulRunsFormat (int n, int numSteps, RandomStream stream,
	                                  Tally statRuns) {return "";}
	   /*Not to be used */
	   public String simulRunsWithSubstreamsFormat (int n, int numSteps,
	                                                RandomStream stream,
	                                                Tally statRuns) {return "";}
	   /*Not to be used */
	   public String simulRQMCFormat (PointSet p, int m, int numSteps,
	                                  PointSetRandomization rand, Tally statReps) {return "";}
	   /*Not to be used */
	   public String testImprovementRQMCFormat (PointSet p, int m, int numSteps,
               PointSetRandomization rand, double varMC,
               Tally statReps) {return "";}

	      
	   
	   /**
	    * Starts a new simulation run and simulates `numSteps` steps of the
	    * Markov chain or until the chain stops, using the given `stream`.
	    */
	   
	   public void simulSteps (int numSteps, RandomStream stream,double gamma) {
	        initialState ();
	        this.numSteps = numSteps;
	        int step = 0;
	        while (step < numSteps && !hasStopped()){
	            nextStep (stream,gamma);
	            ++step;
	        }
	    }
	   
	   /**
	    * Starts a new simulation run and simulates until the stopping time is
	    * reached, using the given <tt>stream</tt>. Same as
	    * {@link #simulSteps() simulSteps(Integer.MAX_VALUE, stream)}.
	    */
	   public void simulSteps (RandomStream stream,double gamma) {
	       simulSteps (Integer.MAX_VALUE, stream,gamma);
	   }
	   
	   
	   
	   /**
	    * Performs `n` simulation runs of the chain, for `numSteps` steps per
	    * run, using the given `stream`. The statistics on the performance for
	    * the `n` runs are placed in `statRuns`.
	    */
	   public void simulRuns (int n, int numSteps, RandomStream stream,double gamma,
	                          Tally statRuns) {
	        statRuns.init ();
	        for (int i = 0; i < n; i++) {
	            simulSteps (numSteps, stream, gamma);
	            statRuns.add (getPerformance ());
	        }
	    }
	   
	   
	   /**
	    * Same as  #simulRuns, except that the stream is first reset to its
	    * initial seed and then reset to the first substream at the beginning
	    * and to the next substream after each run.
	    */
	   public void simulRunsWithSubstreams (int n, int numSteps,
	                                        RandomStream stream, double gamma, Tally statRuns) {
	        statRuns.init ();
	        stream.resetStartStream ();
	        for (int i = 0; i < n; i++) {
	            simulSteps (numSteps, stream,gamma);
	            statRuns.add (getPerformance ());
	            stream.resetNextSubstream ();
	        }
	    }
	   
	   
		/**
		 * Perform n simulation runs of the chain, each for numSteps steps, and
		 * returns average.
		 */
		public double simulMC (int n, int numSteps,double gamma) {
			Tally statRuns = new Tally();
			simulRunsWithSubstreams (n, numSteps, new MRG32k3a(),gamma, statRuns);
			return statRuns.average();
		}
	   
		/**
		 * Perform n runs, each one until the chain stops.
		 */
		public double simulMC (int n,double gamma) {
			  return simulMC (n, Integer.MAX_VALUE,gamma);
	   }
		
		/**
		 * Perform n runs and compute the average, reapeat m times and return the
		 * stats in t.
		 */
		public void simulRepMC (int n, int numSteps, int m,double gamma, Tally t) {
			 for (int rep = 0; rep < m; ++rep) {
	 			  t.add (simulMC (n, numSteps,gamma));
	     } 
		}
		
		/**
		 * Same as previous one, but run the chains until they stop.
		 */
		public void simulRepMC (int n, int m, double gamma, Tally t) {
			simulRepMC (n, Integer.MAX_VALUE, m,gamma, t);
	   }
		
		
	   /**
	    * Performs `m` independent replicates of @f$n@f$ simulation runs of
	    * the chain using a RQMC point set, each time storing the average of
	    * the performance over the @f$n@f$ chains. @f$n@f$ is the number of
	    * points in RQMC point set `p`. Each run goes for `numSteps` steps.
	    * For each replicate, the point set `p` is randomized using `rand`, an
	    * iterator is created, and each run uses a different substream of this
	    * iterator (i.e., a different point). The statistics on the
	    * performance for the `m` independent replications are placed in
	    * `statReps`.
	    */
	   public void simulRQMC (PointSet p, int m, int numSteps,
	                          PointSetRandomization rand,double gamma, Tally statReps) {
	        statReps.init ();
	        Tally statRuns = new Tally ();   // Used within the runs.
	        int n = p.getNumPoints();        // Number of points.
	        RandomStream stream = p.iterator ();
	        for (int rep = 0; rep < m; rep++) {
	            p.randomize(rand);
	            simulRunsWithSubstreams (n, numSteps, stream, gamma, statRuns);
	            statReps.add (statRuns.average ());
	        }
	    }		
	   
	   
	   /**
	    * Same as  #simulRuns but also returns the results as a formatted
	    * string.
	    */
	   public String simulRunsFormat (int n, int numSteps, RandomStream stream,double gamma,
	                                  Tally statRuns) {
	        timer.init ();
	        simulRuns (n, numSteps, stream,gamma, statRuns);
	        StringBuffer sb = new StringBuffer
	           ("----------------------------------------------" +
	                PrintfFormat.NEWLINE);
	        sb.append ("MC simulations:" + PrintfFormat.NEWLINE);
	        sb.append (" Number of runs n          = " + n  +
	                PrintfFormat.NEWLINE);
	        sb.append (formatResults (statRuns));
	        sb.append (" CPU Time = " + timer.format () + PrintfFormat.NEWLINE);
	        return sb.toString ();
	    }
	   
	   

	   /**
	    * Same as  #simulRunsWithSubstreams but also returns the results as a
	    * formatted string.
	    */
	   public String simulRunsWithSubstreamsFormat (int n, int numSteps,
	                                                RandomStream stream,double gamma,
	                                                Tally statRuns) {
	        timer.init ();
	        simulRunsWithSubstreams (n, numSteps, stream,gamma, statRuns);
	        StringBuffer sb = new StringBuffer
	           ("----------------------------------------------" +
	             PrintfFormat.NEWLINE);
	        sb.append ("MC simulations with substreams:" + PrintfFormat.NEWLINE);
	        sb.append (" Number of runs n          = " + n  +
	           PrintfFormat.NEWLINE);
	        sb.append (formatResults (statRuns));
	        sb.append (" CPU Time = " + timer.format () + PrintfFormat.NEWLINE);
	        return sb.toString ();
	    }

	   
	   /**
	    * Same as  #simulRQMC but also returns the results as a formatted
	    * string.
	    */
	   public String simulRQMCFormat (PointSet p, int m, int numSteps,double gamma,
	                                  PointSetRandomization rand, Tally statReps) {
	        timer.init();
	        simulRQMC (p, m, numSteps, rand,gamma, statReps);
	        int n = p.getNumPoints();
	        StringBuffer sb = new StringBuffer
	            ("----------------------------------------------" +
	                PrintfFormat.NEWLINE);
	        sb.append ("RQMC simulations:" + PrintfFormat.NEWLINE +
	                PrintfFormat.NEWLINE);
	        sb.append (p.toString ());
	        sb.append (PrintfFormat.NEWLINE + " Number of indep. randomization, m = "
	             + m  + PrintfFormat.NEWLINE);
	        sb.append (" Number of points n        = "+ n  +
	                    PrintfFormat.NEWLINE);
	        sb.append (formatResultsRQMC (statReps, n));
	        sb.append (" CPU Time = " + timer.format () + PrintfFormat.NEWLINE);
	        return sb.toString ();
	    }
	   
	   
	   /**
	    * Similar to  #simulRQMCFormat, but also gives the variance
	    * improvement factor with respect to MC. Assuming that `varMC` gives
	    * the variance per run for MC.
	    */
	   public String testImprovementRQMCFormat (PointSet p, int m, int numSteps,double gamma,
	                                      PointSetRandomization rand, double varMC,
	                                      Tally statReps) {
	      // Removed next line because numSteps may be infinite!
	      // p.randomize (0, numSteps * dimPerStep, noise);
	      StringBuffer sb = new StringBuffer (simulRQMCFormat
	              (p, m, numSteps,gamma, rand, statReps));
	      double var = p.getNumPoints() * statReps.variance();
	      sb.append (" Variance ratio: " +
	           PrintfFormat.format (15, 10, 4, varMC/var) +
	                PrintfFormat.NEWLINE);
	      return sb.toString ();
	}
	   
	   
}
