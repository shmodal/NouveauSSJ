package umontreal.ssj.splitting;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.flow.ExamplesGraphs;
import umontreal.ssj.networks.flow.GraphFlow;
import umontreal.ssj.networks.flow.MChainNew;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.MarkovChainNetworkReliability;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.splitting.tests.TestParams;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.stat.TallyHistogram;
import umontreal.ssj.stat.TallyStore;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Tools;

public class Debug2 {
	
	
	   public static void main(String[] args) throws IOException {
		   // Création du graphe et de la chaine de Markov associée
		   
		   String file= ExamplesGraphs.getGraphfile("lattice2and2");
			GraphFlow g = new GraphFlow(file);
			int b = 1; // taille b+1
			int[] cap = new int[2];cap[0]=0;cap[1]=1;
			double[] prob = new double[2] ;prob[0]=0.1;prob[1]=0.9;
			for (int i = 0;i<g.getNumLinks();i++) {
				g.setB(b);
				g.setCapacityValues(cap);
				g.setProbabilityValues(prob);
			}
			g.resetCapacities();
			RandomStream stream = new LFSR113();
			RandomStream streamP = new MRG31k3p();
			

			MChainNew mother = new MChainNew(g, streamP, 1);
			
			int N=4;
			int s = 2;
			int q = N - N/s;
			double[] tabS = new double[N];

			double lastGammaLevel = 1.0;
			
			////////////////////////////:
			
			
			double current_gamma = 0.0;
		
			
			
			stream.resetNextSubstream();
			
			
			LinkedList <MChainNew> list0 = new LinkedList <MChainNew> ();
			LinkedList <MChainNew> list1 = new LinkedList <MChainNew> ();
			
			
			for (int i = 0; i < N; i++) {
			
				MChainNew chain0 = mother.clone(); 
				chain0.initialState(stream,0);
				printTab(chain0.valuesY);
				tabS[i] = chain0.getImportance();
				list0.add (chain0);
				//System.out.println("=================BONJOUUUUUUUUUUUUR");
			}
			System.out.println("Tableau S");
			printTab(tabS);
			
			current_gamma = getNextGamma (q, tabS, lastGammaLevel);
			System.out.println("Current gamma" +current_gamma);
			
			System.out.println("==============UPDATE GAMMA");
			
			
			for (MChainNew chain: list0) {
				System.out.println("==============UPDATE GAMMA");
				System.out.println("Valeurs de Y");
				printTab(chain.valuesY);
				   chain.updateChainGamma(current_gamma);
				   if (chain.isImportanceGamma (current_gamma)) {
					   list1.add (chain);
					   //
				   }
				   } 
			System.out.println("chaines conservées");
			for (MChainNew chain: list1) {
				printTab(chain.valuesY);
				System.out.println();
			}
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
			
			System.out.println("Verify le nextStep");
			for (MChainNew chain1: list1) {
				MChainNew newchain = chain1.clone();
				System.out.println("Valeurs de Y");
				printTab(newchain.valuesY);
				 newchain.nextStep (stream,current_gamma);
			}
			
			
			
			
			
			

			
	
			
		    
	   }

	   

	   
	   private static void proc(int split, double q, String GraphName) throws IOException {

	      

	      System.out.println(
	         "====================================================== General GS splitting");
	      //System.out.println("Machine = " + Tools.getHostName());

	      GraphReliability graph = TestParams.getGraph(GraphName);

	      double unrel = q;
	      //double rateN = q;
	      //double rateL = q;
	      graph.setReliability(1 - unrel);
	      double[] unrelTab = { 0 };
	      boolean allequal = true; // all q equal
	      // allequal = false; // all q not equal
	      if (!allequal) {
	         TestParams.setRelRand(graph);
	         unrelTab = graph.getUnreliability();
	      }


	      unrel = -Math.expm1(-q);
	           
	      
	      RandomStream stream = new LFSR113();
	      SamplerType sam = SamplerType.UNIFORM;
	      sam = SamplerType.EXPONENTIAL;
	      RandomStream streamperm = new MRG31k3p(); // for permutations of links

	      GraphWithForest forest;
	      MarkovChainNetworkReliability chain;
	      forest = new GraphWithForest(graph);
	      chain = new MarkovChainNetworkReliability(forest, streamperm, sam);
	      System.out.println("Constructive schema");
	      


	      int numChainAdam = 1000 * 10;
	      Chrono timer = new Chrono();

	      SplittingGSAdam adam = monpilote(chain, numChainAdam, split, stream);
	      double[] gamma = adam.getGamma();

	     
	      stream = new LFSR113();
	     // new LFSR113();
	      System.out.println("rng     = " + stream.getClass().getSimpleName());
	      System.out.println("sampler = " + sam.toString());

	      int nrun = 1000 * 1000 * 1;
	      int numchain = 1;
	      double cro = timer.getSeconds();

			
			  System.out.println("split factor = " + split);
			  System.out.println("pilot numChainAdam = " + numChainAdam);
			  System.out.printf("pilot CPU time:   %.2f  sec%n%n", cro);
			  System.out.println("number of levels = " + (gamma.length - 1));
			  System.out.println(Tools.toString("gamma", gamma));
			  
			  System.out.println("Generalized splitting using connectivity for " +
			  graph.getFileName() + " graph"); System.out.println("number of nodes = " +
			  graph.getNumNodes()); System.out.println("number of edges = " +
			  graph.getNumLinks()); System.out.println("nodes in " + Tools.toString("V0",
			  graph.getV0())); System.out.println("unreliability   = " + unrel);
			  
			  
			  
			  System.out.println("number of runs  = " + nrun + "\nnumber of chains = " +
			  numchain + "\n");
			  
			  boolean histoFlag = false; boolean storeFlag = false;
			  SplittingGS gsplit = new SplittingGS();
			  gsplit.initHistogram(histoFlag, -25, -10, 30); gsplit.initStore(storeFlag,
			  nrun); 
			  //gsplit.initEdgeTallies(false, graph); 
			  //gsplit.initTurn(false,graph, forest);
			  
			  // // gsplit.run0(chain, numrun, numchain, gamma, s, stream);
			  gsplit.run(chain, nrun, gamma, split, stream); if (histoFlag) {
			  TallyHistogram hist = gsplit.getHistogram(); System.out.println("Histogram" +
			  hist.toString()); } if (storeFlag) { TallyStore store = gsplit.getStore();
			  System.out.println("TallyStore\n" + store.toString()); }
			 


	   }

