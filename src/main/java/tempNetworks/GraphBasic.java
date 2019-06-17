package tempNetworks;


import java.io.*;

import java.util.*;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import sampling.SamplerType;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;



public abstract class GraphBasic implements Cloneable {

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
	   
	   
	   
	   /**
	    * Add a node if users want
	    * 
	    * @return void
	    */
	   //public void addNode(NodeBasic n) {
		//   this.nodes.add(n);
		//   this.numNodes++;
	   //}
	   public void addNode(NodeBasic n) {
		   this.nodes.add(n);
		   this.numNodes++;
	   }
	   
	   /**
	    * Add a link if users want
	    * 
	    * @return void
	    */
	   //public void addLink(LinkBasic l) {
	//	   this.links.add(l);
	//	   this.numLinks++;
	//   }
	   public abstract void addLink(LinkBasic l);
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
	   public NodeBasic getNode(int i) {
	      return nodes.get(i);
	   }

	   /**
	    * Get nodes of the graph
	    * 
	    * @return return a tab of nodes contains nodes of the graph
	    */
	   public ArrayList<NodeBasic> getNodes() {
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
	   public LinkBasic getLink(int i) {
	      return links.get(i);
	   }

	   /**
	    * Get links of the graph
	    * 
	    * @return return a tab of links contains links of the graph
	    */
	   public ArrayList<LinkBasic> getLinks() {
	      return links;
	   }
	   
	   
	   // Verifier si c'est fonctionnel que ca soit orient� ou pas
	   /**
	    * Return the j-th Link connected to Node i
	    * 
	    * @param i
	    *           the number of the given node
	    * @param j
	    *           the number of the given link
	    * @return return link j connected to node i
	    */
	   public LinkBasic getLinkFromNode(int j, int i) {
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
	   //Probablemet a mettre pour graphe orient�
	   /**
	    * Returns the node connected to node i by Link link
	    * 
	    * @param link
	    * @param i
	    * @return the neighbor of i
	    */
	   public int getNeighborOfNode(LinkBasic link, int i) {
	      if (link.getSource() == i)
	         return link.getTarget();
	      return link.getSource();
	   }
	   
	   
	   
	   // a verifier
	   
	   @Override
	   public GraphBasic clone() {
	      GraphBasic image = null;

	      try {
	         image = (GraphBasic) super.clone();
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
