
package umontreal.ssj.networks.staticreliability;

//import util.Tools;
import java.util.*;

import umontreal.ssj.networks.GraphReliability;
import umontreal.ssj.networks.LinkReliability;
import umontreal.ssj.networks.staticreliability.ShockList;
import umontreal.ssj.rng.*;
import umontreal.ssj.util.PrintfFormat;
import umontreal.ssj.util.Tools;

/**
 * Implements forest for the destructive schema with shocks.
 * 
 * @author Richard Simard
 * @see GraphWithForestDestruct
 * @since juillet 2013
 */
public class GraphWithForestShocks extends GraphWithForestDestruct implements Cloneable {
   protected ShockList shocks;
   protected int[] co; // shocks counter for links
   protected double[] shockWeight; // weight of each shock.
   protected int kappa; // number of shocks
   protected int[] shockOn;
   // shockOn: 1 if shock[s] has struck; 0 if no shock[s], -1 if undefined
   protected int[] treeAux; // temporary working copy of tree

   /**
    * Constructor. Initializes the destructive schema for links with shocks.
    * 
    * @param father
    */
   public GraphWithForestShocks(GraphReliability father, ShockList shocks) {
      super(father);
      this.shocks = shocks;
      kappa = shocks.getShocks().size(); // number of shocks
      shockWeight = new double[kappa];
      initShockWeights();
      shockOn = new int[kappa];
      setShockState(1);
      int n = tree.length; // number of nodes
      treeAux = new int[n];
      int m = father.getNumLinks(); // number of links
      co = new int[m];
      initShockCounters(shocks);
      shocks.setAntiConnect(false);
   }

   /**
    * Initializes the forest and shocks for the destructive schema, except for
    * the weights (or times) which are unaffected. Initially, all links are
    * operational, there is only 1 tree, and no shock has hit yet.
    */
   public void initForestShocksNotWeights() {
      initForestDestructNotWeights();
      initShockCounters();
      setShockState(-1);
   }

   /**
    * Inits all shock counters to 0.
    */
   public void initShockCounters() {
      for (int j = 0; j < co.length; j++)
         co[j] = 0;
   }

   /**
    * Inits all shock counters to those in chocs.
    * 
    * @param chocs
    *           shocks list
    */
   public void initShockCounters(ShockList chocs) {
      System.arraycopy(chocs.getCounts(), 0, co, 0, co.length);
   }

   /**
    * Initially, all links are operational. Then the shocks fail a group of
    * links simultaneously in "time" order until the network fails. The shocks
    * weights in A will be sorted by increasing order.
    * 
    * @param A
    *           array of kappa shock weights.
    * 
    * @return a 2-element array: the time at which the network fails, and the
    *         (sorted) rank of the shock that made it so.
    */
   @Override
   public double[] getFailTime(double[] A) {
      initForestShocksNotWeights();
      System.arraycopy(shockWeight, 0, A, 0, kappa); // Copy the weights
      Arrays.sort(A); // and sort the copy
      double[] res = new double[2];
      int s = -1;
      for (int j = 0; j < kappa; j++) {
         s = findShock(A[j]); // find the shock with the given weight
         failLinksOfShock(s); // fail all the links in that shock
         if (!isConnected()) {
            res[0] = shockWeight[s];
            res[1] = j;
            return res;
         }
      }
      res[0] = -1.0e300;
      res[1] = -1;
      return res;
   }

   @Override
   public double[] getFailTime() {
      double[] A = new double[kappa];
      return getFailTime(A);
   }

   /**
    * Update forest for new level gamma. Insert in the forest the new links that
    * become operational at this level, that is, those for which W_j >=
    * realgamma or 1/W_j <= gamma, and for which all the shocks have been
    * repaired. Don't forget that we keep gamma = 1/realgamma.
    * 
    * @param gamma
    *           1/(realgamma level)
    */
   @Override
   public void update(double gamma) {
      initWeights();
      updateLinksWeight(true);
      for (int s = 0; s < kappa; ++s) {
         if (1.0 / shockWeight[s] < gamma)
            repairLinksOfShock(s);
      }
   }

