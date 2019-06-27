package umontreal.ssj.splitting;

import java.io.IOException;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Tools;
import umontreal.ssj.networks.*;
import umontreal.ssj.splitting.*;
import umontreal.ssj.splitting.tests.TestParams;
import umontreal.ssj.networks.staticreliability.*;
import umontreal.ssj.stat.*;


public class GeneralGStest {

	/* SPLIT GENERALISE
	 * Fonction qui lance le test pour differentes valeurs de unreliability (contenues dans le tableau A)
	 * et differents parametres s de split
	 * Default, and "1-diamond"
	 * List of usual graphs is in TestParams.getGraphFile
	 */
	
   public static void main(String[] args) throws IOException {
      double[] A = { 0.5, 0.1, 0.01, 0.001, 1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7,
            1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11, 1.0e-12, 1.0e-13, 1.0e-14,
            1.0e-15, 1.0e-16, 1.0e-17, 1.0e-18, 1.0e-19, 1.0e-20 };
      int[] split = { 2, 5, 10, 20, 50, 100 };
    //  for (int j = 1; j < 2; j += 5) {
      for (int j = 1; j < 4; j += 2) {
     //   for (int j = 0; j < 2; j += 1) {
         for (int i = 0; i < 1; i++) {
            proc(split[i], A[j],"1-diamond");
         }
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
}