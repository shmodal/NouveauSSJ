package umontreal.ssj.networks.staticreliability;

import java.io.IOException;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.Tools;


/**
 * This class calls the Monte-Carlo test.
 * 
 * @author Richard Simard
 */
public class mctest {
	/*
	 * The main method launches the MC Test with several unreliability values q (from the A array) and a graph filename.
	 * Default, q = 0.01 and "1-diamond"
	 * List of usual graphs is in TestParams.getGraphFile
	 * 
	 */
	
   public static void main(String[] args) throws IOException {
      double[] A = { 0.5, 0.1, 0.01, 0.001, 1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7,
            1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11, 1.0e-12, 1.0e-13, 1.0e-14,
            1.0e-15 };
      for (int i = 2; i < 3; i += 1) {
         proc(A[i],"1-diamond");
    	 //proc(0.00001);
      }
   }

   // Default : use of shocks
   
   private static void proc(double q, String GraphName) throws IOException {
      boolean shockFlag = false; // with shocks if true
      shockFlag = true;   //doesnt work without shocks
      //shockFlag = false;

      String filename = TestParams.getGraphfile(GraphName); 
      GraphReliability graph = new GraphReliability(filename);

      
      double unreliability = q;
      double rateL = 0.01;  // rateLinks for shocks
      
    //  graph.setReliability(1 - unreliability);
      double[] unrel = { 0 };
      boolean allequal = true; // if all r are equal
      // allequal = false;
      if (!allequal) {
         TestParams.setRel(graph);
         unrel = graph.getUnreliability();
      }

      ShockList shocks = null;
      RandomStream stream = new LFSR113();
      GraphWithForest forest;
      if (shockFlag) {
         shocks = TestParams.getShocks(graph, q, rateL);
         forest = new GraphWithForestShocks(graph, shocks);
      } else {
         forest = new GraphWithForest(graph);
      }

      MonteCarlo mc = null;
      String mcname = "MonteCarlo";
         if (shockFlag) {
            mc = new MonteCarloShocks(graph, forest, shocks);
         } else {
            mc = new MonteCarlo(graph, forest);
         }

      int nrun = 1000 * 1000 * 1;
      System.out.println("============================================ "
            + mcname);
      //System.out.println("Machine = " + Tools.getHostName());  
      //    Ca bugge
      if (shockFlag) {
         System.out.println("Shocks schema");
       //  TestParams.printShocks(shocks);
      } else {
         // System.out.println("Constructive schema");
      }
      System.out.println("rng = " + stream.getClass().getSimpleName() + "\n");
      System.out.println(mcname + " for " + graph.getFileName() + " graph");
      System.out.println("number of nodes = " + graph.getNumNodes());
      System.out.println("number of edges = " + graph.getNumLinks());
      System.out.println("nodes in " + Tools.toString("V0", graph.getV0()));
      if (!shockFlag) {
         if (allequal)
            System.out.println("unreliability   = " + unreliability);
         else
            System.out.println(Tools.toStringSame("unreliabilities", unrel));
      } else {
         System.out.println("Shocks file:  " + shocks.getFileName() + "\n");
      }
      System.out.println("number of runs  = " + nrun + "\n");

      mc.run(nrun, stream);
   }

}