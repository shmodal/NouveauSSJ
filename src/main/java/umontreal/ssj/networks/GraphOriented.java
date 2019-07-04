package umontreal.ssj.networks;

import umontreal.ssj.util.PrintfFormat;

public abstract class GraphOriented<N extends NodeBasic, L extends LinkBasic> extends
Graph<N,L> {

	
	   public void addLink(L l) {
		   this.links.add(l);
		   this.numLinks++;
		   int a = l.getSource();
	       this.nodes.get(a).incCounter();
	       this.nodes.get(a).addNodeLink(l.getIndice());
	       
	   }
	   
	   
	   /**
	    * Return the link which connects node source i and sink j. Similar to
	    * 
	    * @param i
	    * @param j
	    * @return To obtain the number of link with endpoints i and j
	    */

	   
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
	   
	   /**
	    * Returns the node connected to node i by Link link
	    * 
	    * @param link
	    * @param i
	    * @return the neighbor of i
	    */
	   @Override
	   public int getNeighborOfNode(L link, int i) {
	      if (link.getSource() == i)
	         return link.getTarget();
	      //if the link is not from i to another link, return error value
	      return -1;
	   }
	   
	   @Override
	   public String toString() {
	      StringBuffer sb = new StringBuffer(
	            "================================================= Graph"
	                  + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numLinks; i++) {
	         L link = links.get(i);
	         sb.append("link  " + link.getIndice() + "  connects nodes  "
	               + link.getSource() + ", " + link.getTarget()
	               + PrintfFormat.NEWLINE);
	      }
	      sb.append(PrintfFormat.NEWLINE + "----------------------------------"
	                + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numNodes; i++) {
	         N node = nodes.get(i);
	         int r = node.getNodeLinks().size();
	         sb.append("node  " + node.getNumber());
	         sb.append("  has " + r + " links: ");
	         if(r>0) {
		         for (int j = 0; j < r - 1; j++) {
		            sb.append(node.getNodeLink(j) + ", ");
		         }
		         sb.append(node.getNodeLink(r - 1));
	         }
	         sb.append(PrintfFormat.NEWLINE);
	      }
	      
	      return sb.toString();
	   }
	
}
