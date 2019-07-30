package umontreal.ssj.networks.staticreliability;

import java.io.IOException;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.rng.*;
import umontreal.ssj.stat.*;
import umontreal.ssj.util.Tools;


/**
 * This class permits to call either the turnip test or the permutation
 * Monte-Carlo test depending on the boolean <i>pmcflag</i>.
 * 
 * @author Richard Simard
 */
public class turniptest {
	
	/*
	 * Fonction qui lance le test pour differentes valeurs de unreliability (contenues dans le tableau A)
	 */

   public static void main(String[] args) throws IOException {
      double[] A = { 0.5, 0.1, 0.01, 0.001, 1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7,
            1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11, 1.0e-12, 1.0e-13, 1.0e-14,
            1.0e-15, 1.0e-16, 1.0e-17, 1.0e-18, 1.0e-19, 1.0e-20 };
     // for (int i = 5; i <= 22; i += 5) {
    //   for (int i = 3; i < 8; i += 2) {
      for (int i = 1; i < 4; i += 2) {
         proc(A[i],"dodecahedron");
      }
   }

   public static void proc(double q, String GraphName) throws IOException {
      // = 0: no shock; = 1, shocks; = -1: anti-shocks
      int shockFlag = 0;
      boolean antiScanFlag = false; // true for reverse scan; false otherwise
    //  antiScanFlag = true;
      boolean pmcFlag = false; // false for Turnip
      pmcFlag = true; // true for PermutationMonteCarlo
      boolean flagpi = false; // version permut or not of Turnip*, pmc*
   //   flagpi = true;

      String filename = TestParams.getGraphfile(GraphName);
      GraphReliability graph = new GraphReliability(filename);

      // TestParams.monV0 (graph, 20);
      // TestParams.V0dode (graph);
      double unreliability = q;
      double rateN = q;
      double rateL = q;

      ShockList shocks = null;
      if (shockFlag != 0) {
         shocks = TestParams.getShocks(graph, rateN, rateL);
      } else {
         unreliability = -Math.expm1(-q);
      }      
      
      graph.setReliability(1 - unreliability);
      double[] unrel = { 0 };
      boolean allequal = true; // if all r are equal
      // allequal = false;
      if (!allequal) {
         TestParams.setRel(graph);
         unrel = graph.getUnreliability();
      }

      GraphWithForest forest;
      RandomStream stream = new LFSR113();
      // stream = new LFSR113();

      if (0 == shockFlag) {
         forest = new GraphWithForest(graph);
      } else if (-1 == shockFlag || antiScanFlag) {
         forest = new GraphWithForestAntiShocks(graph, shocks);
      } else {
         forest = new GraphWithForestShocks(graph, shocks);
      }

      PMC turn = null;
      if (pmcFlag) { // PermutationMonteCarlo
         if (-1 == shockFlag) {
            turn = new PMCAntiShocks(graph, forest, shocks);
         } else if (1 == shockFlag) {
            if (flagpi)
               turn = new PMCShocksPi(graph, forest, shocks);
            else
               turn = new PMCShocks(graph, forest, shocks);
         } else {
            turn = new PMC(graph, forest);
         }

      } else { // turnip
         if (0 == shockFlag) {
            if (flagpi)
               turn = new TurnipPi(graph, forest);
            else
               turn = new Turnip(graph, forest);
         } else if (-1 == shockFlag) {
            turn = new TurnipAntiShocks(graph, forest, shocks);
         } else if (1 == shockFlag) {
            if (antiScanFlag) {
               turn = new TurnipShocksAntiScan(graph, forest, shocks);
            } else if (flagpi)
               turn = new TurnipShocksPi(graph, forest, shocks);
            else
               turn = new TurnipShocks(graph, forest, shocks);
         } else {
         }
      }

      turn.setAntiScan(antiScanFlag);
      turn.setHypoExpKind(1);
      
      int nrun = 1000 * 1000 * 1;
      //nrun = nrun*10;

      String pmcname = turn.toString();
      printHead(pmcname, shockFlag, shocks, antiScanFlag, flagpi);
      System.out.println("rng = " + stream.getClass().getSimpleName() + "\n");
      System.out.println(pmcname + " for " + graph.getFileName() + " graph");
      System.out.println("number of nodes = " + graph.getNumNodes());
      System.out.println("number of edges = " + graph.getNumLinks());
      System.out.println("nodes in " + Tools.toString("V0", graph.getV0()));
      if (0 == shockFlag) {
         if (allequal)
            System.out.println("unreliability   = " + unreliability);
         else
            System.out.println(Tools.toStringSame("unreliabilities", unrel));
      } else {
         TestParams.printShocksDoc(shocks);
      }
      System.out.println("number of runs  = " + nrun + "\n");

      boolean storeFlag = false;
      boolean histoFlag = false;
      turn.initStore(storeFlag, nrun);
      turn.initHistogram(histoFlag, -25, -10, 15);
      
      turn.run(nrun, stream);
      
      if (storeFlag)
         System.out.println("Store = \n" + turn.getStore());
      if (histoFlag) {
         TallyHistogram hist = turn.getHistogram();
         System.out.println(hist.toString());
      }
   }

   private static void printHead(String pmcname, int shockFlag, 
         ShockList shocks, boolean antiScanFlag, boolean flag1) {
      System.out
            .println("====================================================== "
                  + pmcname);
      //System.out.println("Machine = " + Tools.getHostName());
      if (0 == shockFlag) {
         System.out.println("\n");
         return;
      }

      System.out.print("Shocks schema");

      if (shockFlag < 0) {
         if (flag1)
            System.out.print(": ANTI-shocks");
      } else if (antiScanFlag) {
         System.out.print(": REVERSE scan");
      }
      if (0 == pmcname.compareTo("PermutationMonteCarloShocks")) {

      }

      System.out.println("\n");
    //  TestParams.printAllShocks(shocks);
   }

}
