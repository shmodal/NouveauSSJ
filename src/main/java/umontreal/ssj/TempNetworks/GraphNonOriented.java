package umontreal.ssj.TempNetworks;

import java.util.ArrayList;

public class GraphNonOriented extends GraphBasic{
	
	   /**
	    * Add a node if users want to
	    * 
	    * @return void
	    */
	   public void addNode(NodeBasic n) {
		   this.nodes.add(n);
		   this.numNodes++;
		   
	   }
	   
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
	       this.nodes.get(b).incCounter();
	       this.nodes.get(a).addNodeLink(l.getIndice());
	       this.nodes.get(b).addNodeLink(l.getIndice());
	   }	

	   public int getLinkWithNodes(int i, int j) {
	      int k = 0;
	      while (k < numLinks) {
	         int a, b;
	         a = links.get(k).getSource();
	         b = links.get(k).getTarget();
	         if ((a == i && b == j) || (a == j && b == i)) {
	            break;
	         }
	         k++;
	      }
	      if (k == numLinks)
	         return -1;
	      return links.get(k).getIndice();
	   }
}
