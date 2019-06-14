package umontreal.ssj.TempNetworks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * This class creates the specification to completely define graphs of different
 * kinds according to our convention, and writes that specification in a text
 * file. The graphs are <em>non oriented weighted graphs</em> and contains at
 * most one edge between two nodes. We assume that each node is connected with
 * at least another node. These graphs are non-oriented graphs in which a source
 * node and a target node are connected. <br />
 * <br />
 *
 * The specification of the graph in the text file is as follows; the first line
 * contains: <br />
 * <tt> NumberOfNodes NumberOfLinks SourceNode TargetNode </tt> <br />
 * <p>
 *
 * Then each of the following lines describes an edge as follows:<br />
 *
 * <tt> Node1 Node2 reliability</tt> <br />
 * <p>
 *
 * Here is an example of a network connecting the corners of a square lattice.<br />
 *
 * <pre>
 * 4  4  0 2
 * 0  1  rel
 * 0  3  0.8
 * 1  2  0.6
 * 2  3  0.8
 * </pre>
 *
 * The reliability inside the text file is arbitrary. It is easy to change by
 * using methods of class {@link Graph}. It is kept here for backward
 * compatibility with previous text files.
 *
 * @author Richard simard
 */
public class GraphCreator
{
   private static final double REL0 = -1; // default reliability

   /**
    * Creates an output text file that contains the specification for a stripes
    * lattice graph. It is like a set of vertical stripes. The x-sides of the
    * graph have n nodes, and the y-sides have m nodes. The graph has vertical
    * edges between each adjacent nodes. It has horizontal edges only on the top
    * and bottom x-borders between adjacent nodes. The graph contains n*m nodes
    * and n(m+1) - 2 edges. Here is an example with n = 3 and m = 4:<br />
    *
    * <pre>
    *    o---o---o
    *    |   |   |
    *    o   o   o
    *    |   |   |
    *    o   o   o
    *    |   |   |
    *    o---o---o
    * </pre>
    *
    * @param filename
    *           output file name
    * @param n
    *           number of nodes on x-sides
    * @param m
    *           number of nodes on y-sides
    * @throws java.io.IOException
    */
   public static void createStripeGraph(String filename, int n, int m)
   throws IOException
   {
      int numNodes = n * m;
      int numLinks = n * (m + 1) - 2;
      double rel = REL0; // reliability
      Writer output = null;
      int target = numNodes - 1; // target of graph
      File file = new File(filename);
      output = new BufferedWriter(new FileWriter(file));
      // source node is 0
      output.write(numNodes + "\t" + numLinks + "\t0\t" + target + "\n");

      for (int i = 0; i < n - 1; i++) {
         // the edges on the top x-border
         int s = i + 1;
         output.write(i + "\t" + s + "\t" + rel + "\n");
      }

      for (int i = 0; i < m - 1; i++) {
         // the vertical edges on all nodes
         for (int j = 0; j < n; j++) {
            int k = i * n + j; // node1 of this edge
            int s = k + n; // node2 of this edge
            output.write(k + "\t" + s + "\t" + rel + "\n");
         }
      }

      for (int i = numNodes - n; i < numNodes - 1; i++) {
         // the edges on the bottom x-border
         int s = i + 1;
         output.write(i + "\t" + s + "\t" + rel + "\n");
      }

      output.close();
   }

   /**
    * Creates a text file containing the specification of a complete graph with
    * a given number of nodes. A complete graph has an edge between every pair
    * of nodes. If the graph has n nodes, then it has n*(n-1)/2 edges.
    *
    * @param filename
    *           output file name
    * @param n
    *           number of nodes
    * @throws java.io.IOException
    */
   public static void createCompleteGraph(String filename, int n)
   throws IOException
   {
      int numNodes = n;
      int numLinks = n * (n - 1) / 2;
      Writer output = null;
      double rel = REL0;
      int target = numNodes - 1;
      File file = new File(filename);
      output = new BufferedWriter(new FileWriter(file));
      output.write(numNodes + "\t" + numLinks + "\t0\t" + target + "\n");

      for (int i = 0; i < n - 1; i++) {
         for (int j = i + 1; j < n; j++) {
            output.write(i + "\t" + j + "\t" + rel + "\n");
         }
      }

      output.close();
   }

