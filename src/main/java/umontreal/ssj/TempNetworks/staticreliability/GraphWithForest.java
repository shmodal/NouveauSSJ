package umontreal.ssj.TempNetworks.staticreliability;

import umontreal.ssj.TempNetworks.GraphReliability;
import umontreal.ssj.TempNetworks.LinkReliability;
//import umontreal.ssj.networks.old.GraphOld;
//import umontreal.ssj.networks.old.LinkOld;
import umontreal.ssj.util.PrintfFormat;
import java.util.LinkedList;
import java.util.Arrays;

/**
 * This class implements forest and trees. It maintains structures to keep in memory which links
 * are operational and which nodes are connected to each other.
 * Component labels for the vertices (nodes) and edge (or link) labels for the edges in a current forest are kept.
 * All nodes in the same tree have the same id, which is an arbitrary int.
 * Operational links have operational[j] = true, but they may or may not be
 * in a component tree. Links in a tree have inForest[j] = true. All links in
 * the forest are operational.
 */
public class GraphWithForest implements Cloneable {

	   /**
	    * initial weight of links
	    */
   protected static final double LEN0 = 1.0e200; // initial weight of links
   /**
    * initial id of trees
    */
   private static final int ID0 = -1; // initial id of trees
   protected GraphReliability father;
   /**
    * number of trees in forest
    */
   protected int treeCount; // number of trees in forest
   /**
    * next tree id
    */
   protected int nextId; // next tree id

   /**
    * If link j is operational, then operational[j] = true; otherwise false.
    * These flags were added for efficiency: this way, we do not
    * have to get the link weight and compare it with a new gamma each time.
    */
   protected boolean[] operational;

   /**
    * links in the forest are marked with 1 (2 is used also temporarily); 0
    * otherwise.
    */
   protected int[] inForest;

   /**
    * For each node, indicates which tree contains it. tree id of the node.
    */
   protected int[] tree;

   /** A FINIR
    * Weight of each link of the graph. This is another field for the weight or
    * length of a link; this repeat is surely more efficient, but is it
    * dangerous; can there be conflict between this weight and the one (weight)
    * in class Link? To check more carefully.
    */
   protected double[] weight; // les Y_j de l'article, temps de réparation de chaque arete

   /** Constructs a new forest.
    * Inits the forest, with each node being in its own tree (works 
    * with algorithms using constructive scheme).
    */
   
   public GraphWithForest(GraphReliability father) {
      this.father = father;
      int numberOfNodes = father.getNumNodes();
      int numberOfLinks = father.getNumLinks();
      treeCount = 0;
      nextId = 0;

      operational = new boolean[numberOfLinks];
      setOperational(false);

      inForest = new int[numberOfLinks];
      weight = new double[numberOfLinks];
      tree = new int[numberOfNodes];
      initForest();
   }

   /**
    * Inits trees id to ID0.
    */
   public void initTrees() {
      // Init trees id to ID0
      for (int i = 0; i < tree.length; i++) {
         tree[i] = ID0;
      }
   }
   
   /**
    * Inits links : initially, none of them are in forest.
    */

   public void initLinks() {
      // Initially, none of the links are in forest
      for (int i = 0; i < inForest.length; i++)
         inForest[i] = 0;
   }
   
   /**
    * Inits weights to LENO.
    */
   
   public void initWeights() {
      for (int i = 0; i < weight.length; i++)
         weight[i] = LEN0;
   }

   /**
    * Initializes the weight of each link to x.
    * @param x
    */
   public void initWeights(double x) {
      for (int i = 0; i < weight.length; i++)
         weight[i] = x;
   }

   /*
    * void dc_simple::init_components () { // set node and edge values in to
    * initial values myTree.init (*the_graph, -1); in_forest.init (*the_graph,
    * false); int count = 0; node v; forall_nodes (v, *the_graph) { if
    * (myTree[v] == -1) { my_bfs (v, count); count++; } } cmp_counter = count;
    * next_number = count +1; }
    */
   
   /**
    * Initializes the forest and the weights(standard initialization).
    * This uses the constructive schema for links. Initially, each node is a tree identified 
    * by id and there are no links in the forest.
    */
   
   public void initForest() {
      initForestNotWeights();
      initWeights();
   }

   /**
    * Initializes the forest as in <tt>initForest()</tt> except for the weights
    * which are unchanged. This uses the constructive schema for links.
    * Initially, each node is a tree identified by id and there are no links in the forest.
    */
   public void initForestNotWeights() {
      initTrees();
      initLinks();
      setOperational(false);
      int id = 0;

      // Initially, each node is a tree identified by id
      // and there are no links in the forest
      for (int v = 0; v < tree.length; v++) {
         // tree id was initialized to ID0
         if (tree[v] == ID0) {
            // BFS(v, id);
            tree[v] = v;
            id++;
         }
      }
      treeCount = id;
      nextId = id;
   }

