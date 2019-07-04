package umontreal.ssj.networks.flow.old;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.io.*;

import java.util.LinkedList;

import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;

import umontreal.ssj.randvar.RandomVariateGen;
import umontreal.ssj.util.PrintfFormat;

public class Graph implements Cloneable{
	    /**
	     * Number of nodes
	     */
	    protected int numNodes;
	
	    /**
	     * Number of links
	     */
	    protected int numLinks;
	
	    /**
	     * Subset of nodes that must be connected to make the network operational.
	     * Very often, it contains only 2 nodes: the source and the target. In the
	     * extreme case, it may contain all the nodes of the network.
	     */
	    protected int[] m_V0;
	
	    /**
	     * All the links of the graph
	     */
	    protected LinkWithCapacity[] links;
	
	    /**
	     * All the nodes of the graph
	     */
	    protected Node[] nodes;
	
	    /**
	     * Name of file containing the parameters of this graph
	     */
	    String filename = "";

	   /**
	    * Define graph given a .txt file
	    * 
	    * @param file
	    *           file name
	    * @throws java.io.IOException
	    */
	    public Graph(String file) throws IOException {
	        m_V0 = new int[2]; // by default, contains source and target.
	        // read in file
	        BufferedReader br = new BufferedReader(new FileReader(file));
	        splitFileName(file);
	        String f, l;
	        String[] ss;
	        f = "";
	        l = br.readLine();
	        int index = l.indexOf('#'); 
	        // if first line contains a comment #..., remove it
	        if (index >= 0)
	           l = l.substring(0, index);

	        do {
	           f += l + "\t";
	           l = br.readLine();
	        } while (l != null);

	        ss = f.split("[\t ]+");

	        numNodes = Integer.parseInt(ss[0]);
	        numLinks = Integer.parseInt(ss[1]);
	        m_V0[0] = Integer.parseInt(ss[2]);
	        m_V0[1] = Integer.parseInt(ss[3]);

	        links = new LinkWithCapacity[numLinks];
	        nodes = new Node[numNodes];
	        for (int i = 0; i < numNodes; i++)
	           nodes[i] = new Node(i);

	        int pos = 0;

	        for (int i = 0; i < numLinks; i++) {
	           int a, b;
	           double prob;

	           pos = 3 * i + 4;

	           a = Integer.parseInt(ss[pos]);
	           b = Integer.parseInt(ss[pos + 1]);
	           nodes[a].incCounter();
	           nodes[b].incCounter();
	           prob = Double.parseDouble(ss[pos + 2]);
	           links[i] = new LinkWithCapacity(i, a, b);
	           links[i].setR(prob);
	        }

	        for (int i = 0; i < numNodes; i++) {
	           nodes[i].setNodeLinks(new int[nodes[i].getCounter()]);
	           // for the next step
	           nodes[i].setCounter(0);
	        }

	        for (int i = 0; i < numLinks; i++) {
	           int a = links[i].getSource();
	           int b = links[i].getTarget();
	           nodes[a].setNodeLink(nodes[a].getCounter(), links[i].getIndice());
	           nodes[b].setNodeLink(nodes[b].getCounter(), links[i].getIndice());
	           nodes[a].incCounter();
	           nodes[b].incCounter();
	        }
	     }
	    

	    
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
	   public Node getNode(int i) {
	      return nodes[i];
	   }

	   /**
	    * Get nodes of the graph
	    * 
	    * @return return a tab of nodes contains nodes of the graph
	    */
	   public Node[] getNodes() {
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
	   public LinkWithCapacity getLink(int i) {
	      return links[i];
	   }

	   /**
	    * Get links of the graph
	    * 
	    * @return return a tab of links contains links of the graph
	    */
	   public LinkWithCapacity[] getLinks() {
	      return links;
	   }

	   /**
	    * Return the j-th Link connected to Node i
	    * 
	    * @param i
	    *           the number of the given node
	    * @param j
	    *           the number of the given link
	    * @return return link j connected to node i
	    */
	   public LinkWithCapacity getLinkFromNode(int j, int i) {
	      return links[nodes[i].getNodeLink(j)];
	   }

	   /**
	    * Return the number of neighbors of node i
	    * 
	    * @param i
	    *           node number
	    * @return return the number of neighbors of i
	    */
	   public int getNumberOfNeighbors(int i) {
	      return nodes[i].getNodeLinks().length;
	   }

	   /**
	    * Returns the node connected to node i by Link link
	    * 
	    * @param link
	    * @param i
	    * @return the neighbor of i
	    */
	   public int getNeighborOfNode(LinkWithCapacity link, int i) {
	      if (link.getSource() == i)
	         return link.getTarget();
	      return link.getSource();
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
	         a = links[k].getSource();
	         b = links[k].getTarget();
	         if (a == i && b == j) {
	            break;
	         }
	         k++;
	      }
	      if (k == numLinks)
	         return -1;
	      return links[k].getIndice();
	   }
	   
