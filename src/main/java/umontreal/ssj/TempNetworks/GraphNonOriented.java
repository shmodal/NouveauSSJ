package umontreal.ssj.TempNetworks;

import java.util.ArrayList;

import umontreal.ssj.util.PrintfFormat;

public class GraphNonOriented extends GraphBasic{
	
	public GraphNonOriented() {
		this.numLinks=0;
		this.numNodes=0;
		this.links=new ArrayList<LinkBasic>();
		this.nodes=new ArrayList<NodeBasic>();
	}
	
	   /**
	    * Add a node if users want to
	    * 
	    * @return void
	    */
	   public void addNode(NodeBasic n) {
		   this.numNodes++;
		   this.nodes.add(n);
		   
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