   /**
    * Fails all the links in shock s and remove them from forest.
    * 
    * @param s
    *           shock id
    */
   public void failLinksOfShock(int s) {
      Set<Integer> choc = shocks.getShock(s);
      if (1 == shockOn[s]) // shock is on already, do nothing
         return;
      shockOn[s] = 1;
      for (int i : choc) { // i = link
         if (isOperational(i)) {
            setOperational(i, false);
            removeLink(getFather().getLink(i));
         }
         ++co[i];
         // checkCounter(i);
      }
   }

   /**
    * Fails all the links in shock s and remove them from forest. Also examine
    * the link nodes; if one becomes disconnected from the main tree, remove
    * associated links disconnected from V_0 tree.
    * 
    * @param s
    *           shock id
    */
   public void failLinksNodesOfShock(int s) {
      Set<Integer> choc = shocks.getShock(s);
      if (1 == shockOn[s]) // shock is on already, do nothing
         return;
      shockOn[s] = 1;
      LinkReliability link;

      for (int i : choc) { // i = link
         if (isOperational(i)) {
            setOperational(i, false);
            link = getFather().getLink(i);
            removeLink(link);
            if (!isConnected(link.getSource(), link.getTarget())) {
               if (true) {
                  removeUnconnectedLinks(link.getSource());
                  removeUnconnectedLinks(link.getTarget());
               } else {
                  removeComponent(link.getSource());
                  removeComponent(link.getTarget());
               }
            }
         }
         ++co[i];
      }
   }

   /**
    * Update all links in shock s. If there are other shocks which failed this
    * link, it cannot become operational yet. Only when one link has been
    * repaired in all shocks in which it appears, can it become operational and
    * put in the forest.
    * 
    * @param s
    *           shock id
    */
   public void repairLinksOfShock(int s) {
      if (0 == shockOn[s])
         return;
      shockOn[s] = 0;
      Set<Integer> choc = shocks.getShock(s);
      for (int i : choc) { // examine counter for link i
         --co[i];
         if ((co[i] == 0) && (!isOperational(i))) {
            setOperational(i, true);
            if (0 == isInForest(i))
               insertLink(father.getLink(i));
         }
         // checkCounter(i);
      }
   }

   /**
    * Update all links in shock s. If there are other shocks which failed this
    * link, it cannot become operational yet. Only when one link has been
    * repaired in all shocks in which it appears, can it become operational and
    * put in the forest.
    * 
    * @param s
    *           shock id
    * @param linkSet0
    *           temporary links that can be discarded
    * @param failedLinks
    *           links that are still failed
    * @see iro.lec.algorithm.TurnipShocksAntiScan
    */
   public void repairLinksOfShock(int s, Set<Integer> linkSet0,
         Set<Integer> failedLinks) {
      if (0 == shockOn[s])
         return;
      shockOn[s] = 0;
      Set<Integer> choc = shocks.getShock(s);

      int t; // node id
      LinkReliability link;
      for (int i : choc) { // process link i
         --co[i];
         if ((co[i] == 0) && (!isOperational(i))) {
            setOperational(i, true);
            if (0 == isInForest(i)) {
               link = father.getLink(i);
               t = insertLink(link);
               failedLinks.remove(i);
               linkSet0.add(i);
               if (t >= 0) { // node-source of i
                  moveLinksInSets(t, linkSet0, failedLinks);
               }
            }
         }
      }
   }

   /**
    * Find all the nodes in the same tree as v; remove their dead links from
    * linkSet, and add them to linkSet0.
    * This method is SLOWER THAN moveLinksInSets.
    * @param v
    *           node
    * @param linkSet0
    *           temporary links that can be discarded
    * @param linkSet
    *           links that are still failed
    */

   private void moveLinksInSets2(int v, Set<Integer> linkSet0,
         Set<Integer> linkSet) {
      final int tv = tree[v];
      int s, t;
      LinkReliability link;
      Set<Integer> mylinkSet = new HashSet<Integer>(linkSet);

      for (int j : mylinkSet) {
         if (operational[j])
            continue;
         link = father.getLink(j);
         s = link.getSource();
         t = link.getTarget();
         if (tree[s] != tv || tree[t] != tv)
            continue;
         linkSet.remove(j);
         linkSet0.add(j);
      }
   }

