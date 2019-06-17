package tempNetworks;

import java.util.ArrayList;

import umontreal.ssj.util.PrintfFormat;

public class GraphOriented extends GraphBasic{

	
	public GraphOriented() {
		numNodes = 0;
		numLinks = 0;
		this.links = new ArrayList<LinkBasic>();
		this.nodes = new ArrayList<NodeBasic>();
	}
	
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
	   
	   @Override
	   public String toString() {
	      StringBuffer sb = new StringBuffer(
	            "================================================= Graph"
	                  + PrintfFormat.NEWLINE);
	      for (int i = 0; i < numLinks; i++) {
	         LinkBasic link = links.get(i);
	         sb.append("link  " + link.getIndice() + "  connects nodes  "
	               + link.getSource() + ", " + link.getTarget()
	               + PrintfFormat.NEWLINE);
	      }
	      sb.append(PrintfFormat.NEWLINE + "----------------------------------"
	                + PrintfFormat.NEWLINE);
	      
	      for (int i = 0; i < numNodes; i++) {
	         NodeBasic node = nodes.get(i);
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
