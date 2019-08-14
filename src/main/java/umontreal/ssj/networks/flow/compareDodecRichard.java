package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.networks.staticreliability.*;
import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.PMC;
import umontreal.ssj.networks.staticreliability.TestParams;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;


public class compareDodecRichard {
	
	public static void main(String[] args) throws IOException {
		
		

     
     double[] tabq = {1e-1,1e-2,1e-3,1e-4,1e-5,1e-6};
     
     //double[] tabq = {1e-7,1e-8,1e-9,1e-10,1e-11,1e-12};
     for (int j = 0;j<tabq.length;j++) {
    	 System.out.println("=======================q = " + tabq[j]);
    	 proc(tabq[j]);
     }
	}
	
	
	public static void proc(double q) throws IOException {
		
		
		//cree PMC Flot
		//String file= ExamplesGraphs.getGraphfile("dodecaFlow");
		String file= ExamplesGraphs.getGraphfile("lattice4and4");
		//String file= ExamplesGraphs.getGraphfile("lattice10and10Basic");
		GraphFlow g = new GraphFlow(file);
		g.Undirect();
		g.resetCapacities();
	 int demande =1;
	 g.setSource(0);
	 
	 //g.setTarget(19);
	 g.setTarget(15);
	 int b = 1;
	 int[] tab = new int[b+1];
	 tab[0] =0; tab[1] =1;
	 double[] prob = new double[b+1];
	 prob[0] =q; prob[1]=1-q;
	 for (int i =0;i<g.getNumLinks();i++) {
		 g.setB(i, b);
		 g.setCapacityValues(i, tab);
		 g.setProbabilityValues(i, prob);
	 }
	 PMCFlowNonOriented p = new PMCFlowNonOriented(g);
	 
	 // cree PMC richard
	 //String filename = TestParams.getGraphfile("dodecahedron");
	 String filename = TestParams.getGraphfile("10-square");
	 GraphReliability graph = new GraphReliability(filename);
	 double unreliability = q;
	 unreliability = -Math.expm1(-q);
     graph.setReliability(1 - unreliability);
     double[] unrel = { 0 };
     boolean allequal = true; // if all r are equal
     if (!allequal) {
         TestParams.setRel(graph);
         unrel = graph.getUnreliability();
      }
     GraphWithForest forest;
     forest = new GraphWithForest(graph);
     PMC turn = null;
     turn = new PMC(graph, forest);
     turn.setHypoExpKind(1);
     
     //System.out.println("PMC Flow");
     RandomStream stream = new LFSR113();
       
     int nrun = 1000*1000;
     
     //p.run(nrun, stream, demande);  //PMC Flow
     System.out.print("PMC Flow : ");
     p.filter=true;
     p.returnRelErr(nrun, stream, demande);
     
     stream.resetStartStream();
     

     //turn.run(nrun, stream); //PMC
     System.out.print("PMC :");
     turn.returnRelErr(nrun, stream);
     
     turn = new Turnip(graph, forest);
     turn.setHypoExpKind(1);
     stream.resetStartStream();


     //turn.run(nrun, stream);  //TURNIP
     System.out.print("Turnip :");
     turn.returnRelErr(nrun, stream);
		
	}

}
