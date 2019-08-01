package umontreal.ssj.networks.flow;

import umontreal.ssj.rng.RandomStream;


/** Crude Monte Carlo Algorithm for flow reliability estimation. Graphs are supposed to be undirected.
 * 
 * 	Undirected graphs are defined as in ExamplesGraph. 
 * Let m be the number of links of an undirected graph.
 * Let i integer such as 0<= i < (m/2). then Link i and Link (i+ m/2) are symetric, respectively
 * from a to b and from b to a.
 * They must have the same capacity values and probability values.
 * 
 * DrawCapacity assumes that the capacity of a link follows a discrete distribution.
 * 
 * To estimate unreliability instead of reliability, just switch 1.0 and 0.0 in doOneRun.
 *  
 */

public class MonteCarloFlowNonOriented extends MonteCarloFlow {
	
	
	//Undirected graphs are defined as in ExamplesGraph. 
	// Let m be the number of links of an undirected graph.
	// Let i integer such as 0<= i < (m/2). then Link i and Link (i+ m/2) are symetric 
	// de a vers b et b vers a.
	// they have same capacity values and probability values
	//

	public MonteCarloFlowNonOriented(GraphFlow graph) {
		super(graph);
	}
	@Override
	public double doOneRun(RandomStream stream,int demande) {
		int m = father.getNumLinks();
		for (int i=0;i<(m/2);i++) {
			father.setCapacity(i, drawCapacity(stream,i));
			//System.out.println("nouvelle capa");
			//System.out.println(father.getLink(i).getCapacity());
		}
		for(int i=(m/2);i<m;i++) {
			father.setCapacity(i,father.getLink(i-(m/2)).getCapacity());
		} 
		MaxFlowEdmondsKarp Ek = new MaxFlowEdmondsKarp(father);
		int maxFlow = Ek.EdmondsKarp();
		//System.out.println("MaxFlow MC " + maxFlow);
		if (maxFlow >= demande){
			return 1.0;
		}
		else {return 0.0;}

	}
	
	
	
	
}
