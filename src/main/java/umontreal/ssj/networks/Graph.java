package umontreal.ssj.networks;


import java.io.*;
import java.util.*;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.networks.staticreliability.SamplerType;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;



public abstract class Graph<N extends NodeBasic, L extends LinkBasic> implements Cloneable {
	
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
	   protected ArrayList<L> links;
	   /**
	    * All the nodes of the graph, dynamic structure to add after building
	    */
	   protected ArrayList<N> nodes;
	
	   
	   
		public Graph() {
			this.numLinks=0;
			this.numNodes=0;
			this.links=new ArrayList<L>();
			this.nodes=new ArrayList<N>();
		}
		
		public Graph(ArrayList<L> links, ArrayList<N> nodes) {
			this.numLinks=0;
			this.numNodes=0;
			this.links=links;
			this.nodes=nodes;
		}
	   
	   
	   
	
	   /**
	    * Add a node if users want
	    * 
	    * @return void
	    */
	   public void addNode(N n) {
		   this.nodes.add(n);
		   this.numNodes++;
	   }
	   
	   /**
	    * Add a link if users want
	    * 
	    * @return void
	    */
	   
	   public abstract void addLink(L l);
	     
	   /**
	    * Get the number of nodes
	    * 
	    * @return return the number of nodes
	    */
	   public int getNumNodes() {
	      return numNodes;
	   }

	   /**
	    * Get the i-th node
	    * 
	    * @param i
	    *           the number of wanted node
	    * @return return the i-th node
	    */
	   public N getNode(int i) {
	      return nodes.get(i);
	   }

	   /**
	    * Get nodes of the graph
	    * 
	    * @return return a tab of nodes contains nodes of the graph
	    */
	   public ArrayList<N> getNodes() {
	      return nodes;
	   }

	   /**
	    * Get the number of links
	    * 
	    * @return return the number of links
	    */
	   public int getNumLinks() {
	      return numLinks;
	   }

	   /**
	    * Get the i-th link
	    * 
	    * @param i
	    *           the number of wanted link
	    * @return return the i-th link
	    */
	   public L getLink(int i) {
	      return links.get(i);
	   }

	   /**
	    * Get links of the graph
	    * 
	    * @return return a tab of links contains links of the graph
	    */
	   public ArrayList<L> getLinks() {
	      return links;
	   }
	   
	   
	   // Verifier si c'est fonctionnel que ca soit orienté ou pas
	   /**
	    * Return the j-th Link connected to Node i
	    * 
	    * @param i
	    *           the number of the given node
	    * @param j
	    *           the number of the given link
	    * @return return link j connected to node i
	    */
	   public L getLinkFromNode(int j, int i) {
	      return links.get(nodes.get(i).getNodeLink(j));
	   }
	   
	   /**
	    * Return the number of neighbors of node i
	    * 
	    * @param i
	    *           node number
	    * @return return the number of neighbors of i
	    */
	   public int getNumberOfNeighbors(int i) {
	      return nodes.get(i).getNodeLinks().size();
	   }
	   
	   
	   // VERIFIER
	   //Probablemet a mettre pour graphe orienté
	   /**
	    * Returns the node connected to node i by Link link
	    * 
	    * @param link
	    * @param i
	    * @return the neighbor of i
	    */
	   public int getNeighborOfNode(L link, int i) {
	      if (link.getSource() == i)
	         return link.getTarget();
	      return link.getSource();
	   }
	   
	   
	   
	   
	   /**
	    * SHOULDNT BE USED
	    * 
	    * 
	    */
	   
	   
	   // CONSERVE TEMPORAIREMENT
	   
	   // CLONE ANCIEN QUI FAIT PLEIN DE TRUCS
	   public Graph clone() {
	      Graph image = null;

	      try {
	         image = (Graph) super.clone();
	      } catch (CloneNotSupportedException e) {
	      }

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