   /**
    * Returns the graph of the forest.
    */
   
   public GraphReliability getFather() {
      return father;
   }

   /**
    * Returns the number of trees in the forest.
    */
   
   public int getTreeCount() {
      return treeCount;
   }

   // public int[] getMyTree() {
   // return myTree;
   // }

   /**
    * Returns the tree to which node i belongs.
    */
   public int getTree(int i) {
      return tree[i];
   }

   /**
    * Returns the weight of link i.
    * @param i
    * @return the weight of link i
    */
   public double getWeight(int i) {
      return weight[i];
   }

   /**
    *  Returns the weight of all links.
    * @return weight
    */
   public double[] getWeight() {
      return weight;
   }

   // public int[] isInForest() {
   // return inForest;
   // }

   /**
    *  Checks if the link is in the forest.
    */
   
   public int isInForest(int i) {
      return inForest[i];
   }

   /**
    * Checks if a path from node u to node v exists in the current forest, and
    * thus the 2 nodes are connected.
    * 
    * @param u
    *           first node
    * @param v
    *           second node
    * @return true if the 2 nodes are connected, false otherwise.
    */
   public boolean isConnected(int u, int v) {
      return (tree[u] == tree[v]);
   }

   /**
    * Checks if source and target nodes are connected and thus network is
    * operational
    * 
    * @return true if network is operational
    */
   /*
    * public boolean isConnected0() { int s = father.getSource(); int t =
    * father.getTarget(); return isConnected(s, t); }
    */

   /**
    * Checks if network is operational. This is true if all the nodes in the
    * subset V0 are connected.
    * 
    * @return <tt>true</tt> if network is operational, <tt>false</tt> otherwise
    */
   public boolean isConnected() {
      int[] V = father.getV0();
      int s = tree[V[0]]; // tree index of first node in V
      // Checks if all other nodes in V are connected (same tree).
      for (int i = 1; i < V.length; i++) {
         if (s != tree[V[i]])
            return false;
      }

      return true;
   }

   /**
    * Check if all nodes are connected (are in the same tree).
    * 
    * @return <b>true</b> if all nodes are connected, <b>false</b> otherwise.
    */
   public boolean isAllConnected() {
      return (1 == treeCount);
   }

   /**
    * Checks if link i is operational
    * @param i
    *           link
    * @return true if link i is operational
    */
   public boolean isOperational(int i) {
      return operational[i];
   }

   /**
    * If flag = true, link i becomes operational; otherwise not.
    * 
    * @param i
    * @param flag
    */
   public void setOperational(int i, boolean flag) {
      operational[i] = flag;
   }

   /**
    * If flag = true, all links become operational; otherwise not.
    */
   public void setOperational(boolean flag) {
      for (int i = 0; i < operational.length; i++)
         operational[i] = flag;
   }

   /**
    * Sets weight of link i to y.
    * 
    * @param i
    * @param y
    */
   public void setWeight(int i, double y) {
      weight[i] = y;
   }

   /*
     void dc_simple::insert_edge (edge e, double weight) { in_forest[e] = 0;
     node v = source (e); node w = target (e); if (!query (v, w)) {
     in_forest[e] = 1; my_bfs (v, next_number++); bad_counter++; cmp_counter--;
     
     if (next_number == MAXINT) init_components (); // avoid errors or
     inconsistencies due to an overflow of next_number
      } }
    */
   /*
     public void insertLink(Link link) { // This method is slower than the next
     insertLink(Link link) int j = link.getIndice(); // insert link j in forest
     if operational if (!operational[j]) return ; inForest[j] = 0; int v =
     link.getSource(); int w = link.getTarget();
     
     if (!isConnected(v, w)) { // if the 2 vertices of j were in different
     trees, // join the 2 trees inForest[j] = 1; BFS(v, nextId++); treeCount--;
     // one tree less
     } }
    */

   /**
    * Insert link in forest. If two trees are linked by this insertion, joins them, and 
    * returns the node-source id of this link; otherwise, returns -1.
    * 
    * @param link link
    * @return node-source id
    */
   public int insertLink(LinkReliability link) {
      int j = link.getIndice();
      if (!operational[j])
         return -1;
      // inForest[j] = 0;
      int s = tree[link.getSource()];
      int t = tree[link.getTarget()];

      if (s != t) {
         // if the 2 vertices of link are in different trees,
         // join the 2 trees
         inForest[j] = 1;
         for (int i = 0; i < tree.length; i++) {
            if (tree[i] == s)
               tree[i] = t;
         }
         treeCount--; // one tree less
         return link.getSource();
      }
      return -1;
   }


   
   /**
    * Removes link if it is in the forest. It then rebuilds the new forest, by doing a 
    * BFS on the source node, and adds a new tree if necessary. 
    * 
    * @param link
    */
   
