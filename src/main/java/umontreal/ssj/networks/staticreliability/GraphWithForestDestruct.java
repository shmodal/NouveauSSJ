/**
 * 
 */
package umontreal.ssj.networks.staticreliability;

import java.util.*;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.LinkReliability;


/**
 * Implements forest for the destructive schema.
 * 
 * @see GraphWithForest
 */
public class GraphWithForestDestruct extends GraphWithForest implements Cloneable {

   /**
    * Constructor. Initializes the destructive schema for links.
    * 
    * @param father
    */
   public GraphWithForestDestruct(GraphReliability father) {
      super(father);
   }

   /**
    * Initializes the forest with the destructive schema for links, except for
    * the weights which are unchanged. Initially, all links are operational and
    * there is only 1 tree.
    */
   public void initForestDestructNotWeights() {
      // Initially, all links are operational and in the forest
      for (int i = 0; i < inForest.length; i++)
         inForest[i] = 1;
      setOperational(true);

      int id = 0;
      // Initially, all nodes are connected and in the same tree
      for (int i = 0; i < tree.length; i++)
         tree[i] = id;

      nextId = ++id;
      treeCount = 1;
   }

   /**
    * Initially, all links are operational. Then the links fail one by one in
    * "time" order until the network fails. A copy of the weights of the links,
    * sorted by increasing order, are returned in A. The original weights are
    * unchanged by the method.
    * 
    * @param A
    *           array of m elements, where m is the number of links in the graph
    * 
    * @return a 2-element array: the time at which the network fails, and the
    *         (sorted) rank of the link that made it so.
    */
   public double[] getFailTime(double[] A) {
      initForestDestructNotWeights();
      double[] W = getWeight();
      int m = getFather().getNumLinks(); // W.length
      System.arraycopy(W, 0, A, 0, m); // Copy the weights and sort the copy
      Arrays.sort(A);
      double[] res = new double[2];
      int s = -1;
      for (int j = 0; j < m; j++) {
         s = findLink(A[j]);
         setOperational(s, false);
         removeLink(getFather().getLink(s));
         if (!isConnected()) {
            res[0] = W[s];
            res[1] = j;
            return res;
         }
      }
      res[0] = -1.0e300;
      res[1] = -1;
      return res;
   }

   public double[] getFailTime() {
      int m = weight.length;
      double[] A = new double[m];
      return getFailTime(A);
   }

   /**
    * Updates forest for new level gamma. Inserts in forest the new links that
    * become operational at this level, that is, those for which W_j >=
    * realgamma or 1/W_j <= gamma. Don't forget that we keep gamma = 1/realgamma
    * 
    * @param gamma
    *           1/(realgamma level)
    */
   @Override
   public void update(double gamma) {
      int m = father.getNumLinks();
      for (int j = 0; j < m; j++) {
         if (1.0 / weight[j] <= gamma) { // notice 1/weight compared to Forest
            setOperational(j, true);
            if (0 == isInForest(j))
               insertLink(father.getLink(j));
         }
      }
   }

   /**
    * Removes all links connected to <i>node</i> if they are in a different
    * tree than V_0 since these links will not contribute to future failures.
    * This is used only for the <i>destructive</i> schema.
    * 
    * @param node
    *           node
    */
   public void removeUnconnectedLinks(int node) {
      // if (1 == treeCount)
      // return;
      int node0 = father.getV0()[0]; // first node of V_0
      final int id0 = tree[node0]; // tree containing V_0
      if (tree[node] == id0) // if in same tree, do nothing
         return;
      int numlinks = father.getNumberOfNeighbors(node);
      LinkReliability lien;
      int s; // link

      for (int j = 0; j < numlinks; ++j) {
         lien = father.getLinkFromNode(j, node);
         s = lien.getIndice();
         if (isOperational(s)) {
            setOperational(s, false);
          //  removeLink(lien);
         }
      }
   }

   /**
    * Removes all links which are in a different tree than V_0. This is used
    * only for the <i>destructive</i> schema since these links will not
    * contribute to future fail.
    */
   public void removeUnconnectedLinks() {
      // if (1 == treeCount)
      // return;
      int node = father.getV0()[0]; // first node of V_0
      final int id0 = tree[node]; // tree containing V_0
      final int m = father.getNumLinks();
      LinkReliability lien;
      int id;

      for (int j = 0; j < m; ++j) {
         if (!isOperational(j))
            continue;
         lien = father.getLink(j);
         node = lien.getSource();
         id = tree[node];
         if (id != id0) {
            setOperational(j, false);
            // removeLink(lien);
         }
      }
   }

   /**
    * Removes all links which are in a different tree than V_0. This is used only
    * for the <i>destructive</i> schema since these links will not contribute to
    * future fail.
    */
   public void removeUnconnectedLinks2() {
      // if (1 == treeCount)
      // return;
      int node = father.getV0()[0]; // first node of V_0
      final int id0 = tree[node]; // tree containing V_0
      final int n = father.getNumNodes();
      LinkReliability lien;
      int id; // tree id
      int numlinks; // num of links
      int s; // link

      for (int i = 0; i < n; ++i) {
         id = tree[i];
         if (id == id0)
            continue;
         numlinks = father.getNumberOfNeighbors(i);
         for (int j = 0; j < numlinks; ++j) {
            lien = father.getLinkFromNode(j, i);
            s = lien.getIndice();
            if (isOperational(s)) {
               setOperational(s, false);
               removeLink(lien);
            }
         }
      }
   }

   /**
    * Breadth-first-search: find all the nodes connected to v and fail all the
    * links in this component.
    * 
    * @param v
    *           node
    */
   public void removeComponent(int v) {
      int node = father.getV0()[0]; // first node of V_0
      final int id0 = tree[node]; // tree containing V_0
      final int id = tree[v]; // tree containing node v
      if (id == id0)
         return;
      
      LinkedList<Integer> queue = new LinkedList<Integer>(); // nodes
      Set<Integer> visited = new HashSet<Integer>(); // visited links
      queue.addLast(v);

      while (!queue.isEmpty()) {
         v = queue.pop();
         LinkReliability link;
         int j;
         for (int i = 0; i < father.getNumberOfNeighbors(v); i++) {
            // get link i connected to node v
            link = father.getLinkFromNode(i, v);
            j = link.getIndice();
            // get node u such that j = (v,u)
            int u = father.getNeighborOfNode(link, v);
            if (tree[u] != id || visited.contains(j))
               continue;
            setOperational(j, false);
            visited.add(j);
            queue.addLast(u);
         }
      }
   }

   @Override
   public GraphWithForestDestruct clone() {
      GraphWithForestDestruct image = (GraphWithForestDestruct) super.clone();
      return image;
   }

}