	   private static SplittingGSAdam monpilote(MarkovChainWithImportance chain, int numChainAdam,
	         int split, RandomStream stream) {
	      double firstGamma = 0; // chain.getGamma();
	      double lastGamma = 1.;
	      SplittingGSAdam adam = new SplittingGSAdam();
	      adam.run(chain, numChainAdam, split, stream, firstGamma, lastGamma);
	      return adam;
	   }


	   private static void temps(SplittingGS gsplit,
	         MarkovChainNetworkReliability chain, int numrepeat, int numrun, double[] gamma,
	         int s) {
	      Tally times = new Tally(); // unreliability estimates

	      for (int i = 0; i < numrepeat; i++) {
	         double x = gsplit.run(chain, numrun, gamma, s, new LFSR113());
	         times.add(x);
	      }
	      System.out.println("mean time  = " + times.average());
	      double var = times.variance();
	      double sig = Math.sqrt(var);
	      System.out.println("time sigma = " + sig);
	      System.out.println(times.formatCINormal(0.95, 4));
	   }
	   
	   
	   
	   
	   
	   

	private static void printTab(double[] t) {
		int m = t.length;
		for (int i =0;i<m;i++) {
			System.out.print(" " +t[i] +", ");
		}
	}
	private static void printTab(int[] t) {
		int m = t.length;
		for (int i =0;i<m;i++) {
			System.out.print(" " +t[i] +", ");
		}
	}
	
	
	   private static double getNextGamma (int q, double[] tab, double lastGammaLevel)
	   {
	      Arrays.sort (tab);
	      double x = 0.5*(tab[q-1] + tab[q]);
	      if (x >= lastGammaLevel)
	      	return lastGammaLevel;
	      return x;
	   }
	
}
