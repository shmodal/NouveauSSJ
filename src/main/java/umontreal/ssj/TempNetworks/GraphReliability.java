package umontreal.ssj.TempNetworks;


import java.io.*;
import java.util.*;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.TempNetworks.staticreliability.SamplerType;
import umontreal.ssj.probdist.*;
import umontreal.ssj.randvar.*;


/**
 * This class implements a stochastic non-oriented weighted graph (a network).<br />
 * <p>
 * <b>Non oriented weighted graph</b><br />
 * A Graph object is defined by a set of {@link Node}'s and a set of
 * {@link Link}'s. We assume that each node is connected with at least one
 * another node, and a link connect only two nodes. Hence, it is a non-oriented
 * graph which connects the source and the target. <br />
 * 
 * <p>
 * <b>Construction of a Graph</b><br />
 * 1st option : by hand(adding nodes and links).
 * 2nd option :
 * We assume a Graph is built from a text file which must be as follows:<br />
 * 
 * NumberOfNodes NumberOfLinks SourceNode TargetNode <br />
 * NodeI NodeJ Reliability<br />
 * ...<br />
 * We give an example of a basic network connecting corners of a square <br />
 * 4 4 0 2 <br />
 * 0 1 0.9 <br />
 * 0 3 0.8 <br />
 * 1 2 0.6 <br />
 * 2 3 0.8 <br />
 * <p>
 * <i> The choice to give the reliability inside the text file is arbitrary. If
 * we assume Graph is homogeneous (i.e. each link has the same reliability), we
 * can change the reliability of the network with one of its method. An update
 * should give the possibility of relaxing the homogeneous assumption. </i>
 * 
 */



public class GraphReliability extends GraphNonOriented<NodeBasic,LinkReliability> {
	  
	/**
	    * Subset of nodes that must be connected to make the network operational.
	    * Very often, it contains only 2 nodes: the source and the target. In the
	    * extreme case, it may contain all the nodes of the network.
	    */
	   private int[] m_V0;
	   
	   /**
	    * Name of file containing the parameters of this graph
	    */
	   String filename = "";
	   
	
	public GraphReliability() {
		numNodes = 0;
		numLinks = 0;
		this.nodes = new ArrayList<NodeBasic>();
		this.links = new ArrayList<LinkReliability>();
		this.m_V0 = new int[1];
	}
	public GraphReliability(ArrayList<NodeBasic> nodes, ArrayList<LinkReliability> links) {
		numNodes = nodes.size();
		numLinks = links.size();
		this.nodes = nodes;
		this.links = links;
		this.m_V0 = new int[1];
	}
	
	public GraphReliability(ArrayList<NodeBasic> nodes, ArrayList<LinkReliability> links, int[] m_V0) {
		numNodes = nodes.size();
		numLinks = links.size();
		this.nodes = nodes;
		this.links = links;
		this.m_V0 = m_V0;
	}
	
	  /**
	    * Define graph given a .txt file
	    * 
	    * @param file
	    *           file name
	    * @throws java.io.IOException
	    */
	   public GraphReliability(String file) throws IOException {
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

	      links = new ArrayList<LinkReliability>();
	      nodes = new ArrayList<NodeBasic>();
	      for (int i = 0; i < numNodes; i++)
	         nodes.add(new NodeBasic(i));

	      int pos = 0;

	      for (int i = 0; i < numLinks; i++) {
	         int a, b;
	         double prob;

	         pos = 3 * i + 4;

	         a = Integer.parseInt(ss[pos]);
	         b = Integer.parseInt(ss[pos + 1]);
	         nodes.get(a).incCounter();
	         nodes.get(b).incCounter();
	         prob = Double.parseDouble(ss[pos + 2]);
	         
	         // VERIFIER
	         //links.add(new LinkWithCapacity(i, a, b));
	         links.add(new LinkReliability(i, a, b));
	         links.get(i).setR(prob);
	      }

	      for (int i = 0; i < numNodes; i++) {
	    	  //nodes.get(i).setNodeLinks(new int[nodes.get(i).getCounter()]);
	    	  int n = nodes.get(i).getCounter();
	          ArrayList<Integer> myLinks = new ArrayList<Integer>(n);
	    	  nodes.get(i).setNodeLinks(myLinks);
	    	  // for the next step
	    	  nodes.get(i).setCounter(0);
	      }

	      for (int i = 0; i < numLinks; i++) {
	         int a = links.get(i).getSource();
	         int b = links.get(i).getTarget();
	         nodes.get(a).setNodeLink(nodes.get(a).getCounter(), links.get(i).getIndice());
	         nodes.get(b).setNodeLink(nodes.get(b).getCounter(), links.get(i).getIndice());
	         nodes.get(a).incCounter();
	         nodes.get(b).incCounter();
	      }
	   }
	 

	   
	   /**
	    * Sets the same reliability for all links of the graph.
	    * 
	    * @param r
	    *           reliability
	    */
	   public void setReliability(double r) {
	      for (int i = 0; i < numLinks; i++)
	    	  links.get(i).setR(r);
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
		   links.get(i).setR(r);
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
	    	  links.get(i).setR(R[i]);
	   }

	   
	   /**
	    * Gets the <em>unreliability</em> of all the links of the graph.
	    * 
	    * @return unreliabilities
	    */
	   
   
	   public double[] getUnreliability() {
	      double[] A = new double[numLinks];
	      for (int i = 0; i < numLinks; i++)
	         A[i] = 1.0 - links.get(i).getR();
	      return A;
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

	// A REFAIRE
	   
	   
	   @Override
	   public GraphReliability clone() {
	      //GraphReliability image = null;
	      
	      GraphReliability image = new GraphReliability();

	      //try {
	      //   image = (GraphReliability) super.clone();
	      //} catch (CloneNotSupportedException e) {
	      //}
	      //image = (GraphReliability) super.clone();
	      image.numLinks = numLinks;
	      image.numNodes = numNodes;

	      image.m_V0 = new int[m_V0.length];
	      System.arraycopy(m_V0, 0, image.m_V0, 0, m_V0.length);

	      // Link
	      image.links = new ArrayList<LinkReliability>();
	      for (int i = 0; i < numLinks; i++) {
	         image.links.add(new LinkReliability(links.get(i).getIndice(), links.get(i).getSource(),
	        		 links.get(i).getTarget(), links.get(i).getR()));
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
	            }
	            image.nodes.add( new NodeBasic(0, nodes.get(i).getNumber(), clonemylink));
	         } else {
	            image.nodes.add(new NodeBasic());
	         }
	      }

	      return image;
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
	         LinkReliability link = getLink(j);
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
	    * Returns the id of the target node, if Card(V0) = 2.
	    * @return target node
	    */
	   public int getTarget() {
	      if (m_V0.length != 2)
	         throw new IllegalArgumentException ("many target nodes");
	      return m_V0[1];
	   }  
	

	
}