	   /**
	    * Similar to the previous function but the source and the sink matters
	    * Return the link which connects nodes source i and sink j
	    * 
	    * @param i
	    * @param j
	    * @return To obtain the link from i to j
	    */
	   public int getLinkWithSourceAndSinkNodes(int i, int j) {
	      int k = 0;
	      while (k < numLinks) {
	         int a, b;
	         a = links[k].getSource();
	         b = links[k].getTarget();
	         if (a == i && b == j) {
	            break;
	         }
	         k++;
	      }
	      if (k == numLinks)
	         return -1;
	      return links[k].getIndice();
	   }

	   /**
	    * Sets the same reliability for all links of the graph.
	    * 
	    * @param r
	    *           reliability
	    */
	   public void setReliability(double r) {
	      for (int i = 0; i < numLinks; i++)
	         links[i].setR(r);
	   }

	   /**
	    * Sets the reliability r for link i of the graph.
	    * 
	    * @param i
	    *           link
	    * @param r
	    *           reliability
	    */
	   public void setReliability(int i, double r) {
	      links[i].setR(r);
	   }

	   /**
	    * Sets the reliability for all links of the graph. R must have the same
	    * number of elements as the number of links.
	    * 
	    * @param R
	    *           reliabilities
	    */
	   public void setReliability(double[] R) {
	      for (int i = 0; i < numLinks; i++)
	         links[i].setR(R[i]);
	   }

	   
	   /**
	    * Gets the <em>unreliability</em> of all the links of the graph.
	    * 
	    * @return unreliabilities
	    */
	   public double[] getUnreliability() {
	      double[] A = new double[numLinks];
	      for (int i = 0; i < numLinks; i++)
	         A[i] = 1.0 - links[i].getR();
	      return A;
	   }
	   
	   /**
	    * Sets the same Capacity for all links of the graph.
	    * 
	    * @param r
	    *           Capacity
	    */
	   public void setCapacity(int capacity) {
		   for (int i = 0; i < this.numLinks; i++)
			   links[i].setCapacity(capacity);
	   }

	   /**
	    * Sets the Capacity r for link i of the graph.
	    * 
	    * @param i
	    *           link
	    * @param r
	    *           Capacity
	    */
	   public void setCapacity(int i, int capacity) {
		   links[i].setCapacity(capacity);
	   }

	   /**
	    * Sets the Capacities for all links of the graph. Capacity must have the same
	    * number of elements as the number of links.
	    * 
	    * @param R
	    *           Capacities
	    */
	   public void setCapacity(int[] Capacities) {
		   for (int i = 0; i < numLinks; i++)
			   links[i].setCapacity(Capacities[i]);
	   }

	   /**
	    * Generate a random length for all links of the graph using <tt>gen</tt>.
	    * 
	    * @param gen
	    *           Random number generator
	    */
	   public void generate(RandomVariateGen gen) {
	      for (int i = 0; i < numLinks; i++) {
	         // links[i].setWeight(gen.nextDouble());
	      }
	   }


