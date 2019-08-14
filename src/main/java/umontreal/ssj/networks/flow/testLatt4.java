package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testLatt4 {

	
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
		
		for (int i=0;i<epsilon.length;i++) {
			stream.resetStartSubstream();
			p.initCapaProbaB(tab, rho, epsilon[i]);
			//modifyParam(p.father,epsilon[i]);
			p.father.resetCapacities();
			p.filter=true;
			//System.out.println(p.father.toString());
			System.out.println("epsilon = " + epsilon[i]);
			p.run(1000*1000,stream,demand);
		}
}
	
	
	public static void modifyParam(GraphFlow g, double epsilon) {
		
		int m= g.getNumLinks();
		int s = g.getSource();
		int t = g.getTarget();
		for (int i =0;i<m;i++) {
			LinkFlow EdgeI = g.getLink(i);
			int s0=EdgeI.getSource(); int t0 = EdgeI.getTarget();
			if (s== s0 || s ==t0 || t==s0 ||t==t0 ) { //link source ou destination
				//System.out.println("bord");
				EdgeI.setB(8);
				int [] cap = new int[9];
				for (int j=0;j<9;j++) {cap[j] = j;}
				double [] prob = creeTab(epsilon,8);
				EdgeI.setCapacityValues(cap);
				EdgeI.setProbabilityValues(prob);
				
			} 
			else {
				EdgeI.setB(2); int[] cap = new int[3]; cap[0]=0; cap[1]=4; cap[2]=5;
				EdgeI.setCapacityValues(cap);
				double [] prob = new double[3]; prob[0] = epsilon/4; prob[1] = epsilon;
				prob[2] = 1-prob[0]-prob[1];
				EdgeI.setProbabilityValues(prob);
			}
		}
		
	}
	
	
	public static double[] creeTab(double epsilon,int b) {
		double [] prob = new double[b+1];
		double sum = 0.;
		prob[0] = epsilon*epsilon/b;
		sum += prob[0];
		for (int j=1;j<b;j++) {
			prob[j] = prob[j-1] * (b-j+1)/(b-j) ;
			sum += prob[j];
		}
		prob[b] = 1.-sum;
		
		return prob;
	}
	
}