   /**
    * Creates a text file containing the specification of a square lattice
    * graph. Each side of the graph has n nodes. The graph contains n*n nodes
    * and 2n(n-1) edges. Here is an example with n = 4:<br />
    *
    * <pre>
    *    o---o---o---o
    *    |   |   |   |
    *    o---o---o---o
    *    |   |   |   |
    *    o---o---o---o
    *    |   |   |   |
    *    o---o---o---o
    * </pre>
    *
    * @param filename
    *           output file name
    * @param n
    *           number of nodes on x-sides (or y-sides)
    * @throws java.io.IOException
    */
   public static void createSquareGraph(String filename, int n)
   throws IOException
   {
      int numNodes = n * n;
      int numLinks = 2 * n * (n - 1);
      Writer output = null;
      double rel = REL0;
      int curr = numNodes - 1; // target of graph
      File file = new File(filename);
      output = new BufferedWriter(new FileWriter(file));
      output.write(numNodes + "\t" + numLinks + "\t0\t" + curr + "\n");

      for (int i = 0; i < n - 1; i++) {
         for (int j = 0; j < n - 1; j++) {
            int k = i * n + j;
            int targetleft = k + 1;
            int targetdown = k + n;
            output.write(k + "\t" + targetleft + "\t" + rel + "\n");
            output.write(k + "\t" + targetdown + "\t" + rel + "\n");
         }
      }

      for (int i = 0; i < n - 1; i++) {
         int source = (i + 1) * n - 1;
         int target = source + n;
         output.write(source + "\t" + target + "\t" + rel + "\n");
      }

      for (int j = 0; j < n - 1; j++) {
         int source = (n - 1) * n + j;
         int target = source + 1;
         output.write(source + "\t" + target + "\t" + rel + "\n");
      }
      output.close();
   }

   /**
    * Creates a text file containing the specification of a rectangular lattice
    * graph. The x-sides of the graph have n nodes, and the y-sides have m
    * nodes. The graph has edges between each adjacent nodes. The graph contains
    * n*m nodes and n(m-1) + m(n-1) edges. Here is an example with n = 3 and m =
    * 4:<br />
    *
    * <pre>
    *    o---o---o
    *    |   |   |
    *    o---o---o
    *    |   |   |
    *    o---o---o
    *    |   |   |
    *    o---o---o
    * </pre>
    *
    * @param filename
    *           output file name
    * @param n
    *           number of nodes on x-sides
    * @param m
    *           number of nodes on y-sides
    * @throws java.io.IOException
    */
   public static void createLatticeGraph(String filename, int n, int m)
   throws IOException
   {
      int numNodes = n * m;
      int numLinks = n * (m - 1) + m * (n - 1);
      Writer output = null;
      double rel = REL0;
      int target = numNodes - 1; // target of graph
      File file = new File(filename);
      output = new BufferedWriter(new FileWriter(file));
      output.write(numNodes + "\t" + numLinks + "\t0\t" + target + "\n");

      for (int i = 0; i < m - 1; i++) {
         for (int j = 0; j < n - 1; j++) {
            int k = i * n + j;
            int targetleft = k + 1;
            int targetdown = k + n;
            output.write(k + "\t" + targetleft + "\t" + rel + "\n");
            output.write(k + "\t" + targetdown + "\t" + rel + "\n");
         }
      }

      for (int i = 0; i < m - 1; i++) {
         // node1 and node2 of edge
         int s = (i + 1) * n - 1;
         int t = s + n;
         output.write(s + "\t" + t + "\t" + rel + "\n");
      }

      for (int j = 0; j < n - 1; j++) {
         int s = (m - 1) * n + j;
         int t = s + 1;
         output.write(s + "\t" + t + "\t" + rel + "\n");
      }
      output.close();
   }

   /**
    * Creates a text file containing the specification of a linear series graph.
    * The graph has n nodes and (n-1) edges. Here is an example with n = 4:<br />
    *
    * <pre>
    * o-- - o-- - o-- - o
    * </pre>
    *
    * @param filename
    *           output file name
    * @param n
    *           number of nodes
    * @throws java.io.IOException
    */
   public static void createSeriesGraph(String filename, int n)
   throws IOException
   {
      createLatticeGraph(filename, n, 1);
   }


   /**
    * Creates a text file containing the specification of a parallel graph with
    * m parallel branches, each branch having n internal nodes and n+1 edges.
    * The graph has mn + 2 nodes, and m(n+1) edges. Source node is 0, sink node
    * is 1.
    *
    * @param filename
    *           output file name
    * @param m
    *           number of parallel branches
    * @param n
    *           number of internal nodes on a branch
    * @throws java.io.IOException
    */
   public static void createParallelGraph(String filename, int m, int n)
   throws IOException
   {
      int numNodes = 2 + n * m;
      int numLinks = m * (n + 1);
      Writer output = null;
      double rel = REL0;
      File file = new File(filename);
      output = new BufferedWriter(new FileWriter(file));
      // source is 0; target is 1
      output.write(numNodes + "\t" + numLinks + "\t0\t1\n");

      int inode = 2;
      for (int i = 0; i < m; i++) { // branch i
         output.write(0 + "\t" + inode + "\t" + rel + "\n");
         for (int j = 1; j < n; j++) { // internal nodes
            output.write(inode + "\t" + (inode + 1) + "\t" + rel + "\n");
            ++inode;
         }
         output.write(inode + "\t" + 1 + "\t" + rel + "\n");
         ++inode;
      }

      output.close();
   }

}
