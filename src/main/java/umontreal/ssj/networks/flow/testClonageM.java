package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class testClonageM {

	
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
			MChainNew ch = new MChainNew(g, streamP, 1);
			MChainNew ch2 = ch.clone();
			
			
			
			ch.initialState (stream,0);
			System.out.println("Valeurs de Y");
			for (int i = 0;i<ch.valuesY.length;i++) {
				
				//System.out.println(ch.valuesY[i]);
			}
			
			//stream.resetNextSubstream();
			
			ch2.initialState(stream,0);
			System.out.println("Valeurs de Y après");
			for (int i = 0;i<ch2.valuesY.length;i++) {
				//System.out.println(ch2.valuesY[i]);
				System.out.println(ch2.father.getLink(i).getLambdaValue(0));
			}
			
			int[] t = ch.coordinates.get(0.03269682080824419);
			//System.out.println(t[0]);
			//System.out.println(t[1]);
			
			
			//System.out.println(ch.getImportance());
			//System.out.println(ch2.getImportance());
			
			//System.out.println(ch.Ek.network.toString());
			
			//System.out.println(ch2.getImportance());
			
			
			//ch.updateChainGamma(0.2);
			System.out.println(ch.getImportance());
			
			//System.out.println(ch.father.toString());
			
			System.out.println(ch2.getImportance());
			//System.out.println(ch2.Ek.network.toString());
			
			
			
			MaxFlowEdmondsKarp e = new MaxFlowEdmondsKarp(g);
			
			//g.setCapacity(0, 1);
			e.network.setCapacity(0, 1);
			//System.out.println(g.toString());
		//	System.out.println(e.network.toString());
			
			
			
			//int[] t2 = ch2.coordinates.get(0.03269682080824419);
			
			//boolean b0 =ch2.coordinates.containsKey(0.03269682080824419);
			//System.out.print(b);
			//int[] t2 = ch2.coordinates.get(0.03269682080824419);
			//System.out.println(t2[0]);
			//System.out.println(t2[1]);
			

			//ch.updateChainGamma(current_gamma); 
			
			
			
	}
}
