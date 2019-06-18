package umontreal.ssj.TempNetworksInterface;

import java.util.ArrayList;

//import umontreal.ssj.TempNetworks.GraphBasic;
//import umontreal.ssj.TempNetworks.LinkBasic;
//import umontreal.ssj.TempNetworks.NodeBasic;

public interface Graph<N,L> extends Cloneable {
	   void addNode(N n);
	   
	   /**
	    * Add a link if users want
	    * 
	    * @return void
	    */
	   //public void addLink(LinkBasic l) {
	//	   this.links.add(l);
	//	   this.numLinks++;
	//   }
	   
	   void addLink(L l);
	   /**
	    * Get the number of nodes
	    * 
	    * @return return the number of nodes
	    */
	   int getNumNodes();

	   /**
	    * Get the i-th node
	    * 
	    * @param i
	    *           the number of wanted node
	    * @return return the i-th node
	    */
	   N getNode(int i);

	   /**
	    * Get nodes of the graph
	    * 
	    * @return return a tab of nodes contains nodes of the graph
	    */
	   ArrayList<N> getNodes();

	   /**
	    * Get the number of links
	    * 
	    * @return return the number of links
	    */
	   int getNumLinks();

	   /**
	    * Get the i-th link
	    * 
	    * @param i
	    *           the number of wanted link
	    * @return return the i-th link
	    */
	   L getLink(int i);

	   /**
	    * Get links of the graph
	    * 
	    * @return return a tab of links contains links of the graph
	    */
	   ArrayList<L> getLinks();
	   
	   
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
	   public L getLinkFromNode(int j, int i);
	   
	   /**
	    * Return the number of neighbors of node i
	    * 
	    * @param i
	    *           node number
	    * @return return the number of neighbors of i
	    */
	   public int getNumberOfNeighbors(int i);
	   
	   
	   // VERIFIER
	   //Probablemet a mettre pour graphe orienté
	   /**
	    * Returns the node connected to node i by Link link
	    * 
	    * @param link
	    * @param i
	    * @return the neighbor of i
	    */
	   int getNeighborOfNode(L link, int i);
	   
	   
	   // a verifier
	   
	   //@Override
	   public Graph<N,L> clone();

}
