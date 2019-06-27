/**
 * 
 */
package umontreal.ssj.networks.staticreliability;

import java.io.IOException;
import java.util.ArrayList;

import umontreal.ssj.rng.*;
import umontreal.ssj.randvar.*;
import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.LinkReliability;
import umontreal.ssj.networks.createGraph;
import umontreal.ssj.networks.staticreliability.ShockList;

/**
 * @author Richard Simard
 */
public class TestParams {

   static void setRel(GraphReliability graph) {
      int m = graph.getNumLinks();
      double r = 0.9;
      for (int i = 0; i < m; i++) {
         graph.setReliability(i, r);
         r = r - 0.1;
      }
   }

   static void setRelRand(GraphReliability graph) {
      int m = graph.getNumLinks();
      RandomStream stream = new MRG32k3a();
      RandomVariateGen gen = new UniformGen(stream, 1, 4);
      double r;
      for (int i = 0; i < m; i++) {
         double x = gen.nextDouble();
         r = 1.0 - Math.pow(10, -x);
         graph.setReliability(i, r);
      }
   }

   static void setRelcomp(GraphReliability graph) {
      int m = graph.getNumLinks();
      int n = graph.getNumNodes();
      //LinkReliability[] ar = graph.getLinks();
      ArrayList<LinkReliability> ar = graph.getLinks();
      double r = .999998;
      for (int i = 0; i < m; i++) {
         LinkReliability lin = ar.get(i);
         if (((n - 1) == lin.getSource()) || ((n - 1) == lin.getTarget()))
            graph.setReliability(i, r);
         if ((0 == lin.getSource()) || (0 == lin.getTarget()))
            graph.setReliability(i, r);
      }
      // graph.setReliability (98, r);
   }

   static String getGraphfile(String GraphName) {
      String legraphe;
      // choose a network
      // legraphe = "dodac3paragain2";
   //  legraphe = "dodac3serie";
   //   legraphe = "dodecahedron";
   //  legraphe = "10-Complete";
    // legraphe = "40-Square";
    //   legraphe = "2-100-Parallel";
     // legraphe = "7-2-Parallel";
      // legraphe = "fuller";
      // legraphe = "100-2-Lattice";
      // legraphe = "100-2-Stripe";
      // legraphe = "graph-1";
      //legraphe = "1-diamond";
      legraphe = GraphName ;
      
     //  legraphe = "BRE";
      String file = createGraph.RAREHOME + "/data/" + legraphe + ".txt";
      return file;
   }

   static ShockList getShocks(GraphReliability graph, double rateN, double rateL)
         throws IOException {
      boolean flagFile = false;
      // flagFile = true;

      ShockList shocks;
      if (flagFile) {
         String filename = "choc30";
         String file = createGraph.RAREHOME + "/data/chocs/" + filename
               + ".txt";
         shocks = new ShockList(graph, file);
      } else {
         if (rateL < 0)
            shocks = getShocksFromNodes(graph, rateN);
         else if (rateN < 0)
            shocks = getShocksFromLinks(graph, rateL);
         else
            shocks = getShocksFromNodesAndLinks(graph, rateN, rateL);
      }

      return shocks;
   }

   
   private static void removeSourceSinkShocks(ShockList shocks, int rateN) {
      if (rateN <= 0)
         return;
      // remove shocks on source and sink nodes
      shocks.remove(1);  // 1 is rarely sink node
      shocks.remove(0);     
   }
   
   
   private static ShockList getShocksFromNodes(GraphReliability graph, double rate) {
      ShockList shocks = ShockList.shocksFromNodesFailure(graph);
      // double rateN = -Math.log(rate);
      double x = rate;
      shocks.setRates(x);
      return shocks;
   }

   private static ShockList getShocksFromLinks(GraphReliability graph, double rate) {
      ShockList shocks = ShockList.shocksFromLinksFailure(graph);
      // 1 choc, 1 lien
      // double rateL = -Math.log1p(-rate); // rate = -ln(1 - unrel)
      double x = rate;
      shocks.setRates(x);
      return shocks;
   }

   private static ShockList getShocksFromNodesAndLinks(GraphReliability graph,
         double rateN, double rateL) {
      ShockList shocksN = ShockList.shocksFromNodesFailure(graph);
      // double rate = -Math.log(relia);
      double x = rateN;
      shocksN.setRates(x);
      ShockList shocksL = ShockList.shocksFromLinksFailure(graph);
      x = rateL;
      shocksL.setRates(x);
      shocksN.addAll(shocksL);
      return shocksN;
   }

   private static ShockList getShocksParallelFromNodes(GraphReliability graph, double rate) {
      // remove source and sink node as shocks
      ShockList shocks = ShockList.shocksFromNodesFailure(graph);
      shocks.remove(1);
      shocks.remove(0);
      double x = rate;
      shocks.setRates(x);
      return shocks;
   }

   static ShockList getShocksBRE(GraphReliability graph, double eps) {
      ShockList shocks = ShockList.shocksFromLinksFailure(graph);
      double[] y = new double[4];
      y[3] = eps;
      y[2] = eps;
      y[1] = eps*eps;
      y[0] = eps*eps;
      shocks.setRates(y);
      return shocks;
   }

   static void monV0(GraphReliability graph, int n) {
      // Graphes bandes ou carrés, les 4 coins doivent être connectés
      int[] S5 = { 0, n - 1, n * n - n, n * n - 1 };
      int[] S3 = { 0, 3 };
      /*
       * int numnodes = n; int[] S = new int[numnodes]; for (int i = 0; i <
       * numnodes; i++) S[i] = i;
       */
      graph.setV0(S3);
   }

   static void V0dode(GraphReliability graph) {
      int[] S2 = { 0, 3, 6, 9, 12, 15, 19 };
      int[] S = { 0, 1, 2, 3, 16, 17, 18, 19 };
      graph.setV0(S);
   }

   static GraphReliability getGraph(String GraphName) throws IOException {
      String file = getGraphfile(GraphName);
      GraphReliability graph = new GraphReliability(file);
      // monV0 (graph, 10);
      // V0dode (graph);
      return graph;
   }

   public static void printShocksDoc(ShockList shocks) {
      String fname = shocks.getFileName();
      if (fname.contains("nodes") || fname.contains("links")) {
         System.out.println(fname);
         double[] rate = shocks.getRates();
         int len = rate.length;
         if (rate[0] == rate[len - 1])
            System.out.printf("shocks rate   = %g%n", rate[0]);
         else {
            System.out.printf("shocks rate nodes = %g%n", rate[0]);
            System.out.printf("shocks rate links = %g%n%n", rate[len - 1]);
         }
      } else
         System.out.println("Shocks file:  " + fname);

      int ka = shocks.getShocks().size();
      System.out.println("number of shocks = " + ka);
   }

   static void printAllShocks(ShockList shocks) {
      if (null == shocks)
         return;
      System.out.println("--------------------------------------------");
      String chocs = shocks.toString();
      System.out.println(chocs);
      System.out.println("--------------------------------------------");
   }
}
