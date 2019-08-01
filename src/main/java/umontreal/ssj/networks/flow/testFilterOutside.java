package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testFilterOutside {
	
	public static void main(String[] args) {
		GraphFlow Do = ExamplesGraphs.buildDodecaNoOr(); //Attention, aucune capacité set
		
		Do.setSource(0);
		Do.setTarget(19);  //19 avant
		RandomStream stream = new LFSR113();
		int b = 4;
		int demande = 5;
		double rho = 0.7;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		//PMCNonOriented p = new PMCNonOriented(Do);
		PMCFilterOutsideNew p = new PMCFilterOutsideNew(Do);

		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		p.initCapaProbaB(tab, rho, epsilon[9]);
		p.trimCapacities(demande);
		
		p.filterOutside = true;
		p.filter=false;
		
		//p.filter = true;
		stream.resetStartSubstream();
		p.run(100000, stream, demande);
		//p.doOneRun(stream, demande);
		stream.resetStartSubstream();
		
		p.filterOutside = false;
		p.filter=true;
		
		//p.run(100000, stream, demande);
		
		p.filter=false;
		p.run(100000, stream, demande);
		
		//p.runOld(500000, stream, demande);
		//ExamplesGraphs.toString(Latt6);
		
		//Comparaison avec Monte Carlo : inefficace à epsilon = 1e-4
		
		//MonteCarloFlowNonOriented mc = new MonteCarloFlowNonOriented(p.father);
		//stream.resetStartSubstream();
		//mc.run(500000, stream, demande);
		
		
	}


}
