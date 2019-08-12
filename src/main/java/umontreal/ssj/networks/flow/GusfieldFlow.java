package umontreal.ssj.networks.flow;

import umontreal.ssj.networks.LinkBasic;
import umontreal.ssj.networks.NodeBasic;
import java.util.*;

public class GusfieldFlow <N extends NodeBasic, L extends LinkBasic>{
	
	   	private final GraphFlow network;
	    /* Number of vertices in the graph */
	    private final int N;

	    /* Data structures for computations */
	    private List<NodeBasic> vertexList = new ArrayList<>();
	    private Map<NodeBasic, Integer> indexMap = new HashMap<>();
	    private int[] p; // See vector p in the paper description
	    private double[] fl; // See vector fl in the paper description

	    /* Matrix containing the flow values for every $s-t$ pair */
	    private double[][] flowMatrix = null;

	    private NodeBasic lastInvokedSource = null;
	    private NodeBasic lastInvokedTarget = null;
	    private Set<NodeBasic> sourcePartitionLastInvokedSource = null;

	    /**
	     * Constructs a new GusfieldEquivalentFlowTree instance.
	     * 
	     * @param network input graph
	     */
	    public GusfieldFlow(GraphFlow network)
	    {
	        this.network=network;
	        this.N=network.getNumNodes();
	    }


	    /**
	     * Runs the algorithm
	     */
	    private void calculateGomoryHuTree()
	    {
	        flowMatrix = new double[N][N];
	        p = new int[N];
	        fl = new double[N];

	        for (int s = 1; s < N; s++) {
	            int t = p[s];
	            double flowValue =
	                minimumSTCutAlgorithm.calculateMinCut(vertexList.get(s), vertexList.get(t));
	            Set<V> sourcePartition = minimumSTCutAlgorithm.getSourcePartition(); // Set X in the
	                                                                                 // paper
	            fl[s] = flowValue;

	            for (int i = 0; i < N; i++)
	                if (i != s && sourcePartition.contains(vertexList.get(i)) && p[i] == t)
	                    p[i] = s;
	            if (sourcePartition.contains(vertexList.get(p[t]))) {
	                p[s] = p[t];
	                p[t] = s;
	                fl[s] = fl[t];
	                fl[t] = flowValue;
	            }

	            // populate the flow matrix
	            flowMatrix[s][t] = flowMatrix[t][s] = flowValue;
	            for (int i = 0; i < s; i++)
	                if (i != t)
	                    flowMatrix[s][i] =
	                        flowMatrix[i][s] = Math.min(flowMatrix[s][t], flowMatrix[t][i]);
	        }
	    }

	    /**
	     * Returns the Gomory-Hu Tree as an actual tree (graph). Note that this tree is not necessarily
	     * unique. The edge weights represent the flow values/cut weights. This method runs in $O(n)$
	     * time.
	     * 
	     * @return Gomory-Hu Tree
	     */
	    public SimpleWeightedGraph<V, DefaultWeightedEdge> getGomoryHuTree()
	    {
	        if (p == null) // Lazy invocation of the algorithm
	            this.calculateGomoryHuTree();

	        // Compute the tree from scratch. Since we compute a new tree, the user is free to modify
	        // this tree.
	        SimpleWeightedGraph<V, DefaultWeightedEdge> gomoryHuTree =
	            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
	        Graphs.addAllVertices(gomoryHuTree, vertexList);
	        for (int i = 1; i < N; i++) {
	            Graphs.addEdge(gomoryHuTree, vertexList.get(i), vertexList.get(p[i]), fl[i]);
	        }

	        return gomoryHuTree;
	    }

	    /* ================== Maximum Flow ================== */