   /**
    * Find all the nodes in the same tree as v; remove their dead links from
    * linkSet, and add them to linkSet0.
    * 
    * @param v
    *           node
    * @param linkSet0
    *           temporary links that can be discarded
    * @param linkSet
    *           links that are still failed
    */
   private void moveLinksInSets(int v, Set<Integer> linkSet0,
         Set<Integer> linkSet) {
      final int tv = tree[v];
      final int m = father.getNumLinks();
      int s, t;
      LinkReliability link;

      for (int j = 0; j < m; ++j) {
         if (operational[j])
            continue;
         link = father.getLink(j);
         s = link.getSource();
         t = link.getTarget();
         if (tree[s] != tv || tree[t] != tv)
            continue;
         if (linkSet.contains(j)) {
            linkSet.remove(j);
            linkSet0.add(j);
         }
      }
   }

   /**
    * Find all the nodes in the same tree as v; remove their dead links from
    * linkSet, and add them to linkSet0. 
    * This method is A LITTLE SLOWER THAN moveLinksInSets.
    * 
    * @param v
    *           node
    * @param linkSet0
    *           temporary links that can be discarded
    * @param linkSet
    *           links that are still failed
    */
   private void moveLinksInSets1(int v, Set<Integer> linkSet0,
         Set<Integer> linkSet) {
      final int tv = tree[v];
      final int n = father.getNumNodes();
      int u, s;
      LinkReliability link;

      for (int j = 0; j < n; ++j) {
         if (tree[j] != tv)
            continue;
         for (int i = 0; i < father.getNumberOfNeighbors(j); i++) {
            // get link i connected to node v
            link = father.getLinkFromNode(i, j);
            u = father.getNeighborOfNode(link, j);
            if (tree[u] != tv)
               continue;
            s = link.getIndice();
            if (!operational[s] && linkSet.contains(s)) {
               linkSet.remove(s);
               linkSet0.add(s);
            }
         }
      }
   }

   /**
    * Checks that shock counter for link i is within legal limits.
    * 
    * @param i
    */
   private void checkCounter(int i) {
      if (co[i] < 0)
         throw new IllegalArgumentException("co[" + i + "] < 0");
      if (co[i] > shocks.getCount(i))
         throw new IllegalArgumentException("co[" + i + "] > Max");
   }

   /**
    * Given the sorted weights W of all shocks, find the corresponding shock
    * numbers. Returns the shock numbers j such that shock at rank r in the
    * sorted weights has weight W[j].
    * 
    * @param W
    *           sorted weights of all shocks
    * @return shock indices corresponding to each weight
    */
   public int[] findShockRanks(double[] W) {
      int m = W.length;
      int[] rank = new int[m];
      for (int j = 0; j < m; j++) {
         double x = shockWeight[j];
         int r = Arrays.binarySearch(W, x);
         rank[r] = j;
      }
      return rank;
   }

   /**
    * Draw shocks time (or weight) from the exponential distribution. The rate
    * parameters (lambda) of the shocks are given.
    * 
    * @param stream
    *           random stream
    */
   public void sampleShockTimes(RandomStream stream) {
      double w;
      for (int j = 0; j < kappa; j++) {
         w = shocks.sample(stream, j, 0);
         setShockWeight(j, w);
      }
   }

   /**
    * Sample one shock time (or weight). Choose a = 0 to sample over the
    * complete range, and a = real_gamma to sample over a restricted range.
    * 
    * @param stream
    *           random stream
    * @param j
    *           shock number
    * @param a
    *           threshold
    */
   public void sampleShockTime(RandomStream stream, int j, double a) {
      // a is either 0, or threshold real gamma
      double time = shocks.sample(stream, j, a);
      setShockWeight(j, time);
   }

   /**
    * Updates all the links weight given the current shock weights. If
    * <tt>smallerFlag</tt> is <tt>true</tt>, updates the link weight only if the
    * shock weight is smaller; if <tt>smallerFlag</tt> is <tt>false</tt>,
    * unconditionally updates the link weight with the shock weight.
    * 
    * @param smallerFlag
    *           condition
    */
   public void updateLinksWeight(boolean smallerFlag) {
      for (int s = 0; s < kappa; ++s) {
         Set<Integer> choc = shocks.getShock(s);
         if (smallerFlag) {
            for (int i : choc) {
               if (shockWeight[s] < weight[i]) {
                  setWeight(i, shockWeight[s]);
               }
            }
         } else {
            for (int i : choc) {
               setWeight(i, shockWeight[s]);
            }
         }
      }
   }