	   @Override
	   public Graph clone() {
		  Graph image = null;

	      try {
	         image = (Graph) super.clone();
	      } catch (CloneNotSupportedException e) {
	      }

	      image.numLinks = numLinks;
	      image.numNodes = numNodes;

	      image.m_V0 = new int[m_V0.length];
	      System.arraycopy(m_V0, 0, image.m_V0, 0, m_V0.length);

	      // Link
	      image.links = new LinkWithCapacity[numLinks];
	      for (int i = 0; i < numLinks; i++) {
	         image.links[i] = new LinkWithCapacity(links[i].getIndice(), links[i].getSource(),
	               links[i].getTarget(), links[i].getR());
	      }

	      // nodes
	      image.nodes = new Node[numNodes];
	      for (int i = 0; i < numNodes; i++) {
	         if (nodes[i].getNodeLinks() != null) {
	            int clonemylink[] = new int[nodes[i].getNodeLinks().length];
	            for (int j = 0; j < clonemylink.length; j++) {
	               clonemylink[j] = nodes[i].getNodeLink(j);
	            }
	            image.nodes[i] = new Node(0, nodes[i].getNumber(), clonemylink);
	         } else {
	            image.nodes[i] = new Node();
	         }
	      }

	      return image;
	   }

	   
	   /**
	    * Generate the residual graph of the current grapWithCapacity.
	    */
      public Graph residual() {
   	      Graph image = null;

   	      try {
   	         image = (Graph) super.clone();
   	      } catch (CloneNotSupportedException e) {
   	      }
   	      
   	      /*Storing the edges that are not present in both ways : source--»target and target--»source */
   	      LinkedList<Integer> Queue = new LinkedList<Integer>();
   	      for (int i = 0; i < numLinks; i++) {
   	    	  if(getLinkWithSourceAndSinkNodes(links[i].getTarget(),links[i].getSource())==-1) {
   			      Queue.add(i);
   		      }
   	      }
   	      
   	      image.numLinks = numLinks+Queue.size();
   	      image.numNodes = numNodes;

   	      image.m_V0 = new int[m_V0.length];
   	      System.arraycopy(m_V0, 0, image.m_V0, 0, m_V0.length);

   	      // Link
   	      image.links = new LinkWithCapacity[image.numLinks];
   	      for (int i = 0; i < numLinks; i++) {
   	         image.links[i] = new LinkWithCapacity(links[i].getIndice(), links[i].getSource(),
   	               links[i].getTarget(), links[i].getR(),links[i].getCapacity());
   	      }
   	      
   	      /*counter to create the new links*/
   	      int counterIndiceLink=this.numLinks;
   	      while(!Queue.isEmpty()) {
   		     /*we pop the first element of the queue*/
   		     int duplicate=Queue.poll();
   		     image.links[counterIndiceLink] = new LinkWithCapacity(counterIndiceLink, links[duplicate].getTarget(),
   		        		 links[duplicate].getSource(), links[duplicate].getR(), 0);
   		     counterIndiceLink++;

   	      }

   	      // nodes
   	      /*change this part to add the edge in "connects nodes" (normally not important*/
   	      image.nodes = new Node[numNodes];
   	      for (int i = 0; i < numNodes; i++) {
   	         if (nodes[i].getNodeLinks() != null) {
   	            int clonemylink[] = new int[nodes[i].getNodeLinks().length];
   	            for (int j = 0; j < clonemylink.length; j++) {
   	               clonemylink[j] = nodes[i].getNodeLink(j);
   	            }
   	            image.nodes[i] = new Node(0, nodes[i].getNumber(), clonemylink);
   	         } else {
   	            image.nodes[i] = new Node();
   	         }
   	      }

   	      return image;
   	   }
      
      
	   @Override
	   public String toString() {
	      StringBuffer sb = new StringBuffer(
	            "================================================= Graph"
	                  + PrintfFormat.NEWLINE);
	      for (int i = 0; i < numLinks; i++) {
	         LinkWithCapacity link = links[i];
	         sb.append("link  " + link.getIndice() + "  connects nodes  "
	               + link.getSource() + ", " + link.getTarget() + ", " 
	               +link.getCapacity() + PrintfFormat.NEWLINE);
	      }
	      sb.append(PrintfFormat.NEWLINE + "----------------------------------"
	                + PrintfFormat.NEWLINE);
	      for (int i = 0; i < numNodes; i++) {
	         Node node = nodes[i];
	         int r = node.getNodeLinks().length;
	         sb.append("node  " + node.getNumber());
	         sb.append("  has " + r + " links: ");
	         for (int j = 0; j < r - 1; j++) {
	            sb.append(node.getNodeLink(j) + ", ");
	         }
	         sb.append(node.getNodeLink(r - 1));
	         sb.append(PrintfFormat.NEWLINE);
	      }
	      return sb.toString();
	   }

