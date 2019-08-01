package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.staticreliability.GraphWithForest;
import umontreal.ssj.networks.staticreliability.PMC;
import umontreal.ssj.networks.staticreliability.TestParams;
import umontreal.ssj.networks.staticreliability.turniptest;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.stat.Tally;

/**===============================   TEST    ============================
 *  Test pour vérifier les résultats de PMC Flot. On prend le même graphe dodecahedron.
 *  On compare le résultat obtenu de PMC de static reliability, avec celui du PMC Flot (pour chaque 
 *  arête, capacités binaires, 0 ou 1) 
 *  On observe les intervalles de confiance n fois, et on vérifie si ils se recoupent bien à chaque 
 *  reprise.
 *  
 */


public class compareConfInterval {

	public static void main(String[] args) throws IOException {
	
		//cree PMC Flot
	GraphFlow g = ExamplesGraphs.buildDodecaNoOr();
	 int demande =1;
	 g.setSource(0);
	 g.setTarget(19);
	 double q = 0.01;
	 int b = 1;
	 int[] tab = new int[b+1];
	 tab[0] =0; tab[1] =1;
	 double[] prob = new double[b+1];
	 prob[0] =q; prob[1]=1-q;
	 for (int i =0;i<g.getNumLinks();i++) {
		 g.setB(i, b);
		 g.setCapacityValues(i, tab);
		 g.setProbabilityValues(i, prob);
	 }
	 PMCFlowNonOriented p = new PMCFlowNonOriented(g);
	 
	 // cree PMC richard
	 String filename = TestParams.getGraphfile("dodecahedron");
	 GraphReliability graph = new GraphReliability(filename);
	 double unreliability = q;
	 unreliability = -Math.expm1(-q);
     graph.setReliability(1 - unreliability);
     double[] unrel = { 0 };
     boolean allequal = true; // if all r are equal
     if (!allequal) {
         TestParams.setRel(graph);
         unrel = graph.getUnreliability();
      }
     GraphWithForest forest;
     forest = new GraphWithForest(graph);
     PMC turn = null;
     turn = new PMC(graph, forest);
     turn.setHypoExpKind(1);
     
	 
	 
	 RandomStream stream;
	 int compteur = 0;
	 int n = 3;
	 int nrun = 5000000;
	 double[] t = new double[2];
	 double[] tf = new double[2]; //tableau pour PMC flot
	 double[] tr = new double[2];  //tableau pour PMC richard
	 
	 
	 stream = new LFSR113();
	 
	 Tally values1 = new Tally();
	 Tally values2 = new Tally();
	 for (int i=0;i<n;i++) {
	 for (int j = 0; j < nrun; j++) {
		 stream = new LFSR113();
		 //stream = new LFSR113();
		 stream.resetNextSubstream();
		 double x = p.doOneRun(stream,demande);
		 values1.add(x);
		 //stream = new LFSR113();
		 //stream = new LFSR113();
		 //stream = new LFSR113();
		 stream = new LFSR113();
		 stream.resetNextSubstream();
		 stream.resetNextSubstream();
		 double y = turn.doOneRun(stream);
		 values2.add(y);
	 }
	 System.out.println(values1.formatCINormal(0.95, 4));
	 System.out.println(values2.formatCINormal(0.95, 4));
	 System.out.println("Fin");
	 values1.confidenceIntervalNormal(0.95, tf);
	 values2.confidenceIntervalNormal(0.95, tr);
	 double af = tf[0]-tf[1];
	 double bf = tf[0]+tf[1];
	 double ar = tr[0]-tr[1];
	 double br = tr[0]+tr[1];
	 if (af<ar && bf>ar) {compteur++ ; 
	 }
	 if (ar<af && br >af) {compteur++ ;}
	 }
	 
	 //values1.confidenceIntervalNormal(0.95, t);
	 //System.out.println(t[0]);
	 //System.out.println(t[1]);
	 //System.out.println(values1.formatCINormal(0.95, 4));
	 //System.out.println(t[0]-t[1]);
	 //turniptest.proc(q,"dodecahedron");
	
	
	System.out.println(compteur + " intervalles qui se recoupent sur " + n);
	 
	
	}
}