   @Override
   public GraphWithForestShocks clone() {
      GraphWithForestShocks image = (GraphWithForestShocks) super.clone();
      image.shocks = shocks; // .clone();

      int n = treeAux.length;
      image.treeAux = Arrays.copyOf(treeAux, n);

      int m = co.length;
      image.co = Arrays.copyOf(co, m);

      image.kappa = kappa;
      image.shockWeight = Arrays.copyOf(shockWeight, kappa);

      image.shockOn = new int[kappa];
      image.shockOn = Arrays.copyOf(shockOn, kappa);

      return image;
   }

   /**
    * Tests whether the network would become operational if we added all the
    * links in <tt>shock</tt>, i.e. whether all the nodes in V_0 would become
    * connected. Returns <tt>true</tt> if it becomes connected, <tt>false</tt>
    * otherwise. <b>Must be called on a clone of forest because the forest is
    * modified</b>.
    * 
    * @param shock
    *           set of links in a shock
    * @return true if network becomes operational
    */
   public boolean wouldBeConnected1(Set<Integer> shock) {
      // Must be called on a clone of forest; otherwise, the temporary
      // links would have to be removed after checking connectedness.
      for (int j : shock) {
         if (co[j] > 1)
            continue;
         LinkReliability link = father.getLink(j);
         // Get the 2 nodes of link j
         int a = link.getSource();
         int b = link.getTarget();
         if (isConnected(a, b)) {
            // The 2 nodes of link j belong to the same tree
            continue;
         }

         if (0 == isInForest(j)) {
            setOperational(j, true);
            insertLink(link);
         }
      }

      if (isConnected())
         return true;
      else
         return false;
   }

   /**
    * Tests whether the network would become operational if we added all the
    * links in <tt>shock</tt>, i.e. whether all the nodes in V_0 would become
    * connected. Returns <tt>true</tt> if it becomes connected, <tt>false</tt>
    * otherwise.
    * 
    * @param shock
    *           set of links
    * @return true if network becomes operational, false otherwise.
    */
   public boolean wouldBeConnected2(Set<Integer> shock) {
      int n = tree.length; // number of nodes
      System.arraycopy(tree, 0, treeAux, 0, n);
      int s, t; // nodes of link i
      LinkReliability link;

      for (int i : shock) {
         if (co[i] > 1)
            continue;
         link = father.getLink(i);
         s = treeAux[link.getSource()];
         t = treeAux[link.getTarget()];
         if (s == t) // same tree
            continue;
         // if link connect 2 nodes, merge 2 trees
         for (int j = 0; j < n; j++) {
            if (t == treeAux[j])
               treeAux[j] = s;
         }
      }

      int[] V = father.getV0();
      s = treeAux[V[0]]; // tree index of first node in V0
      // Checks if all other nodes in V are connected (same tree).
      for (int i = 1; i < V.length; i++) {
         if (s != treeAux[V[i]])
            return false;
      }

      return true;
   }

   private void merge2() {

   }

