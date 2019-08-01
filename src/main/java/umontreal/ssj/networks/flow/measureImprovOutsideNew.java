package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.PMC;
import umontreal.ssj.networks.staticreliability.TestParams;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;
import umontreal.ssj.util.Chrono;

public class measureImprovOutsideNew {
	

	public static void main(String[] args) throws IOException {
		
		GraphFlow Do = ExamplesGraphs.buildDodecaNoOr(); //Attention, aucune capacité set
		
		//GraphFlow Do = ExamplesGraphs.buildLatt6NoOr();
		
		Do.setSource(0);
		Do.setTarget(19);  //19 avant
		//Do.setTarget(35);  //19 avant
		//RandomStream stream = new LFSR113();
		int b = 4;
		int demande = 10;
		double rho = 0.7;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		
		PMCFlowNonOriented p = new PMCFlowNonOriented(Do);
		p.filter = false;
		
		PMCFilterOutsideNew q = new PMCFilterOutsideNew(Do);
		q.filter = false; q.filterOutside = true;
		
		
		int m0 = p.father.getNumLinks();
		int[] tab = new int[m0];
		for (int i = 0; i<m0;i++) {
			tab[i] = b;
		}
		
		p.initCapaProbaB(tab, rho, epsilon[0]);
		p.trimCapacities(demande);
		q.initCapaProbaB(tab, rho, epsilon[0]);
		q.trimCapacities(demande);
		
		
		RandomStream stream;
		int n = 10;
		int nrun = 50000;
		stream = new LFSR113();
		Tally values1 = new Tally();
		Tally values2 = new Tally();
		 
		 for (int i=0;i<n;i++) {
				Chrono timer = new Chrono();
				timer.init();
			 for (int j = 0; j < nrun; j++) {

				 stream = new LFSR113();
				 //stream = new LFSR113();
				 //stream.resetNextSubstream();
				 double x = p.doOneRun(stream,demande);
				 values1.add(x);
				 //stream = new LFSR113();
				 //stream = new LFSR113();
				 //stream = new LFSR113();
				 stream = new LFSR113();
				 //stream.resetNextSubstream();
				 //stream.resetNextSubstream();
				 double y = q.doOneRun(stream,demande);
				 values2.add(y);
			 }
				double m_ell1 = values1.average();
				double m_variance1 = values1.variance();
				double sig1 = Math.sqrt(m_variance1);
				double relerr1 = sig1 / (m_ell1 * Math.sqrt(nrun)); // relative error
			    double cro1 = timer.getSeconds();
			    double tem1 = cro1 * m_variance1 / nrun;
			    double WNRV1 =  tem1/ (m_ell1 * m_ell1);
			    
				double m_ell2 = values2.average();
				double m_variance2 = values2.variance();
				double sig2 = Math.sqrt(m_variance2);
				double relerr2 = sig2 / (m_ell2 * Math.sqrt(nrun)); // relative error
			    //double cro2 = timer.getSeconds();
			    double tem2 = cro1 * m_variance2 / nrun;
			    double WNRV2 =  tem2/ (m_ell2 * m_ell2);
			    
			    System.out.println("Amélioration de " + 100*((WNRV1-WNRV2) /WNRV1 ));
			    
			    //System.out.println("Erreur relative de : " + relerr1 + " et WNRV : " + WNRV1);
			    //System.out.println("Erreur relative de : " + relerr2 + " et WNRV : " + WNRV2);
			    //System.out.println("Temps : " + cro);
				values1.clearObservationListeners();
				values2.clearObservationListeners();
			 }
		
		
		
		
		
		
		
		
     
	
	}


}
