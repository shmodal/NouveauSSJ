package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testRelErr {

	public static void main(String[] args) throws IOException {
	
		String file= ExamplesGraphs.getGraphfile("lattice4and4");
		GraphFlow g = new GraphFlow(file);
		g = ExamplesGraphs.Undirect(g);
		g.resetCapacities();
		
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
		
		
		int nrun = 1000 * 1000;
		
		
		
		
		
	}
	
	
}
