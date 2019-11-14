package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Tools;
import umontreal.ssj.networks.*;
import umontreal.ssj.splitting.*;
import umontreal.ssj.splitting.tests.TestParams;
import umontreal.ssj.networks.staticreliability.*;
import umontreal.ssj.stat.*;

import java.util.Iterator;
import java.util.Map;

public class GeneralGSTestFonctio {


	
   public static void main(String[] args) throws IOException {
	   String file= ExamplesGraphs.getGraphfile("lattice2and2");
		GraphFlow g = new GraphFlow(file);
		//g = ExamplesGraphs.Undirect(g);
		int b = 1; // taille b+1
		int[] cap = new int[2];cap[0]=0;cap[1]=1;
		double[] prob = new double[2] ;prob[0]=0.1;prob[1]=0.9;
		for (int i = 0;i<g.getNumLinks();i++) {
			g.setB(b);
			g.setCapacityValues(cap);
			g.setProbabilityValues(prob);
		}
		//GraphFlow g = SimpleGraph();
		g.resetCapacities();
		RandomStream stream = new LFSR113();
		RandomStream streamP = new MRG31k3p();
		//System.out.println(g.toString());

		MChainNew mother = new MChainNew(g, streamP, 1);
//		mother.valuesY =  new double[2];
//		
//		mother.valuesY[0] = 2.524;
//		mother.valuesY[1] = 1.121;
//
//		
		mother.coordinates  = new HashMap <Double,int[]>();
		int[] t = new int[2];
		t[0]=3;t[1]=4;
		mother.coordinates.put(2.524, t);
//		
		t = new int[2];
		t[0]=6;t[1]=7;
//		
		mother.coordinates.put(1.121, t);
//		
//
//		int[] i0 = mother.coordinates.get(1.121);
//		System.out.println(i0[0]);
//		System.out.println(i0[1]);
//		
//		
//		
		MChainNew chain = mother.clone();
//		
//		int[] i1 = chain.coordinates.get(1.121);
//		System.out.println(i1.length);
//		System.out.println(i1[0]);
//		System.out.println(i1[1]);
		
		
		
		//mother.Yinf.remove(0);
		//System.out.println(mother.Yinf.get(0));
		//System.out.println(mother.Yinf.get(1));
		//System.out.println(chain.Yinf.get(0));

		
		
		MChainNew m = new MChainNew(g, streamP, 1);
		m.initialState(stream,0);
		m.updateChainGamma(0.1);
		
		
		MChainNew newchain = m.clone();
		newchain.nextStep(stream,0.1);
		// a verifier
		
		newchain.updateChainGamma(0.2);
		MChainNew test = newchain.clone();
		
		
		
		
		
		int[] indices = new int[2];
		indices[0]=100;
		indices[1]=101;
		//m.coordinates.remove(0.14132121112711807);
		//m.coordinates.put(0.12,indices);
		//System.out.println("on clone l'autre, n'a pas du etre modifee");
		//MChainNew k = n.clone();
		//MChainNew l = m.clone();
		
		
		stream.resetNextSubstream();
		System.out.println("debut verif adam");
		MChainNew a = new MChainNew(g, streamP, 1);
		a.initialState(stream,0);
		a.getImportance();
		
		
		
		
		
		
		
		

		
		

	    //SplittingGSAdam adam = monpilote(mother, 500, 2, stream);
		//SplittingGSAdam adam = monpilote(mother, 4, 2, stream);
	    //double[] gamma = adam.getGamma();
	      
	   // MChainNew chain = mother.clone();
	   // chain.initialState(stream,0);
	      

			
	      
//		
	int nrun = 10000;//*100*100;
	nrun =1;
		//SplittingGS gsplit = new SplittingGS();
		//gsplit.run(ch, nrun, gamma, 2, stream);
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
