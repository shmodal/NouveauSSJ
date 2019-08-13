package umontreal.ssj.networks.flow;
import java.util.*;

import umontreal.ssj.networks.LinkWithCapacity;
//import umontreal.ssj.util.Tools;
//import umontreal.ssj.networks.flow.GraphWithCapacity;
import umontreal.ssj.rng.*;


public class MaxFlowEdmondsKarp {
	
	protected GraphFlow network;
	/*network's residual graph*/
	protected GraphFlow residual;
	protected int source = -1;
	protected int sink = -1;
	protected int maxFlowValue = -1;
	
	
    public MaxFlowEdmondsKarp(GraphFlow network){
    	this.network=network;
    	this.residual=network.residual();
    	this.source=network.getSource();
    	this.sink=network.getTarget();
    	this.maxFlowValue=0;
        int numberOfLinks = network.getNumLinks();

    }

    //
    
	/**
	 * Increase the capacity for one link in particular by the increaseCap
	 * 
	 * WARNING: if graph non oriented considered (which has been transformed in an oriented graph) you
	 *  have to call it on both links (the symmetric ones)
	 * 
	 *     @param link
	 *     	
	 *     @param increaseCap
	 *     
	 *     @return whether or not it may have increased the maximum flow		
	 */
    public boolean IncreaseLinkCapacity(int link, int increaseCap) {
    	boolean reloadFlow=false;
    	if(residual.getLink(link).getCapacity()==0) {
    		reloadFlow=true;
    	}
    	this.residual.getLink(link).setCapacity(this.residual.getLink(link).getCapacity()+increaseCap);

    	return reloadFlow;
    }
    
    

	/**
	 * Decrease the capacity for one link in particular by the decreaseCap. Contrary to increaseCap
	 * calling this method will change the maximum flow and the residual capacities.
	 * 
	 *     @param link
	 *     	
	 *     @param increaseCap
	 *     		
	 */
    public void DecreaseLinkCapacity(int link, int decreaseCap) {
    	int delta= decreaseCap-residual.getLink(link).getCapacity();
    	if(delta>0) {
	    	
    		int tmpSource=this.residual.getLink(link).getSource();
	    	int tmpTarget=this.residual.getLink(link).getTarget();
	    	int oppositeIndice = residual.getLinkWithSourceAndSinkNodes(tmpTarget,tmpSource);
			LinkWithCapacity oppositeLink = residual.getLink(oppositeIndice);
			oppositeLink.setCapacity(oppositeLink.getCapacity()-delta);
	    	this.residual.getLink(link).setCapacity(0);
	    	
	    	//redistribute the excess flow received in v
	    	if(tmpTarget!=this.sink) {
	    		int vToSink=DecreaseCapFlow( this.sink,tmpTarget, delta);
	    		
	    	}
	    	//reroute the excess flow from u to target
	    	int uvMaxFlow=DecreaseCapFlow(tmpSource, this.sink, delta);
	    	
	    	
	    	if(uvMaxFlow<delta) {
	    		this.residual=this.network.residual();
	    		this.EdmondsKarp();
	    	}
	    	/*
	    	if(uvMaxFlow<delta) {
	    		if(tmpSource!=this.source) {
		    		int uToSource=DecreaseCapFlow(tmpSource, this.source, delta-uvMaxFlow);
		    		
		    	}
	    		this.EdmondsKarp();
	    		
	    	}
	    	
	    	*********************
	    	
	    	
	    	//call max flow for source u et target v
	    	int uvMaxFlow=DecreaseCapFlow(tmpSource, tmpTarget, delta);
	    	this.maxFlowValue+=uvMaxFlow-delta;
	    	
	    	//reequilibre flow matrix
	    	if(tmpSource!=this.source) {
	    		int uToSource=DecreaseCapFlow(tmpSource, this.source, delta-uvMaxFlow);
	    		
	    	}
	    	if(tmpTarget!=this.sink) {
	    		int vToSink=DecreaseCapFlow( this.sink,tmpTarget, delta-uvMaxFlow);
	    		
	    	}
	    	//try to see if we can redirect part of the lost flow
	    	this.EdmondsKarp();
	    	*/
	    	

    	}else {
    		this.residual.getLink(link).setCapacity(-delta);
	    }
    }
    
    
	/**
	 * Use to compute augmenting path between source and sink nodes for maximum flow problem
	 * 
	 *     
	 *     @return current maximum flow		
	 */
    public int EdmondsKarp() {
        int flow;
        while((flow = flowBFS(source , sink)) != 0) {
        	this.maxFlowValue += flow;
        }
        return this.maxFlowValue;
    }

	/**
	 * Use to compute maximum flow (with upper bound delta), between two nodes in current residual graph.
	 * 
	 * 
	 *     @param tmpSource
	 *     	
	 *     @param tmpTarget
	 *     
	 *     @param delta
	 *     
	 *     @return maximum flow between two points		
	 */
    public int DecreaseCapFlow(int tmpSource,int tmpTarget,int delta) {
        int flow=flowBFS(tmpSource , tmpTarget);
        int uvMaxFlow=0;
        while(flow != 0 && uvMaxFlow<delta) {
        	uvMaxFlow += flow;
        	flow=flowBFS(tmpSource , tmpTarget);
        }
        
        return uvMaxFlow;
    }


	/**
	 * Use to find augmenting path between two nodes in current residual graph.
	 * 
	 * 
	 *     @param s
	 *     	
	 *     @param target
	 *     
	 *     @return augmenting flow		
	 */
    public int flowBFS(int s,int target) {
    	// find a s-t path in g of positive capacity
    	// initialize path capacities
    	// pathcap[u] = capacity of the path from s to u
    	int[] pathcap = new int[this.residual.getNumNodes()];
    	pathcap[s] = Integer.MAX_VALUE;
    	// initialize parents to build the paths
    	LinkFlow[] parent = new LinkFlow[this.residual.getNumNodes()];
    	LinkedList<Integer> Queue = new LinkedList<Integer>();
    	Queue.add(s);
    	while (!Queue.isEmpty() && parent[target] == null) {
    		
    		int v = Queue.pop();
    		LinkFlow link;
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
		     LinkFlow oppositeLink = residual.getLink(oppositeIndice);
			 oppositeLink.setCapacity(oppositeLink.getCapacity()+flow);
		     cur = parent[cur].getSource();
		 }
		 return flow;
      
    }

}
