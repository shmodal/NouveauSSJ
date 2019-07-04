package umontreal.ssj.networks.flow.old;

import java.io.*;
import java.util.*;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.rng.RandomStream;


/**
 * This class implements lists of shocks which make sets of links in a graph
 * fail simultaneously.
 * 
 * @author Richard Simard
 * @since juillet 2013
 */
public class ShockList implements Cloneable {

   /** The List contains the list of shocks; each shock is a set of links
    * that fail all together. */
   private List<Set<Integer>> shocks;
   private double[] lambda; // rate for each shock
   private int[] count; // counts the number of shocks in which link j appears
   String filename = "";  // file name containing the shocks
   Graph graph;
   // when using anti-shock for connectivity = true; otherwise false.
   private boolean antiConnect = false;

   
   
   /**
    * Reads the list of shocks from the text file <tt>filename</tt>. Each
    * line of the file describes a shock: the first number is the shock rate
    * (a real number), and the next numbers are an arbitrary number of
    * links (integers) that are associated with this shock.</br> Lines whose
    * first non-white character is a # are treated as comments and
    * disregarded by the reading program. </br> 
    * Example of a shock with 4 failing links:
    * <pre>0.9  3 0 5 2</pre>
    * 
    * @param graph
    *           associated graph
    * @param filename
    *           list of shocks
    * @throws IOException
    */
   public ShockList(Graph graph, String filename) throws IOException {
      this.graph = graph;
      int numLinks = graph.getNumLinks();  // number of links of graph
      count = new int[numLinks];
      shocks = new ArrayList<Set<Integer>>();
      List<Double> lamb = new ArrayList<Double>();
      this.filename = splitFileName(filename) ;
      BufferedReader input = new BufferedReader(new FileReader(filename));
      String line;
      Integer link;
      Double rate;
      Locale loc = Locale.getDefault();
      Locale.setDefault(Locale.US); // to read reals as 8.3 instead of 8,3

      while ((line = input.readLine()) != null) {
         line = line.trim();
         // split line into words
         String[] token = line.split("[ \t]+");

         // lines starting with # are comments; there can be spaces before the #
         if ((0 == token[0].compareTo("")) || (0 == token[0].compareTo("#")))
            continue;

         // first element of line is the shock rate
         rate = Double.valueOf(token[0]);
         lamb.add(rate);

         // other elements are the link numbers affected by the shock
         Set<Integer> choc = new HashSet<Integer>();
         // put all the links on this line in the set
         for (int i = 1; i < token.length; i++) {
            link = Integer.valueOf(token[i]);
            if (link >= numLinks)
               throw new IllegalArgumentException(
                     "link >= number of links of graph");
            ++count[link];
            choc.add(link);
         }
         shocks.add(choc);
      }

      Locale.setDefault(loc);
      input.close();
      int kappa = lamb.size(); // number of shocks
      lambda = new double[kappa];
      for (int j = 0; j < kappa; j++)
         lambda[j] = lamb.get(j);
      lamb.clear();
      lamb = null;
   }

   
   public ShockList() {
   }

   /**
    * Adds all shocks in list <tt>list</tt> to this object. The resulting list
    * of this object contains all shocks of the two lists.
    * @param list list of shocks
    */
   public void addAll(ShockList list) {
      if (graph != list.graph)
         throw new IllegalArgumentException
           ("graph must be the same for both shocks lists");
      shocks.addAll(list.shocks);
      int m = graph.getNumLinks();
      for (int i = 0; i < m; ++i) {   // update links counters of shocks
         count[i] += list.getCount(i);
      }
      int kap1 = lambda.length; // number of shocks 1
      double[] rates2 = list.getRates();
      int kap2 = rates2.length; // number of shocks 2
      int kappa = kap1 + kap2;
      double[] lam = new double[kappa];
      for (int j = 0; j < kap1; j++)
         lam[j] = lambda[j];
      for (int j = 0; j < kap2; j++)
         lam[j + kap1] = list.lambda[j];
      lambda = null;
      lambda = lam;
      filename = "shocks from nodes AND links failure";
   }
   
   
      
