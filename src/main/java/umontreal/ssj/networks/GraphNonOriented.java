package umontreal.ssj.networks;

import umontreal.ssj.util.PrintfFormat;

public abstract class GraphNonOriented<N extends NodeBasic, L extends LinkBasic> extends
Graph<N,L> {
	
	   /**
	    * Add a link if users want to
	    * 
	    * @return void
	    */
	   public void addLink(L l) {
		   this.links.add(l);
		   this.numLinks++;
		   int a = l.getSource();
	       int b = l.getTarget();
	       this.nodes.get(a).incCounter();
	       this.nodes.get(b).incCounter();
	       this.nodes.get(a).addNodeLink(l.getIndice());
	       this.nodes.get(b).addNodeLink(l.getIndice());
	   }
	   
	   
	   /**
	    * Return the link which connects nodes i and j
	    * 
	    * @param i
	    * @param j
	    * @return To obtain the number of link with endpoints i and j
	    */
	   
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
	      return link.getSource();
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
