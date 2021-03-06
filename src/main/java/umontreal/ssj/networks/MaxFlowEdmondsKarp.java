package umontreal.ssj.networks;
import java.util.*;


import java.util.LinkedList;
import java.util.List;

import umontreal.ssj.util.Tools;
import umontreal.ssj.networks.GraphWithCapacity;
import umontreal.ssj.networks.flow.GraphFlow;
import umontreal.ssj.networks.flow.LinkFlow;
import umontreal.ssj.rng.*;


public class MaxFlowEdmondsKarp {
	
	protected GraphWithCapacity network;
	/*network's residual graph*/
	protected GraphWithCapacity residual;
	/* Source used during the last invocation of this algorithm */
	protected int source = -1;
	/* Sink used during the last invocation of this algorithm */
	protected int sink = -1;
	/* Max flow established after last invocation of the algorithm. */
	protected int maxFlowValue = -1;
	
	
	
	
    public MaxFlowEdmondsKarp(GraphWithCapacity network){
    	this.network=network;
    	this.residual=network.residual();
    	this.source=network.getSource();
    	this.sink=network.getTarget();
    	this.maxFlowValue=0;

    }
    
    //if graph non oriented considered (which has been transformed in an oriented graph) you
    // have to call it on both links (the symmetric ones)
    public boolean IncreaseLinkCapacity( int link, int increaseCap) {
    	boolean reloadFlow=false;
    	if(residual.getLink(link).getCapacity()==0) {
    		reloadFlow=true;
    	}
    	this.residual.getLink(link).setCapacity(this.residual.getLink(link).getCapacity()+increaseCap);
	    
    	return reloadFlow;
    }
    
    
    public boolean DecreaseLinkCapacity(int link, int decreaseCap) {
    	boolean reloadFlow=false;
    	if(residual.getLink(link).getCapacity()-decreaseCap<0) {
    		reloadFlow=true;
    	}
    	this.residual.getLink(link).setCapacity(this.residual.getLink(link).getCapacity()-decreaseCap);
	    int oppositeIndice = residual.getLinkWithSourceAndSinkNodes(this.residual.getLink(link).getTarget(),
				this.residual.getLink(link).getSource());
		LinkWithCapacity oppositeLink = residual.getLink(oppositeIndice);
		oppositeLink.setCapacity(oppositeLink.getCapacity()+decreaseCap);

    	return reloadFlow;
    }
    
    
    
    public int EdmondsKarp() {
        int flow;
        //System.out.println(residual.toString());
        while((flow = flowBFS(source , sink)) != 0) {
        	this.maxFlowValue += flow;
        }
        System.out.println(residual.toString());
        return this.maxFlowValue;
    }
    

    public int flowBFS(int s,int target) {
    	// find a s-t path in g of positive capacity
    	// initialize path capacities
    	// pathcap[u] = capacity of the path from s to u
    	int[] pathcap = new int[this.residual.getNumNodes()];
    	pathcap[s] = Integer.MAX_VALUE;
    	// initialize parents to build the paths
    	LinkWithCapacity[] parent = new LinkWithCapacity[this.residual.getNumNodes()];
    	LinkedList<Integer> Queue = new LinkedList<Integer>();
    	Queue.add(s);
    	while (!Queue.isEmpty() && parent[target] == null) {
    		int v = Queue.pop();
    		LinkWithCapacity link;
    		int j;
    		for (int i = 0; i < this.residual.getNumberOfNeighbors(v); i++) {
    			// get link i connected to node v
    			link = this.residual.getLinkFromNode(i, v);
    			j = link.getIndice();
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
		  
		 // check whether a path was found
		 if(parent[target] == null) return 0;
		 // we found a path, update the flow
		 int flow = pathcap[target];
		 // push the flow on the path
		 int cur = target;
		 
		 while(parent[cur] != null) {
			 parent[cur].setCapacity(parent[cur].getCapacity()-flow);
		     /*get the opposite link*/
		     int oppositeIndice = residual.getLinkWithSourceAndSinkNodes(parent[cur].getTarget(), parent[cur].getSource());
		     LinkWithCapacity oppositeLink = residual.getLink(oppositeIndice);
			 oppositeLink.setCapacity(oppositeLink.getCapacity()+flow);
		     cur = parent[cur].getSource();
		 }
		 return flow;
      
    }

}
