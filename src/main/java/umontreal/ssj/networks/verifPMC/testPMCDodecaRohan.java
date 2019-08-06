package umontreal.ssj.networks.verifPMC;

import umontreal.ssj.networks.flow.ExamplesGraphs;
import umontreal.ssj.networks.flow.GraphFlow;
import umontreal.ssj.networks.flow.LinkFlow;
import umontreal.ssj.networks.flow.PMCFlowNonOriented;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testPMCDodecaRohan {
	
	public static void main(String[] args) {
		GraphFlow Do = ExamplesGraphs.buildDodecaNoOr(); //Attention, aucune capacité set
		
		Do.setSource(0);
		Do.setTarget(19);
		RandomStream stream = new LFSR113();
		int b = 4;
		int demande = 5;
		double rho = 0.7;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		PMCFlowNonOriented p = new PMCFlowNonOriented(Do);

		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		p.initCapaProbaB(tab, rho, epsilon[2]);
		p.trimCapacities(demande);
		
		p.filter = true;
		
		p.run(50000, stream, demande);
		stream.resetStartSubstream();
		
		//p.filter = false;
		
		//p.run(50000, stream, demande);
		
		//p.runOld(500000, stream, demande);
		//ExamplesGraphs.toString(Latt6);
		
		//Comparaison avec Monte Carlo : inefficace à epsilon = 1e-4
		
		//MonteCarloFlowNonOriented mc = new MonteCarloFlowNonOriented(p.father);
		//stream.resetStartSubstream();
		//mc.run(500000, stream, demande);
		
		
	}


	   public static void toS(GraphFlow g) {
		   int m = g.getNumLinks();
		   for (int i=0;i<m;i++) {
			   LinkFlow Edge = g.getLink(i);
			   int s = Edge.getSource();
			   int t = Edge.getTarget();
			   System.out.println("Lien " + (i+1) + ": " + (s+1) + " et " + (t+1));
			   System.out.println();
		   }

	   }

}