   /**
    * Generate the shocks by considering nodes failure. One shock is 
    * made of all links incident on a node. The source and the sink nodes
    * are excluded: there are no shock on these nodes.
    * There are as many shocks as there are nodes that are not in V_0.
    * The shocks rates are not set by this method: 
    * use method <tt>setRates</tt> to do that.
    * @param graph base graph
    * @return the shocks list 
    */
   public static ShockList shocksFromNodesFailure(Graph graph) {
      ShockList maliste = new ShockList();
      maliste.filename = "shocks from nodes failure";
      maliste.graph = graph;
      int numLinks = graph.getNumLinks();
      maliste.count = new int[numLinks];
      int numNodes = graph.getNumNodes();
      final int[] V0 = graph.getV0();
      int n = numNodes - V0.length;  // number of shocks from nodes failure
      maliste.lambda = new double[n];
      maliste.shocks = new ArrayList<Set<Integer>>(n);
      LinkWithCapacity link;
      int s;
      
      for (int i = 0; i < numNodes; ++i) {   // visit all nodes
         if (isInV0 (V0, i))  // exclude nodes in V0
            continue;
         Set<Integer> unchoc = new HashSet<Integer>();
         int numNeighbors = graph.getNumberOfNeighbors(i);
         for (int j = 0; j < numNeighbors; j++) { // visit all links of node
            link = graph.getLinkFromNode(j, i);
            s = link.getIndice();
            ++(maliste.count[s]);
            unchoc.add(s);
         }
         maliste.shocks.add(unchoc);
      }
      
      return maliste;
   }
   
   /**
    * If node r is in V0, return true; else return false
    * @param V0
    * @param r
    * @return
    */
   private static boolean isInV0 (int[] V0, int r) {
      int len = V0.length;
      for (int i = 0; i < len; ++i) {
         if (r == V0[i])
            return true;
      }
      return false;
   }
   
   /**
    * Generate the shocks by considering links failure. One shock fails
    * only one link. There are as many shocks as there are links. 
    * The shocks rates are not set by this method: use
    * method <tt>setRates</tt> to do that.
    * @param graph base graph
    * @return the shocks list 
    */
   public static ShockList shocksFromLinksFailure(Graph graph) {
      ShockList maliste = new ShockList();
      maliste.filename = "shocks from links: 1 shock fails 1 link ";
      maliste.graph = graph;
      int m = graph.getNumLinks();
      maliste.count = new int[m];
      maliste.lambda = new double[m];
      maliste.shocks = new ArrayList<Set<Integer>>(m);
     
      for (int i = 0; i < m; ++i) {   // visit all links
         Set<Integer> unchoc = new HashSet<Integer>(1);
         maliste.count[i] = 1;
         unchoc.add(i);
         maliste.shocks.add(unchoc);
      }
      
      return maliste;
   }
     
   /**
    * Sets all shocks rate to the value x.
    * @param x
    */
   public void setRates (double x) {
      int n = shocks.size();
      for (int i = 0; i < n; ++i)
         lambda[i] = x;
   }
   
   
   /**
    * Sets shocks rate i to the value rate[i].
    * @param rate shocks rate
    */
   public void setRates (double[] rate) {
      int n = shocks.size();
      for (int i = 0; i < n; ++i)
         lambda[i] = rate[i];
   }
   
   
   /**
    * Sets shock i rate to the value x.
    * @param x
    */
   public void setRate (int i, double x) {
      lambda[i] = x;
   }
      
