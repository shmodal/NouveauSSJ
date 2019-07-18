package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.RandomStream;

public class MonteCarloFlowNonOriented extends MonteCarloFlow {

	public MonteCarloFlowNonOriented(GraphFlow graph) {
		super(graph);
	}

	   protected double doOneRun(RandomStream stream,int demande) {
		   int m = father.getNumLinks();
		   for (int i=0;i<(m/2);i++) {
			   father.setCapacity(i, drawCapacity(stream,i));
			   //System.out.println("nouvelle capa");
			   //System.out.println(father.getLink(i).getCapacity());
		   }
		   for(int i=(m/2);i<m;i++) {
			   father.setCapacity(i,father.getLink(i-(m/2)).getCapacity());
		   } 
		   MaxFlowEdmondsKarp Ek= new MaxFlowEdmondsKarp(father);
		   int maxFlow = Ek.EdmondsKarp();
		   //System.out.println(maxFlow);
		   if (maxFlow >= demande){
			   return 0.0;
			   }
		   else {return 1.0;}
		   
	   }
	
	
	
	
}
