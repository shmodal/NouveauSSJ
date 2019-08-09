package umontreal.ssj.networks.flow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import umontreal.ssj.networks.NodeBasic;

public class GraphLatticeFlow extends GraphFlow{
	   
	/**
	    * Creates a graph define by a rectangular lattice
	    * graph. The x-sides of the graph have n nodes, and the y-sides have m
	    * nodes. The graph has edges between each adjacent nodes of capacity capacite. The graph contains
	    * n*m nodes and n(m-1) + m(n-1) edges. Here is an example with n = 3 and m =
	    * 4:<br />
	    *
	    * <pre>
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    *    |   |   |
	    *    o---o---o
	    * </pre>
	    *
	    * @param filename
	    *           output file name
	    * @param n
	    *           number of nodes on x-sides
	    * @param m
	    *           number of nodes on y-sides
	    * @param capacite
	    * 			max capacity for every edge
	    */
	   GraphLatticeFlow(String filename, int n, int m, int capacite) throws IOException{
	    	  this.numNodes = 0;
		      this.numLinks = 0;
		      this.links=new ArrayList<LinkFlow>();
		      this.nodes=new ArrayList<NodeBasic>();
		      double unifProb= 1.0/(capacite+1);
		      
		      for (int i=0;i<n*m;i++) {
		    	  this.addNode(new NodeBasic(i));
		      }
	
		      
		      int[] tabCap;
		      double[] tabProb;
		      int cntLink=0;
		      for (int i = 0; i < m - 1; i++) {
		         for (int j = 0; j < n - 1; j++) {
		            int k = i * n + j;
		            int targetleft = k + 1;
		            int targetdown = k + n;
		            
		            tabCap=new int[capacite+1];
		            tabProb=new double[capacite+1];
		            for (int cap=0;cap<=capacite;cap++) {
		            	tabCap[cap]=cap;
		            	tabProb[cap]=unifProb;
		            }
		            this.addLink(new LinkFlow(cntLink, k, targetleft,0,tabCap,tabProb));
		            
		            cntLink++;
		            
		            tabCap=new int[capacite+1];
		            tabProb=new double[capacite+1];
		            for (int cap=0;cap<=capacite;cap++) {
		            	tabCap[cap]=cap;
		            	tabProb[cap]=unifProb;
		            }
		            this.addLink(new LinkFlow(cntLink, k, targetdown,0,tabCap,tabProb));
		        
		            cntLink++;
		         }
		      }
		      for (int i = 0; i < m - 1; i++) {
		         // node1 and node2 of edge
		         int s = (i + 1) * n - 1;
		         int t = s + n;
		         tabCap=new int[capacite+1];
		         tabProb=new double[capacite+1];
		         for (int cap=0;cap<=capacite;cap++) {
		        	 tabCap[cap]=cap;
		        	 tabProb[cap]=unifProb;
		         }
		         this.addLink(new LinkFlow(cntLink, s, t ,0,tabCap,tabProb));
		         
		         cntLink++;
		      }
	
		      for (int j = 0; j < n - 1; j++) {
		         int s = (m - 1) * n + j;
		         int t = s + 1;
		         tabCap=new int[capacite+1];
		         tabProb=new double[capacite+1];
		         for (int cap=0;cap<=capacite;cap++) {
		        	 tabCap[cap]=cap;
		        	 tabProb[cap]=unifProb;
		         }
		         this.addLink(new LinkFlow(cntLink, s, t ,0,tabCap,tabProb));
		         
		         cntLink++;
		      }
		      this.source=0;
		      this.target = this.numNodes - 1; // target of graph
	   }
	
	
}
