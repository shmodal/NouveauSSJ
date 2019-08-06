package umontreal.ssj.networks.flow;

import java.io.IOException;
import java.util.LinkedList;

import umontreal.ssj.networks.NodeBasic;
import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testPMCDodeca {

	public static void main(String[] args) {

		int demand = 5;
		int source = 0;
		int target = 19;
		int b = 4;
		double rho = 0.7;
		double[] epsilon = {1.0e-4, 1.0e-5, 1.0e-6, 1.0e-7, 1.0e-8, 1.0e-9, 1.0e-10, 1.0e-11,
				1.0e-12, 1.0e-13};
		
		proc(demand,source,target,b,rho,epsilon[2]);
		
	}

	private static void proc(int demand,int source, int target,int b, double rho, double epsilon) {
		boolean pmc = false; //true for launching PMC
		pmc = true;
		boolean nofilter = false; //true for plain PMC(without filter)
		nofilter = true;
		boolean filter = false; // true for filtering simple
		filter= true;
		boolean fOutside = false; // true for Filter Outside
		fOutside = true;
		
		int nrun = 1000000 ;
		
		GraphFlow g = null;
		String nameG = null;
		
		try {	
			//GraphFlow g=new GraphFlow(name);
			String file= ExamplesGraphs.getGraphfile("dodecaFlow");
			g = new GraphFlow(file);
			g.resetCapacities();
			g= ExamplesGraphs.Undirect(g);
			//System.out.println(g.toString());
			nameG = "Dodecahedron";

		int numNodes = g.getNumNodes();
		assert(0 <= source && source < numNodes );
		assert(0 <= target && target < numNodes );
		
		g.setSource(source);
		g.setTarget(target);
		RandomStream stream = new LFSR113();
		
		System.out.println("Graph : " +nameG);
		
		System.out.println("Reliability of transporting flow " + demand + " from " + g.getSource()+" to " + g.getTarget());
		System.out.println();
		System.out.println("rng = " + stream.getClass().getSimpleName() + "\n");
		
		
		stream.resetStartSubstream();
		PMCFlowNonOriented p = null;
		if(pmc) { 
			
			int m0 = g.getNumLinks();
			int[] tab = new int[m0];
			for (int i = 0; i<m0;i++) {
				tab[i] = b;
			}
			System.out.println("============================================ Permutation Monte Carlo");
			if (nofilter) { 
				System.out.println("============================================ No Filter");
			//PMCNonOriented p = new PMCNonOriented(g);
			p = new PMCFlowNonOriented(g);
			p.filter=false; p.filterOutside=false;
			stream.resetStartSubstream();
			p.initCapaProbaB(tab, rho, epsilon);
			p.trimCapacities(demand);
			p.run(nrun,stream,demand);
			}
			
			if (filter) {
				System.out.println("============================================ Filter single");
				p = new PMCFlowNonOriented(g); 
				//p = new PMCFilterOutsideNew(g);
				p.filter=true; p.filterOutside=false;
				stream.resetStartSubstream();
				p.initCapaProbaB(tab, rho, epsilon);
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
				
			}
			if (fOutside) {
				System.out.println("============================================ Filter Outside");
				//PMCFilterOutsideNew p = new PMCFilterOutsideNew(g);
				p = new PMCFlowNonOriented(g);
				p.filterOutside=true; p.filter=false;
				p.frequency=5;p.seuil=0.8;
				stream.resetStartSubstream();
				p.initCapaProbaB(tab, rho, epsilon);
				p.trimCapacities(demand);
				p.run(nrun,stream,demand);
			}
		}
	
		
		}
		catch (IOException ioe) {
	        System.out.println("Trouble reading from the file: " + ioe.getMessage());
	    } 
		
	}
	
	
}
