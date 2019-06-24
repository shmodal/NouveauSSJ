package umontreal.ssj.TempNetworks;

import java.util.ArrayList;

/**
 *  This class provides nodes for a graph.
 */

public class NodeBasic {
	
	   //tricky variable
	   protected int counter;
	   /**
	    * The number of the node
	    */
	   private int number;
	   /**
	    * An array of links number connected to the node
	    */
	   //private int[] iLinks;
	   private ArrayList<Integer> iLinks;
	 
	   /**
	    * Default constructor
	    */
	   public NodeBasic()
	   {}

	   /**
	    * Full contructor
	    * @param counter define a tricky variable, usefull for initialisation
	    * @param indice define the number of the node
	    * @param nLinks define the links connected to the node
	    */
	   public NodeBasic(int counter, int indice, ArrayList<Integer> nLinks)
	   {
	      this.counter = counter;
	      this.number = indice;
	      this.iLinks = nLinks;
	   }

	   /**
	    * Useful constructor for basic initialization
	    * @param indice
	    */
	   public NodeBasic(int indice)
	   {
	      this.number = indice;
	      this.counter = 0;
	      this.iLinks = new ArrayList<Integer>() ;
	   }

	   /**
	    * Return a variable only used for the building of the graph
	    * @return the value of the counter
	    */
	   public int getCounter()
	   {
	      return counter;
	   }

	   /**
	    * Return the node's number
	    * @return To obtain the node's number
	    */
	   public int getNumber()
	   {
	      return number;
	   }


	   /**
	    * Useless function because it returns i
	    * @param i
	    * @return the link
	    */
	   public int getNodeLink(int i)
	   {
	      return iLinks.get(i);
	   }

	   /**
	    * return the array of links connected to this node
	    * @return the link
	    */
	   public ArrayList<Integer> getNodeLinks()
	   {
	      return iLinks;
	   }

	   /**
	    * Set the counter equals to counter
	    * @param counter
	    */
	   public void setCounter(int counter)
	   {
	      this.counter = counter;
	   }

	   /**
	    * Increase the counter by 1.
	    */
	   public void incCounter()
	   {
	      this.counter++;
	   }

	   /**
	    * Set the number ofnodeequals to number
	    * @param number
	    */
	   public void setNumber(int number)
	   {
	      this.number = number;
	   }

	   /**
	    * Define the links connected to the node
	    * @param myLinks
	    */
	   public void setNodeLinks(ArrayList<Integer> myLinks)
	   {
	      iLinks = myLinks;
	   }

	   /**
	    * Set the i-th link of the node to newvalue.
	    * @param i
	    * @param newvalue
	    */
	   public void setNodeLink(int i, int newvalue)
	   {
	      //iLinks[i] = newvalue;
	      iLinks.set(i, newvalue);
	   }
	   
	   
	   /**
	    * Add the link with id newvalue to iLinks.
	    * @param newvalue
	    */
	   
	   public void addNodeLink(int newvalue) {
		   iLinks.add(newvalue);
	   }

	 
	   /**
	    * Clone the node
	    * @return To obtain a clone of the node
	    */
	   @Override
	   public NodeBasic clone()
	   {
	      NodeBasic image = new NodeBasic();

	      try {
	         image = (NodeBasic) super.clone();
	      } catch (CloneNotSupportedException e) {
	         // No deberia suceder
	      }
	      image.counter = this.counter;
	      image.number = this.number;
	      int n = this.iLinks.size();
	      //int n = this.iLinks.length;
	      //image.iLinks = new int[n];
	      image.iLinks = new ArrayList<Integer>();
	      for (int i = 0; i < n; i++) {
	         //image.iLinks[i] = this.iLinks[i];
	         image.iLinks.add(iLinks.get(i));
	      }
	      return image;
	   }

	   /**
	    * Define the equals operator for nodes
	    * @param obj
	    * @return true if obj = this
	    */
	   public boolean equals(NodeBasic obj)
	   {
	      if (obj == null) {
	         return false;
	      }

	      final NodeBasic other = obj;

	      if (this.number != other.number) {
	         return false;
	      }

	      return true;
	   }


	   /**
	    * Print function in order to pint information about node
	    */
	   public void print()
	   {
	      System.out.println("PRINT NODE");
	      System.out.println("\t***--node " + this.number + " --***");
	   }

}
