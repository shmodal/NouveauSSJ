package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testPMCLatt6Rohan {
	
	public static void main(String[] args) {
		
		GraphFlow Latt6 = ExamplesGraphs.buildLatt6NoOr(); //Attention, aucune capacité set
		Latt6.setSource(0);
		Latt6.setTarget(35);
		RandomStream stream = new LFSR113();
		int b = 8;
		int demande = 10;
		double rho = 0.6;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		PMCNonOriented p = new PMCNonOriented(Latt6);

		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		p.initCapaProbaB(tab, rho, epsilon[2]);
		p.trimCapacities(demande);
		
		//p.filter = true;
		
		p.run(50000, stream, demande);
		//ExamplesGraphs.toString(Latt6);
		
		//Comparaison avec Monte Carlo : ne pas faire plus que epsilon =  1e-4
		
		//MonteCarloFlowNonOriented mc = new MonteCarloFlowNonOriented(p.father);
		//stream.resetStartSubstream();
		//mc.run(50000, stream, demande);
		
	}

}
