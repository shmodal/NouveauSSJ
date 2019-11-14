package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.MRG31k3p;
import umontreal.ssj.rng.RandomStream;

public class debug6 {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		   String file= ExamplesGraphs.getGraphfile("lattice2and2");
		   
		   int demand = 4;
		   
		   
			GraphFlow g = new GraphFlow(file);
			
			int b = 2; // taille b+1
			int[] cap = new int[3];cap[0]=0;cap[1]=1; cap[2]=4;
			//double[] prob = new double[2] ;prob[0]=0.1;prob[1]=0.9;
			double [] prob = new double[3] ;prob[0]=0.05;prob[1]=0.05;prob[2]=0.9;
			for (int i = 0;i<g.getNumLinks();i++) {
				g.setB(b);
				g.setCapacityValues(cap);
				g.setProbabilityValues(prob);
			}
			
			g.resetCapacities();
			for (int i = 0;i<g.getNumLinks();i++) {
				g.initLinkLambda(i);
			}
			
			RandomStream stream = new LFSR113();
			RandomStream streamP = new MRG31k3p();
			

			MChainTris mother = new MChainTris(g, streamP, demand);
			
			MChainTris chain0 = mother.clone();
			
			
			System.out.println(chain0.Ek.network.toString());
			System.out.println(mother.Ek.network.toString());
		

	}

}