   /**
    * Prints the list of all shocks.
    */
   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer("Shocks list: " + PrintfFormat.NEWLINE);
      int j = 0;
      for (Collection<Integer> unchoc : shocks) {
         sb.append(lambda[j] + " ");
         j++;
         for (Integer lien : unchoc) {
            sb.append(" " + lien.intValue());
         }
         sb.append(PrintfFormat.NEWLINE);
      }
      return sb.toString();
   }

   /**
    * Inits all counts to 0.
    */
   public void initCounts () {
      for (int j = 0; j < count.length; j++)
         count[j] = 0;  
   }
   
   /**
    * Returns the rate of shock j (counting from 0).
    * 
    * @param j
    * @return rate of shock j.
    */
   public double getRate(int j) {
      return lambda[j];
   }

   /**
    * Returns all shock rates.
    * 
    * @return shock rates.
    */
   public double[] getRates() {
      return lambda;
   }

   /**
    * Returns the links of shock j. Shock counting starts at 0.
    * 
    * @param j
    * @return links of shock j
    */
   public Set<Integer> getShock(int j) {
      return shocks.get(j);
   }

   /**
    * Returns the list of all shocks.
    * 
    * @return all shocks
    */
   public List<Set<Integer>> getShocks() {
      return shocks;
   }

   /**
    * Returns the number of shocks in which link j appears. Link counting starts
    * at 0.
    * 
    * @param j
    * @return number of shocks for link j
    */
   public int getCount(int j) {
      return count[j];
   }

   /**
    * Returns the number of shocks in which each link appears.
    * 
    * @return number of shocks for all links
    */
   public int[] getCounts() {
      return count;
   }

   
   @Override
   public ShockList clone() {
      ShockList image = null;
      try {
         image = (ShockList) super.clone();
      } catch (CloneNotSupportedException e) {
         throw new InternalError (
               "CloneNotSupportedException for class ShockList");
      }
       
      // the rates and the shocks are the same for all chains; they will not
      // change: don't clone.
      image.lambda = lambda;
      image.shocks = shocks;
     
      // counts of failed links are changed bu the algorithms
      int n = count.length;     // num of links in graph
      image.count = new int[n];
      image.count = Arrays.copyOf(count, n);
      image.filename = filename;
      image.antiConnect = antiConnect;
      return image;
   }
   
   /**
    * Sets the flag for sampling shocks. 
    * <tt>true</tt> for antiShocks connectivity, <tt>false</tt> otherwise.
    * @param flag
    */
   public void setAntiConnect (boolean flag) {
      antiConnect = flag;
   }
   
   
   public boolean getAntiConnect () {
      return antiConnect;
   }
      
   /**
    * Sample the weight of a shock. 
    * a is either 0 or a threshold gamma.
    * @return a random weight
    */
   public double sample(RandomStream stream, int s, double a)
   {
      double w;
      if (antiConnect)
         w = ExponentialSampler.sample(stream, a, lambda[s]);
     else
         w = ExponentialSampler.sampleDestruct(stream, a, lambda[s]);
      return w;
   }

   /**
    * Removes shock k from this list of shocks.
    * @param k shock index
    */
   public void remove (int k) {
      Set<Integer> choc = shocks.remove(k);
      int newlen = lambda.length - 1;
      for (int i = k; i < newlen; ++i) {
         lambda[i] = lambda[i+1];
      }
      double[] lam = new double[newlen];
      lam = Arrays.copyOf(lambda, newlen);
      lambda = lam;
      for (int j : choc) {
         --count[j];
      }
   }
   
   
   /**
    * A shock list is usually built by reading its parameters
    * from a text file. Returns the name of that input file.
    * @return the file name of the shocks
    */
   public String getFileName() {
      return filename;
   }
   
   
   /**
    * Removes path directories from file name, i.e. removes everything in
    * the file name before the last /
    * 
    * @param fname
    * @return the simple file name
    */
   private String splitFileName(String fname) {
      int t = fname.lastIndexOf('/');
      if (t < 0)
         return fname;
      else
         return fname.substring(1 + t);
   }
}
