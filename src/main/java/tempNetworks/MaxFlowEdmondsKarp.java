package tempNetworks;
import java.util.*;

import java.util.LinkedList;
import java.util.List;

import umontreal.ssj.util.Tools;
import umontreal.ssj.rng.*;


public class MaxFlowEdmondsKarp {
	
	protected GraphWithCapacity network;
	/*network's residual graph*/
	protected GraphWithCapacity residual;
	/*If link j is operational, then operational[j] = true; otherwise false.*/
	protected boolean[] operational;
	/* Source used during the last invocation of this algorithm */
	protected int source = -1;
	/* Sink used during the last invocation of this algorithm */
	protected int sink = -1;
	/* Max flow established after last invocation of the algorithm. */
	protected double maxFlowValue = -1;
	/* Mapping of the flow on each edge. */
	protected Map<Link, Double> maxFlow = null;
	
	
	
	
    public MaxFlowEdmondsKarp(GraphWithCapacity network){
    	this.network=network;
    	this.residual=network.residual();
    	this.source=network.getSource();
    	this.sink=network.getTarget();
    	this.maxFlowValue=0.0;
        int numberOfLinks = network.getNumLinks();
   
        operational = new boolean[numberOfLinks];
        /*for the moment nut will evolve if unreliability*/
        setOperational(true);

    }
    
    
    /**
     * 
     * @param i
     *           link
     * @return true if link i is operational
     */
    public boolean isOperational(int i) {
       return operational[i];
    }

    /**
     * if flag = true, link i becomes operational; otherwise not
     * 
     * @param i
     * @param flag
     */
    public void setOperational(int i, boolean flag) {
       operational[i] = flag;
    }

    /**
     * if flag = true, all links become operational; otherwise not.
     */
    public void setOperational(boolean flag) {
       for (int i = 0; i < operational.length; i++)
          operational[i] = flag;
    }
    
    
    public int EdmondsKarp() {
        int maxflow = 0;
        int flow;
        System.out.println(residual.toString());
        while((flow = flowBFS(source , sink)) != 0) {
            maxflow += flow;
        }
        System.out.println(residual.toString());
        return maxflow;
    }
	  

    public int flowBFS(int s,int target) {
    	// find a s-t path in g of positive capacity
    	// initialize path capacities
    	// pathcap[u] = capacity of the path from s to u
    	int[] pathcap = new int[this.residual.getNumNodes()];
    	pathcap[s] = Integer.MAX_VALUE;
    	// initialize parents to build the paths
    	LinkCapacity[] parent = new LinkCapacity[this.residual.getNumNodes()];
    	LinkedList<Integer> Queue = new LinkedList<Integer>();
    	Queue.add(s);
    	while (!Queue.isEmpty() && parent[target] == null) {
    		int v = Queue.pop();
    		LinkCapacity link;
    		int j;
    		for (int i = 0; i < this.residual.getNumberOfNeighbors(v); i++) {
    			// get link i connected to node v
    			link = this.residual.getLinkFromNode(i, v);
    			j = link.getIndice();
		        if (operational[j]) {
		        	// get node u such that i = (v,u)
		        	int u = this.residual.getNeighborOfNode(link, v);
		        	if(u != s && link.getCapacity()> 0 && parent[u] == null) {
		        		// we found an unvisited node u
		        		parent[u] = link;
		        		pathcap[u] = Math.min(pathcap[v], link.getCapacity());
		        		Queue.add(u);
		        	}
		           
		        }
		     }
		 }
		  
		 // check whether a path was found
		 if(parent[target] == null) return 0;
		 // we found a path, update the flow
		 int flow = pathcap[target];
		 // push the flow on the path
		 int cur = target;
		 for(int i=1;i<this.residual.getNumNodes();i++ ) {
			 System.out.println(parent[i].getIndice());
		 }
		 
		 while(parent[cur] != null) {
			 parent[cur].setCapacity(parent[cur].getCapacity()-flow);
		     /*get the opposite link*/
		     int oppositeIndice = residual.getLinkWithNodes(parent[cur].getTarget(), parent[cur].getSource());
		     LinkCapacity oppositeLink = residual.getLink(oppositeIndice);
			 oppositeLink.setCapacity(oppositeLink.getCapacity()+flow);
		     cur = parent[cur].getSource();
		 }
		 return flow;
      
    }

}
