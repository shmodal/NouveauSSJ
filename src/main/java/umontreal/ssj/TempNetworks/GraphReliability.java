package umontreal.ssj.TempNetworks;


import java.io.*;



import java.util.*;

import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;
import umontreal.ssj.networks.staticreliability.SamplerType;
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
public class GraphReliability extends GraphNonOriented implements Cloneable {




   /**
    * Subset of nodes that must be connected to make the network operational.
    * Very often, it contains only 2 nodes: the source and the target. In the
    * extreme case, it may contain all the nodes of the network.
    */
   
   // PAS DANS BASIC
   
   private int[] m_V0;

   /**
    * All the links of the graph, dynamic structure to add after building
    */
   // Pour static /flow reliabiliyt, constructeur avec int[] : recopier arraylist
   // pour rendre ça statique
   
   
   private ArrayList<LinkReliability> links;

   /**
    * All the nodes of the graph, dynamic structure to add after building
    */
   private ArrayList<NodeBasic> nodes;

   /**
    * Name of file containing the parameters of this graph
    */
   
   // PAS DANS BASIC
   
   String filename = "";
   
   
   
   // PAS DANS BASIC

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
         links.add(new LinkCapacity(i, a, b));
         links.get(i).setR(prob);
      }

      for (int i = 0; i < numNodes; i++) {
    	  nodes.get(i).setNodeLinks(new int[nodes.get(i).getCounter()]);
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
    * Add a link if users want
    * 
    * @return void
    */
   public void addLink(LinkReliability l) {
	   this.links.add(l);
	   this.numLinks++;
	   int a = l.getSource();
       int b = l.getTarget();
       
   }




   /**
    * Get the i-th link
    * 
    * @param i
    *           the number of wanted link
    * @return return the i-th link
    */
   public LinkReliability getLink(int i) {
      return links.get(i);
   }

   /**
    * Get links of the graph
    * 
    * @return return a tab of links contains links of the graph
    */
   public ArrayList<LinkReliability> getLinks() {
      return links;
   }

   // MIS DANS BASIC, A VERIFIER
   
   /**
    * Return the j-th Link connected to Node i
    * 
    * @param i
    *           the number of the given node
    * @param j
    *           the number of the given link
    * @return return link j connected to node i
    */
   public LinkReliability getLinkFromNode(int j, int i) {
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
      return nodes.get(i).getNodeLinks().length;
   }

   //VERIFIER SI OK BASIC
   
   /**
    * Returns the node connected to node i by Link link
    * 
    * @param link
    * @param i
    * @return the neighbor of i
    */
   public int getNeighborOfNode(LinkReliability link, int i) {
      if (link.getSource() == i)
         return link.getTarget();
      return link.getSource();
   }

   
   
   // VALABLE POUR GRAPHE NON ORIENTE. VERSION ORIENTEE EN BAS
   
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
   
   // VERSION ORIENTEE
   
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
         a = links.get(k).getSource();
         b = links.get(k).getTarget();
         if (a == i && b == j) {
            break;
         }
         k++;
      }
      if (k == numLinks)
         return -1;
      return links.get(k).getIndice();
   }

// PAS DANS BASIC
   
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

// PAS DANS BASIC
   
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

// PAS DANS BASIC
   
   
   /**
    * Sets the reliability for all links of the graph. R must have the same
    * number of elements as the number of links.
    * 
    * @param R
    *           reliabilities
    */
   
// PAS DANS BASIC
   
   
   public void setReliability(double[] R) {
      for (int i = 0; i < numLinks; i++)
    	  links.get(i).setR(R[i]);
   }

   
   /**
    * Gets the <em>unreliability</em> of all the links of the graph.
    * 
    * @return unreliabilities
    */
   
// PAS DANS BASIC
   
   
   public double[] getUnreliability() {
      double[] A = new double[numLinks];
      for (int i = 0; i < numLinks; i++)
         A[i] = 1.0 - links.get(i).getR();
      return A;
   }
   
   
// PAS DANS BASIC

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
      GraphReliability image = null;

      try {
         image = (GraphReliability) super.clone();
      } catch (CloneNotSupportedException e) {
      }

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
            int clonemylink[] = new int[nodes.get(i).getNodeLinks().size()];
            for (int j = 0; j < clonemylink.length; j++) {
               clonemylink[j] = nodes.get(i).getNodeLink(j);
            }
            image.nodes.add( new NodeBasic(0, nodes.get(i).getNumber(), clonemylink));
         } else {
            image.nodes.add(new NodeBasic());
         }
      }

      return image;
   }

   
   // ToString pour non orienté
   
   
   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(
            "================================================= Graph"
                  + PrintfFormat.NEWLINE);
      for (int i = 0; i < numLinks; i++) {
         LinkReliability link = links.get(i);
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
         for (int j = 0; j < r - 1; j++) {
            sb.append(node.getNodeLink(j) + ", ");
         }
         sb.append(node.getNodeLink(r - 1));
         sb.append(PrintfFormat.NEWLINE);
      }
      return sb.toString();
   }

   
   // PAS DANS BASIC
   
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
   
   // PAS DANS BASIC

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

   
   // PAS DANS BASIC
   
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
   
   // PAS DANS BASIC

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

   
   // PAS DANS BASIC
   
   /**
    * Returns the id of the target node.
    * @return target node
    */
   public int getTarget() {
      if (m_V0.length != 2)
         throw new IllegalArgumentException ("many target nodes");
      return m_V0[1];
   }  
}