   /**
    * Tests whether the network would become operational if we added all the
    * links in <tt>shock</tt>, i.e. whether all the nodes in V_0 would become
    * connected. Returns <tt>true</tt> if it becomes connected, <tt>false</tt>
    * otherwise.
    * 
    * @param shock
    *           set of links
    * @return true if network becomes operational
    */
   public boolean wouldBeConnected3(Set<Integer> shock) {
      int numNodes = father.getNumNodes();
      int numTrees = getTreeCount();
      int pos, pos2;

      System.out
            .println("\n ****** LA MÉTHODE ForestShocks.wouldBeConnected3 NE FONCTIONNE"
                  + " PAS CORRECTEMENT.\n ******* À CORRIGER");
      System.exit(1);

      // build a set with all tree ids
      treeAux[0] = tree[0];
      int el = 1;
      int j = 1;
      while (j < numNodes && el < numTrees) {
         int s = tree[j];
         pos = search(treeAux, el, s);
         if (pos < 0) {
            // it is not in the set, add it
            treeAux[el] = s;
            ++el;
         }
         if (el >= numTrees)
            break; // got them all in the set
         ++j;
      }

      Arrays.sort(treeAux, 0, numTrees);

      int[] treeMerge = new int[numTrees];
      System.arraycopy(treeAux, 0, treeMerge, 0, numTrees);
      int s, t; // nodes of link i
      LinkReliability link;
      for (int i : shock) {
         if (co[i] > 1)
            continue;
         link = father.getLink(i);
         s = tree[link.getSource()];
         t = tree[link.getTarget()];
         if (s == t) // same tree
            continue;

         // if link connect 2 nodes, merge 2 trees
         pos = search(treeAux, numTrees, s);
         pos2 = search(treeAux, numTrees, t);
         if (pos > pos2) { // swap to have pos < pos2
            int tem = pos;
            pos = pos2;
            pos2 = tem;
         }

         treeMerge[pos2] = treeMerge[pos];
      }

      remerge(treeAux, treeMerge);

      int[] V = father.getV0();
      s = tree[V[0]]; // tree id of first node in V0
      // Checks if all other nodes in V are connected (same tree).
      pos = search(treeAux, numTrees, s);
      int id = treeMerge[pos];
      for (int i = 1; i < V.length; i++) {
         s = tree[V[i]];
         pos = search(treeAux, numTrees, s);
         if (id != treeMerge[pos])
            return false;
      }

      return true;
   }

   /**
    * Searches position of tree s in the first n elements of treeA. Returns the
    * index of s in treeA if found. Returns -1 if not found.
    * 
    * @param treeAux
    * @param n
    *           number of elements of treeA
    * @param s
    * @return position in array
    */
   private int search(int[] treeA, int n, int s) {
      for (int j = 0; j < n; j++) {
         if (s == treeA[j])
            return j;
      }
      return -1;
   }

   /**
    * Make sure that treeMerge contains the same tree for connected nodes.
    * 
    * @param treeA
    * @param treeMerge
    */
   private void remerge(int[] treeA, int[] treeMerge) {
      int n = treeMerge.length;
      int i, s;
      for (int j = 0; j < n; j++) {
         i = j;
         // Follow the chains of tree pointers until it is the smallest id;
         // then all the connected nodes will be in the same tree
         while (treeMerge[i] != treeA[i]) {
            s = treeMerge[i];
            i = search(treeA, n, s);
            treeMerge[j] = treeMerge[i];
         }
      }
   }

   protected void initShockWeights() {
      for (int i = 0; i < kappa; i++)
         shockWeight[i] = LEN0;
   }

   /**
    * Find the first shock which has weight x.
    * 
    * @param x
    *           weight
    * @return shock number with this weight
    */
   protected int findShock(double x) {
      for (int j = 0; j < kappa; j++) {
         if (x == shockWeight[j])
            return j;
      }
      return -1;
   }

   /**
    * Sets the weight of shock j to y
    * 
    * @param j
    *           shock number
    * @param y
    *           shock weight
    */
   public void setShockWeight(int j, double y) {
      shockWeight[j] = y;
   }

   /**
    * 
    * @param j
    * @return the weight of shock j
    */
   public double getShockWeight(int j) {
      return shockWeight[j];
   }

   /**
    * @return the weight of all shocks
    */
   public double[] getShockWeight() {
      return shockWeight;
   }

   /**
    * If flag = 1, all shocks are set on. If flag = 0, all shocks are set off.
    * If flag = -1, the state of all shocks is undefined.
    */
   public void setShockState(int flag) {
      for (int i = 0; i < shockOn.length; i++)
         shockOn[i] = flag;
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer(super.toString());
      sb.append(Tools.toString("co", co));
      sb.append(PrintfFormat.NEWLINE);
      sb.append(Tools.toString("shockWeight", shockWeight));
      sb.append(PrintfFormat.NEWLINE);
      sb.append(Tools.toString("shockOn", shockOn));
      sb.append(PrintfFormat.NEWLINE);
      return sb.toString();
   }

}
