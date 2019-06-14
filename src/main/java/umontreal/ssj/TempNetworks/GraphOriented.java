package umontreal.ssj.TempNetworks;

import java.util.ArrayList;

public class GraphOriented extends GraphBasic{
	  /**
	    * Number of nodes
	    */
	   protected int numNodes;

	   /**
	    * Number of links
	    */
	   protected int numLinks;
	   /**
	    * All the links of the graph, dynamic structure to add after building
	    */
	   protected ArrayList<LinkBasic> links;

	   /**
	    * All the nodes of the graph, dynamic structure to add after building
	    */
	   protected ArrayList<NodeBasic> nodes;
	
	public GraphOriented() {
		numNodes = 0;
		numLinks = 0;
		ArrayList<LinkBasic> links = new ArrayList<LinkBasic>();
		ArrayList<NodeBasic> nodes = new ArrayList<NodeBasic>();
	}
	
	   /**
	    * Add a node if users want to
	    * 
	    * @return void
	    */
	  // public void addNode(NodeBasic n) {
	//	   this.nodes.add(n);
		//   this.numNodes++;  
	   //}
	   
	   /**
	    * Add a link if users want to
	    * 
	    * @return void
	    */
	   public void addLink(LinkBasic l) {
		   this.links.add(l);
		   this.numLinks++;
		   int a = l.getSource();
	       int b = l.getTarget();
	       this.nodes.get(a).incCounter();
	       this.nodes.get(a).addNodeLink(l.getIndice());
	       
	   }
	   
	   public int getLinkWithSourceAndSinkNodes(int i, int j) {
	      int k = 0;
	      while (k < this.numLinks) {
	         int a, b;
	         a = this.links.get(k).getSource();
	         b = this.links.get(k).getTarget();
	         if (a == i && b == j) {
	            break;
	         }
	         k++;
	      }
	      if (k == this.numLinks)
	         return -1;
	      return this.links.get(k).getIndice();
	   }
}
