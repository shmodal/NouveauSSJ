package umontreal.ssj.networks;

import java.util.ArrayList;


public class GraphOrientedBasic extends GraphOriented<NodeBasic,LinkBasic> {
	
	
	public GraphOrientedBasic() {
		this.numLinks=0;
		this.numNodes=0;
		this.links=new ArrayList<LinkBasic>();
		this.nodes=new ArrayList<NodeBasic>();
	}
	
	public GraphOrientedBasic(ArrayList<NodeBasic> nodes, ArrayList<LinkBasic> links) {
		this.numLinks=0;
		this.numNodes=0;
		this.links=links;
		this.nodes=nodes;
	}
	
	   @Override
	   public GraphOrientedBasic clone() {
	      GraphOrientedBasic image = new GraphOrientedBasic();
	      //image = (GraphOrientedBasic) super.clone();

	      image.numLinks = numLinks;
	      image.numNodes = numNodes;


	      // Link
	      image.links = new ArrayList<LinkBasic>();
	      for (int i = 0; i < numLinks; i++) {
	         image.links.add(new LinkBasic(links.get(i).getIndice(), links.get(i).getSource(),
	        		 links.get(i).getTarget()));
	      }

	      // nodes
	      image.nodes = new ArrayList<NodeBasic>();
	      for (int i = 0; i < numNodes; i++) {
	         if (nodes.get(i).getNodeLinks() != null) {
	            //int clonemylink[] = new int[nodes.get(i).getNodeLinks().size()];
	            int n = nodes.get(i).getNodeLinks().size();
	        	ArrayList<Integer> clonemylink = new ArrayList<Integer>();
	            for (int j = 0; j < n; j++) {
	               clonemylink.add(nodes.get(i).getNodeLink(j));
	               //= nodes.get(i).getNodeLink(j);
	            }
	            image.nodes.add( new NodeBasic(0, nodes.get(i).getNumber(), clonemylink));
	         } else {
	            image.nodes.add(new NodeBasic());
	         }
	      }

	      return image;
	   }
	
	

}