	    /**
	     * Unsupported operation
	     * 
	     * @param source source of the flow inside the network
	     * @param sink sink of the flow inside the network
	     *
	     * @return nothing
	     */
	    @Override
	    public MaximumFlow<E> getMaximumFlow(V source, V sink)
	    {
	        throw new UnsupportedOperationException(
	            "Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
	    }

	    /**
	     * Returns the Maximum flow between source and sink. The algorithm is only executed once;
	     * successive invocations of this method will return in $O(1)$ time.
	     * 
	     * @param source source vertex
	     * @param sink sink vertex
	     * @return the Maximum flow between source and sink.
	     */
	    @Override
	    public double getMaximumFlowValue(V source, V sink)
	    {
	        assert indexMap.containsKey(source) && indexMap.containsKey(sink);

	        lastInvokedSource = source;
	        lastInvokedTarget = sink;
	        sourcePartitionLastInvokedSource = null;
	        gomoryHuTree = null;

	        if (p == null) // Lazy invocation of the algorithm
	            this.calculateGomoryHuTree();
	        return flowMatrix[indexMap.get(source)][indexMap.get(sink)];
	    }

	    /**
	     * Unsupported operation
	     * 
	     * @return nothing
	     */
	    @Override
	    public Map<E, Double> getFlowMap()
	    {
	        throw new UnsupportedOperationException(
	            "Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
	    }

	    /**
	     * Unsupported operation
	     * 
	     * @param e edge
	     * @return nothing
	     */
	    @Override
	    public V getFlowDirection(E e)
	    {
	        throw new UnsupportedOperationException(
	            "Flows calculated via Gomory-Hu trees only provide a maximum flow value, not the exact flow per edge/arc.");
	    }

	    /* ================== Minimum Cut ================== */

	    @Override
	    public double calculateMinCut(V source, V sink)
	    {
	        return getMaximumFlowValue(source, sink);
	    }

	    /**
	     * Calculates the minimum cut in the graph, that is, the minimum cut over all $s-t$ pairs. The
	     * same result can be obtained with the {@link org.jgrapht.alg.StoerWagnerMinimumCut}
	     * implementation. After invoking this method, the source/sink partitions corresponding to the
	     * minimum cut can be queried through the {@link #getSourcePartition()} and
	     * {@link #getSinkPartition()} methods. After computing the Gomory-Hu Cut tree, this method runs
	     * in $O(N)$ time.
	     * 
	     * @return weight of the minimum cut in the graph
	     */
	    public double calculateMinCut()
	    {
	        if (this.gomoryHuTree == null)
	            this.gomoryHuTree = this.getGomoryHuTree();
	        DefaultWeightedEdge cheapestEdge = gomoryHuTree
	            .edgeSet().stream().min(Comparator.comparing(gomoryHuTree::getEdgeWeight))
	            .orElseThrow(() -> new RuntimeException("graph is empty?!"));
	        lastInvokedSource = gomoryHuTree.getEdgeSource(cheapestEdge);
	        lastInvokedTarget = gomoryHuTree.getEdgeTarget(cheapestEdge);
	        sourcePartitionLastInvokedSource = null;
	        return gomoryHuTree.getEdgeWeight(cheapestEdge);
	    }

	    @Override
	    public double getCutCapacity()
	    {
	        return calculateMinCut(lastInvokedSource, lastInvokedTarget);
	    }

	    @Override
	    public Set<V> getSourcePartition()
	    {
	        if (sourcePartitionLastInvokedSource != null)
	            return sourcePartitionLastInvokedSource;

	        if (this.gomoryHuTree == null)
	            this.gomoryHuTree = this.getGomoryHuTree();

	        Set<DefaultWeightedEdge> pathEdges =
	            this.findPathBetween(gomoryHuTree, lastInvokedSource, lastInvokedTarget);
	        DefaultWeightedEdge cheapestEdge =
	            pathEdges.stream().min(Comparator.comparing(gomoryHuTree::getEdgeWeight)).orElseThrow(
	                () -> new RuntimeException("path is empty?!"));

	        // Remove the selected edge from the gomoryHuTree graph. The resulting graph consists of 2
	        // components
	        V source = gomoryHuTree.getEdgeSource(cheapestEdge);
	        V target = gomoryHuTree.getEdgeTarget(cheapestEdge);
	        gomoryHuTree.removeEdge(cheapestEdge);

	        // Return the vertices in the component with the source vertex
	        sourcePartitionLastInvokedSource =
	            new ConnectivityInspector<>(gomoryHuTree).connectedSetOf(lastInvokedSource);

	        // Restore the internal tree structure by putting the edge back
	        gomoryHuTree.addEdge(source, target, cheapestEdge);

	        return sourcePartitionLastInvokedSource;
	    }

	    /**
	     * BFS method to find the edges in the shortest path from a source to a target vertex in a tree
	     * graph.
	     * 
	     * @param tree input graph
	     * @param source source
	     * @param target target
	     * @return edges constituting the shortest path between source and target
	     */
	    private Set<DefaultWeightedEdge> findPathBetween(
	        SimpleWeightedGraph<V, DefaultWeightedEdge> tree, V source, V target)
	    {
	        boolean[] visited = new boolean[vertexList.size()];
	        Map<V, V> predecessorMap = new HashMap<V, V>();
	        Queue<V> queue = new LinkedList<V>();
	        queue.add(source);

	        boolean found = false;
	        while (!found && !queue.isEmpty()) {
	            V next = queue.poll();
	            for (V v : Graphs.neighborListOf(tree, next)) {
	                if (!visited[indexMap.get(v)]) {
	                    predecessorMap.put(v, next);
	                    queue.add(v);
	                }
	                if (v == target) {
	                    found = true;
	                    break;
	                }
	            }
	            visited[indexMap.get(next)] = true;
	        }

	        Set<DefaultWeightedEdge> edges = new LinkedHashSet<>();
	        V v = target;
	        while (v != source) {
	            V pred = predecessorMap.get(v);
	            edges.add(tree.getEdge(v, pred));
	            v = pred;
	        }
	        return edges;
	    }

	    @Override
	    public Set<V> getSinkPartition()
	    {
	        Set<V> sinkPartition = new LinkedHashSet<>(network.vertexSet());
	        sinkPartition.removeAll(this.getSourcePartition());
	        return sinkPartition;
	    }

	    @Override
	    public Set<E> getCutEdges()
	    {
	        Set<E> cutEdges = new LinkedHashSet<>();
	        Set<V> sourcePartion = this.getSourcePartition();
	        for (E e : network.edgeSet()) {
	            V source = network.getEdgeSource(e);
	            V sink = network.getEdgeTarget(e);
	            if (sourcePartion.contains(source) ^ sourcePartion.contains(sink))
	                cutEdges.add(e);
	        }
	        return cutEdges;
	    }
	}
	
	
	
}
