package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.ArrayList;

import umontreal.ssj.rng.*;
import umontreal.ssj.util.Chrono;
import umontreal.ssj.util.Tools;
import umontreal.ssj.networks.*;
import umontreal.ssj.splitting.*;
import umontreal.ssj.splitting.tests.TestParams;
import umontreal.ssj.networks.staticreliability.*;
import umontreal.ssj.stat.*;


public class GeneralGStest {


	
   public static void main(String[] args) throws IOException {
      
	   String file= ExamplesGraphs.getGraphfile("lattice4and4");
		GraphFlow g = new GraphFlow(file);
		//g = ExamplesGraphs.Undirect(g);
		
		int b = 1; // taille b+1
		int[] cap = new int[2];cap[0]=0;cap[1]=1;
		double[] prob = new double[2] ;prob[0]=0.01;prob[1]=0.9;
		for (int i = 0;i<g.getNumLinks();i++) {
			g.setB(b);
			g.setCapacityValues(cap);
			g.setProbabilityValues(prob);
		}

		
		
		
		
		//GraphFlow g = SimpleGraph();
		g.resetCapacities();
      
		RandomStream stream = new LFSR113();
		RandomStream streamP = new MRG31k3p();
		
		//MarkovChainRandomDiscreteCapacities chain = new MarkovChainRandomDiscreteCapacities(g, streamP, 10); 
      
		//double[] gamma = {0,1,2};
		MChainNew ch = new MChainNew(g, streamP, 1);
		/////////ch.initialState(stream,0);
		
		
		
		//System.out.println(ch.valuesY[0]);
		//System.out.println(ch.valuesY[1]);
		
		/////ch.updateChainGamma(1);
		/////////////ch.updateChainGamma(2);
		
		
		
		//MaxFlowEdmondsKarp p = new MaxFlowEdmondsKarp(g);
		//p.EdmondsKarp();
		
		
		
		//System.out.print(ch.Ek.residual.toString());
		
		
		//System.out.println(ch.Ek.maxFlowValue);
		//System.out.print(ch.Ek.network.toString());
		
		
		//ArrayList<Double> Yinf = new ArrayList<Double>();
		//System.out.print("taille"+ Yinf.size());
		
	      SplittingGSAdam adam = monpilote(ch, 100, 2, stream);
	      //double[] gamma = adam.getGamma();
		
		double[] gamma = {0.23437439600861393,
				0.35571224755605396,
				0.4297098204258004,
				0.4929386074307349,
				0.5497266401404897,
				0.5933519946211896,
				0.6468538751519813,
				0.7041322953683398,
				0.748078287804846,
				0.7690903938223312,
				0.7957967596706139,
				0.8480959973587716,
				0.8837616603728125,
				0.8936204688391574,
				0.9155918575010613,
				0.964939957421288,
				0.9899984938496039};
		
	int nrun = 10000;//*100*100;
	//double[] gamma = {0,0.02,0.04,0.06};
	//double[] gamma = { 0.00000,  0.434821,  0.573229 , 0.704168 , 0.834213 , 0.964473,  1.00000 };
	
	
		SplittingGS gsplit = new SplittingGS();
		gsplit.run(ch, nrun, gamma, 2, stream);
		
		
		
		PMCFlow p = new PMCFlow(g);
		
	    stream = new LFSR113();
	    p.trimCapacities(10);
	    p.run(100000,stream,1);
	    
	    
	    
	    
	    
	    int[] valuesY = {10,2,4,5};
		int numY = valuesY.length;
		int[] tab = new int[numY]; //tab contient des indices,
		//double [] newvaluesY = new double [numY]; // utile ?

		RandomStream streamPermut = new MRG31k3p();
		
		
		streamPermut.resetNextSubstream();
		RandomPermutation.init(tab, numY);
		RandomPermutation.shuffle(tab, streamPermut); // permute the links
		
		//for (int j=0;j<tab.length;j++) {
	//		System.out.println(tab[j]-1);
//		}
	    
	    
	    
      
      
   }

   
   private static GraphFlow SimpleGraph() {
	   GraphFlow g = new GraphFlow();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(2));
		g.addLink(new LinkFlow(0,0,1));
		g.addLink(new LinkFlow(1,1,2));
		//set les B
		int b = 1; // taille b+1
		for (int i = 0;i<2;i++) {
			g.setB(b);
		}
		int[] cap = new int[2];cap[0]=0;cap[1]=5;
		double[] prob = new double[2] ;prob[0]=0.2;prob[1]=0.8;
		g.setCapacityValues(0, cap);
		g.setProbabilityValues(0, prob);
		g.setCapacityValues(1, cap);
		g.setProbabilityValues(1, prob);
		System.out.println(g.toString());
		return g;
		
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