   public void removeLink(LinkReliability link) {
      int j = link.getIndice();
      if (inForest[j] == 1) {
         // remove link j from forest and rebuild new forest
         inForest[j] = 0;
         BFS(link.getSource(), nextId++);
         // if the 2 vertices of j are not in same tree
         if (!isConnected(link.getSource(), link.getTarget()))
            treeCount++; // one tree more
      }
   }

   /**
    * Updates forest for new level gamma; inserts in forest the new links that
    * become operational at this level.
    * 
    * @param gamma
    *           level gamma
    */
   public void update(double gamma) {
      int m = father.getNumLinks();
      for (int j = 0; j < m; j++) {
         if (weight[j] <= gamma) {
            setOperational(j, true);
            if (0 == isInForest(j))
               insertLink(father.getLink(j));
         } 
         
         /* Destructive part, if you want to remove links instead
            * else {
            *  setOperational(j, false);
            *  if (1 == isInForest(j))
            *  removeLink(father.getLink(j)); 
            * }
            */
      }
   }

   
   /**
    * Breadth-first-search: find all the nodes connected to v. They will make
    * the new tree with identifier id.
    * @param v node
    * @param id tree to which v belong
    */
   public void BFS(int v, int id) {
      LinkedList<Integer> Queue = new LinkedList<Integer>();
      Queue.addLast(v);
      tree[v] = id;

      while (!Queue.isEmpty()) {
         v = Queue.pop();
         LinkReliability link;
         int j;
         for (int i = 0; i < father.getNumberOfNeighbors(v); i++) {
            // get link i connected to node v
            link = father.getLinkFromNode(i, v);
            j = link.getIndice();
            if (operational[j]) {
               // get node u such that i = (u,v)
               int u = father.getNeighborOfNode(link, v);

               if (tree[u] != id) {
                  tree[u] = id;
                  // add in tree
                  inForest[j] = 2;
                  Queue.addLast(u);
               } else {
                  if (inForest[j] == 2) {
                     // have already visited this link
                     inForest[j] = 1;
                  } else {
                     // exclude this link
                     inForest[j] = 0;
                  }
               }
            }
         }
      }
   }

   
   /*
    * Implements Depth-First Search Algorithm
   <ol>
   <li> Stack the root node. </li>
   <li> Pop a node and examine it.
    * If the element sought is found in this node, stop the search and return
    * <tt>true</tt>. Otherwise stack any successors (the direct child nodes) that
    * have not yet been discovered.</li>
   <li>If the stack is empty, every node on the graph has been examined; 
    * stop the search and return <tt>false</tt>.</li>
   <li>Repeat from Step 2.</li>
   </ol>

    * @param g
    * @param connectedNodes
    * @param isLinkAvailable
    * @return
    */
   /*
   public boolean DFS(MiniGraph g, Integer source, Integer target, boolean[] isLinkAvailable)
   {
      Graph father = g.getFather();
      LinkedList<Integer> Queue = new LinkedList<Integer>();
      boolean[] visited = new boolean[father.getNumNodes()];

      for (int i = 0; i < father.getNumNodes(); i++) {
         visited[i] = false;
      }

      //1
      Queue.addFirst(source);

      //2
      while (!Queue.isEmpty()) {

         int n = Queue.pop();
         if (n == target) 
            return true;

            //System.out.print(n+" ");
            if (!g.getPermanentLabel(n)) {
               //while current has got neighbors unvisited
               for (int i = 0; i < father.getNumberOfNeighbors(n); i++) {
                  // get the i-eth link from the node n
                  Link ll = father.getLinkFromNode(i, n);
                  if (isLinkAvailable[ll.getIndice()]) {
                     // get the number of the neighbor
                     int neighbor = father.getNeighborOfNodeN(ll, n);
                     if (visited[neighbor]) {
                        continue;
                     } 
                        if (!Queue.contains(neighbor)) {
                           Queue.push(neighbor);
                        }
                  }
               }
               visited[n] = true;
            }
      }
      return false;
   }
*/
   
   
   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(
            "--------------------------------- Forest" + PrintfFormat.NEWLINE);
      sb.append("treeCount = " + treeCount + PrintfFormat.NEWLINE);
      sb.append(PrintfFormat.NEWLINE);
      int i;
      for (i = 0; i < weight.length; i++)
         sb.append("weight[" + i + "] = " + weight[i] + PrintfFormat.NEWLINE);
      sb.append(PrintfFormat.NEWLINE);
      for (i = 0; i < operational.length; i++)
         sb.append("operational[" + i + "] = " + operational[i]
               + PrintfFormat.NEWLINE);
      sb.append(PrintfFormat.NEWLINE);
      for (i = 0; i < inForest.length; i++) {
         LinkReliability link = father.getLink(i);
         int r = link.getSource();
         int s = link.getTarget();
         sb.append("inForest[" + i + "] = " + inForest[i] + "   (" + r + ", "
               + s + ")" + PrintfFormat.NEWLINE);
      }
      sb.append(PrintfFormat.NEWLINE);
      for (i = 0; i < tree.length; i++)
         sb.append("myTree[" + i + "] = " + tree[i] + PrintfFormat.NEWLINE);
      sb.append(PrintfFormat.NEWLINE);
      return sb.toString();
   }

   @Override
   public GraphWithForest clone() {
      GraphWithForest image = null;

      try {
         image = (GraphWithForest) super.clone();
      } catch (CloneNotSupportedException e) {
      }

      image.father = father; // don't clone father, reference only
      image.treeCount = treeCount;
      image.nextId = nextId;

      int n = tree.length; // num of nodes
      image.tree = new int[n];
      image.tree = Arrays.copyOf(tree, n);

      int m = inForest.length; // number of links
      image.inForest = new int[m];
      image.inForest = Arrays.copyOf(inForest, m);

      image.operational = new boolean[m];
      image.operational = Arrays.copyOf(operational, m);

      image.weight = new double[weight.length];
      image.weight = Arrays.copyOf(weight, weight.length);
      
      return image;
   }

   /**
    * Similar to <tt>getRepairTime()</tt>, except that the weights of the
    * links, sorted by increasing order, are returned in A.
    * 
    * @param A
    *           array of m elements, where m is the number of links in the graph
    * 
    * @return a 2-element array: the time at which the network becomes
    *         operational, and the (sorted) rank of the link that made it so.
    */
   public double[] getRepairTime(double[] A) {
      initForestNotWeights();
      double[] W = getWeight();
      int m = father.getNumLinks(); // W.length
      System.arraycopy(W, 0, A, 0, m);
      Arrays.sort(A);
      double[] res = new double[2];
      int s = -1;
      for (int j = 0; j < m; j++) {
         s = findLink(A[j]);
         setOperational(s, true);
         insertLink(father.getLink(s));
         if (isConnected()) {
            res[0] = W[s];
            res[1] = j;
            return res;
         }
      }
      res[0] = -1.0e300;
      res[1] = -1;
      return res;
   }

   /**
    * Given a graph with a weight (which may be considered as time of repair) on
    * each link, compute the time at which the network (represented by the
    * graph) first becomes operational. 
    * Returns a 2-element array: element 0
    * contains the time (weight) at which the network becomes operational,
    * element 1 contains the rank of the critical link, the one whose addition
    * made the network first operational. 
    * For example, when the links are sorted
    * according to weights, and the network becomes operational by adding the
    * 10-th link (counting from 0), then the rank is 9. But this is not link
    * numbered 9 in the graph, but link number 9 in sorted order of weights.
    * 
    * @return a 2-element array: the time at which network becomes
    *         operational, and the rank of the link that made it so.
    */
   public double[] getRepairTime() {
      int m = weight.length;
      double[] A = new double[m];
      return getRepairTime(A);
   }

   /**
    * Find the first link of the network which has the weight x.
    * 
    * @param x
    *           weight
    * @return number of link with this weight (-1 if no link)
    */
   public int findLink(double x) {
      int m = weight.length;
      for (int j = 0; j < m; j++) {
         if (x == getWeight(j))
            return j;
      }
      return -1;
   }

   /**
    * Given the weights W of all links of the graph, find the corresponding link
    * numbers. Returns the link numbers such that link j has weight W[j].
    * 
    * @param W
    *           weights of all the links
    * @return index of link corresponding to each weight
    */
   /*
    * public int[] findLink7 (double[] W) { int m = father.getNumLinks(); int[]
    * link = new int[m]; for (int j = 0; j < m; j++) { double x = W[j]; for (int
    * i = 0; i < m; i++) { if (x == weight[i]) { link[j] = i; break; } } }
    * return link; }
    */

   /**
    * Given the sorted weights W of all links of the graph, find the
    * corresponding link numbers. Returns the link numbers j such that link at
    * rank r in the sorted weights has weight W[j].
    * 
    * @param W
    *           weights of all the links
    * @return index of link corresponding to each weight
    */
   public int[] findLinkIndices(double[] W) {
      int m = W.length;
      int[] link = new int[m];
      for (int j = 0; j < m; j++) {
         double x = weight[j];
         int r = Arrays.binarySearch(W, x);
         link[r] = j;
      }
      return link;
   }
           
}
