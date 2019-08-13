package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testBRE {

	public static void main(String[] args) throws IOException {
		String file= ExamplesGraphs.getGraphfile("lattice4and4");
		GraphFlow g = new GraphFlow(file);
		g = ExamplesGraphs.Undirect(g);
		g.resetCapacities();
		//System.out.println(g.toString());
		
		System.out.println(g.source);
		System.out.println(g.target);
		RandomStream stream = new LFSR113();
		PMCFlowNonOriented p = null;
		p = new PMCFlowNonOriented(g);
		p.filter=false; p.filterOutside=false;
		int b = 8;
		int m0 = g.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		double rho = 0.6;
		int demand = 10;
		
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		int repetition = 100; //on calcule 10 fois l'err relative pour un epsilon
		double [] epsi= {1.0e-28};
		for (int i=0;i<epsi.length;i++) {
			stream.resetStartSubstream();
			p.initCapaProbaB(tab, rho, epsi[i]);
			p.father.resetCapacities();
			//System.out.println(p.father.toString());
			//p.run(500000,stream,demand);
			double rep = 0.;
			for (int j=0;j<repetition;j++) {
				p.father.resetCapacities();
				rep += p.returnRelErr(500000, stream, demand);
			}
			rep = rep/repetition;
			System.out.println("erreur pour epsilon = " + epsi[i] + " : " + rep);
		}

	}

}
