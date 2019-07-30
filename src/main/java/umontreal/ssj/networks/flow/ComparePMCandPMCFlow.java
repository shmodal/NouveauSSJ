package umontreal.ssj.networks.flow;


import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.*;
import umontreal.ssj.networks.staticreliability.turniptest;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class ComparePMCandPMCFlow {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		
		 
		GraphFlow g = ExamplesGraphs.buildDodecaNoOr();
		//GraphFlow g = ExamplesGraphs.buildLatt4NoOr();
		//GraphFlow g = ExamplesGraphs.buildLatt6NoOr();
		 RandomStream stream = new LFSR113();
		 //stream = new MRG31k3p();
		 //stream.resetNextSubstream();
		 //stream.resetStartSubstream();
		 int demande =1;
		 // A modifier
		 g.setSource(0);
		 g.setTarget(19);
		 
		 double q = 0.01;
		 
		 
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
		 
		 stream = new LFSR113();
		 stream = new LFSR113();
		 stream = new LFSR113();
		 
		 PMCNonOriented p = new PMCNonOriented(g);
		 //p.filter = true;
		 p.run(1000000,stream,demande);
		 
		 
		 
		 GraphFlow gTest = rebuildAlexo3();
		 gTest.setSource(0);
		 gTest.setTarget(6);
		 PMCNonOriented pTest = new PMCNonOriented(gTest);
		 
		 //pTest.run(1000000, stream, demande);
		 
		 // for turnip, source and target are inside the text file
		 
		 //stream.resetStartSubstream();
		 //turniptest.proc(q,"dodecahedron");
		 stream = new LFSR113();
		 stream = new LFSR113();
		 stream = new LFSR113();
		 turniptest.proc(q,"dodecahedron");
		 //turniptest.proc(q,"Alexo3");
		 
		 
		 
		 

	}
	
	
	
	private static GraphFlow rebuildAlexo3() {
		GraphFlow g = ExamplesGraphs.buildAlexo3NoOr();
		 double q = 0.001;
		 
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
		return g;
	}

}