	   /**
	    * Choose the sampler used to generate random weights for each link of the
	    * graph. If <tt>destructFlag</tt> is true, the destructive schema is used;
	    * if <tt>destructFlag</tt> is false, the constructive schema is used.
	    * 
	    * @param sam
	    *           which kind of random sampler is used
	    * @param destructFlag
	    *           true for the destructive schema
	    */
	   public void setSampler(SamplerType sam, boolean destructFlag) {
	      int m = getNumLinks();
	      for (int j = 0; j < m; j++) {
	         LinkWithCapacity link = getLink(j);
	         link.setSampler(sam, destructFlag);
	      }
	   }

	   /**
	    * A graph is usually built by reading its parameters (nodes and edges) from
	    * a text file. Returns the name of that input file.
	    * 
	    * @return the file name of the graph
	    */
	   public String getFileName() {
	      return filename;
	   }

	   private void splitFileName(String file) {
	      int t = file.lastIndexOf('/');
	      int s = file.lastIndexOf('.');
	      if (s < 0)
	         filename = file.substring(1 + t);
	      else
	         filename = file.substring(1 + t, s);
	   }

	   /**
	    * Returns the subset of the nodes that must be connected to make the network
	    * operational. Most often, it contains only 2 nodes: the source and the
	    * target. In the extreme case, it may contain all the nodes of the network.
	    * 
	    * @return the subset of nodes V0
	    */
	   public int[] getV0() {
	      return m_V0;
	   }

	   /**
	    * Sets the subset of the nodes that must be connected to make the network
	    * operational to S, which contains the id of the nodes.
	    */
	   public void setV0(int[] S) {
	      int n = S.length;
	      m_V0 = new int[n];
	      for (int i = 0; i < n; i++)
	         m_V0[i] = S[i];
	   }

	   
	   /**
	    * Returns the id of the source node.
	    * @return source node
	    */
	   public int getSource() {
	      return m_V0[0];
	   }  

	   
	   /**
	    * Returns the id of the target node.
	    * @return target node
	    */
	   public int getTarget() {
	      if (m_V0.length != 2)
	         throw new IllegalArgumentException ("many target nodes");
	      return m_V0[1];
	   }  
	      
	   
	   
	   
	   
	   
	   public void initLinkLambda(int i) {
		   links[i].initLambda();
	   }
	   public void initJumpAndIndexes(int i) {
		   links[i].initJumpAndIndexes();
	   }
	   
	   public void setLambdaValues(double [] tab,int i) {
		   links[i].setLambdaValues(tab);
	   }
	   
	   public double[] getLambdaValues(int i) {
		   return links[i].getLambdaValues();
	   }
	   
	   public void setValuesY(double[] tab, int i) {
		   links[i].setValuesY(tab);
	   }
	   
	   public double[] getValuesY(int i) {
		   return links[i].getValuesY();
	   }
	   
	   public void setJump(int i, int k, int value) {
		   links[i].setJump(k, value);
	   }
	   

	   // New
	   
	   public void setCapacityValues(int i, int[] tab) {
		   links[i].setCapacityValues(tab);
	   }
	   
	   // Memes capacites, NEW
	   
	   public void setCapacityValues(int[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   links[i].setCapacityValues(tab);
			   }
	   }
	   
	   // NEW
	   
	   public void setB(int i, int k) {
		   links[i].setB(k);
	   }
	   
	   
	   // Meme B, NEW
	   
	   public void setB(int k) {
		   for (int i = 0; i < numLinks; i++) {
			   links[i].setB(k);
			   }
	   }
	   
	   
	   // New
	   
	   public void setProbabilityValues(int i, double[] tab) {
		   links[i].setProbabilityValues(tab);
	   }
	   
	   // Memes capacites, NEW
	   
	   public void setProbabilityValues(double[] tab) {
		   for (int i = 0; i < numLinks; i++) {
			   links[i].setProbabilityValues(tab);
			   }
	   }
	   
	   
	   public int getB(int i) {
		   return links[i].getB();
	   }
	   
	   public int[] getCapacityValues(int i) {
		   return links[i].getCapacityValues();
	   }
	   
	   public double[] getProbabilityValues(int i) {
		   return links[i].getProbabilityValues();
	   }
}
