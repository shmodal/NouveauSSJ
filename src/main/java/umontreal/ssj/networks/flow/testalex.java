package umontreal.ssj.networks.flow;

import java.io.IOException;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;

public class testalex {
	
	
	public static void main(String[] args) throws IOException {
		String file= ExamplesGraphs.getGraphfile("alexo2");
		GraphFlow g = new GraphFlow(file);
		g = ExamplesGraphs.Undirect(g);
		g.resetCapacities();
		System.out.println(g.toString());
		
		System.out.println(g.source);
		System.out.println(g.target);
		RandomStream stream = new LFSR113();
		PMCFlowNonOriented p = null;
		p = new PMCFlowNonOriented(g);
		p.filter=false; p.filterOutside=false;
		p.run(10000,stream,60);
}

}
