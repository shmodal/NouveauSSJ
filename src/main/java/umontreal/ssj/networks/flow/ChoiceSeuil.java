package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.util.Chrono;

/**===============================   TEST    ============================
 *  Test pour trouver le seuil optimal à partir duquel on effectue filter.
 *  Pour essayer de rendre Filter plus efficace, on a une option pour laquelle on n'exécute
 *  Filter que si le maxFlot actuel est tel que maxFlow > level*demand
 *  
 *  Empiriquement pour le dodecahedron avec n=50 000, level = 0.8 semble plutôt bon
 *  temps équivalent à no filter, et erreur relative légèrement meilleure. A confirmer.
 */

public class ChoiceSeuil {
	

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
		
		PMCNonOriented p = new PMCNonOriented(Do);

		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		p.initCapaProbaB(tab, rho, epsilon[2]);
		p.trimCapacities(demande);
		
		int nrun = 500000;
		Tally values = new Tally();
		
		p.filter = true;
		double[] alpha = {0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9};
		//double[] alpha = {0.8,0.81,0.82,0.83,0.84,0.85,0.86,0.87,0.88,0.89};
		for (int i =0;i<alpha.length;i++) {
			p.level = alpha[i];
			Chrono timer = new Chrono();
			timer.init();
			stream = new LFSR113();
			for (int j = 0; j < nrun; j++) {
				double x = p.doOneRun(stream,demande);
				values.add(x);
			}
			double m_ell = values.average();
			double m_variance = values.variance();
			double sig = Math.sqrt(m_variance);
			double relerr = sig / (m_ell * Math.sqrt(nrun)); // relative error
		    double cro = timer.getSeconds();
		    double tem = cro * m_variance / nrun;
		    double WNRV =  tem/ (m_ell * m_ell);
		    System.out.println("Alpha = " + alpha[i]);
		    System.out.println("Erreur relative de : " + relerr + " et WNRV : " + WNRV);
			System.out.println("Temps : " + cro);
			values.clearObservationListeners();
		}
		System.out.println("No filter");
		Chrono timer = new Chrono();
		timer.init();
		stream = new LFSR113();
		for (int j = 0; j < nrun; j++) {
			double x = p.doOneRun(stream,demande);
			values.add(x);
		}
		double m_ell = values.average();
		double m_variance = values.variance();
		double sig = Math.sqrt(m_variance);
		double relerr = sig / (m_ell * Math.sqrt(nrun)); // relative error
	    double cro = timer.getSeconds();
	    double tem = cro * m_variance / nrun;
	    double WNRV =  tem/ (m_ell * m_ell);
	    System.out.println("Erreur relative de : " + relerr + " et WNRV : " + WNRV);
		System.out.println("Temps : " + cro);
		

	}

}
