package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testResultsLattice {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		int nrun = 1000*1000;
		int number;
		number = 4;
		String name = "lattice"+number+"and"+number;
		
		String file= ExamplesGraphs.getGraphfile(name);
		GraphFlow g = new GraphFlow(file);
		g = ExamplesGraphs.Undirect(g);
		g.resetCapacities();
		//System.out.println(g.toString());
		
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
		
		for (int i=0;i<epsilon.length;i++) {
			stream.resetStartSubstream();
			p.initCapaProbaB(tab, rho, epsilon[i]);
			//modifyParam(p.father,epsilon[i]);
			p.father.resetCapacities();
			//System.out.println(p.father.toString());
			System.out.println("epsilon = " + epsilon[i]);
			p.run(1000*1000,stream,demand);
		}
		
		
		
		
		//on cree les graphes
		
		System.out.println("Graphe : " + name);
		System.out.println("Nrun =" +nrun);
		
		System.out.println("            WN        RE        WNRV        T");
		
		
		
		
	}

}
