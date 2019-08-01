package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.LFSR113;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.networks.NodeBasic;


public class testBasique {

	public static void main(String[] args) {
		//GraphFlow g = buildAlexo1();
		//int demande =60;
	    //g.setSource(0);
	    //g.setTarget(9);
	    
	    //PMC p = new PMC(g);
	    //RandomStream stream = new LFSR113();
	    //p.trimCapacities(demande);
	    //p.run(100000,stream,demande, true);
		
		
		
		GraphFlow g = new GraphFlow();
		g.addNode(new NodeBasic(0));
		g.addNode(new NodeBasic(1));
		g.addNode(new NodeBasic(2));
		
		g.addLink(new LinkFlow(0,0,1));
		g.addLink(new LinkFlow(1,1,2));
		
		//set les B
		int b = 1; // taille b+1
		for (int i = 0;i<2;i++) {
			g.setB(b);
		}
		int[] cap = new int[2];cap[0]=0;cap[1]=5;
		
		
		double[] prob = new double[2] ;prob[0]=0.2;prob[1]=0.8;
		
		g.setCapacityValues(0, cap);
		g.setProbabilityValues(0, prob);
		g.setCapacityValues(1, cap);
		g.setProbabilityValues(1, prob);
		
		
		//g.addLink(new LinkFlow(2,1,0));
		//g.addLink(new LinkFlow(3,2,1));
		//g.setCapacityValues(2, cap);
		//g.setProbabilityValues(2, prob);
		//g.setCapacityValues(3, cap);
		//g.setProbabilityValues(3, prob);
		
		
		
		int demande =5;
	    g.setSource(0);
	    g.setTarget(2);
	    
	    PMCFlow p = new PMCFlow(g);
	    RandomStream stream = new LFSR113();
	    p.trimCapacities(demande);
	    double  pr = p.doOneRun(stream, demande);
	    //System.out.println(pr);
	    
	    //p.run(100000,stream,demande, true);
	    
	    
	    
	}

}
