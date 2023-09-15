package xmpp_network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;
/**
 * @author LPELCRACK896
 */
public class MatrixTopology {
    public long[][] matrix;
    public HashMap<String, Integer> nodeToIndex;
    public HashMap<Integer, String> indexToNode;

    /**
     * Constructor.
     * @param nodes network members.
     */
    public MatrixTopology(ArrayList<String> nodes) {
        int size = nodes.size();
        matrix = new long[size][size];
        nodeToIndex = new HashMap<>();
        indexToNode = new HashMap<>();

        for (int i = 0; i < size; i++) {
            Arrays.fill(matrix[i], Long.MAX_VALUE);
            matrix[i][i] = 0;
            nodeToIndex.put(nodes.get(i), i);
            indexToNode.put(i, nodes.get(i));
        }
    }
    /*
     * #################
     * #################
     * MAIN METHODS
     * #################
     * #################
     */
    /**
     * For a certain node add connections to some others with its costs into an adjacency matrix.
     * @param node Node that has the connections.
     * @param connections hashmap with the connections, alias as key and cost as value.
     */
    public void establishConnections(String node, HashMap<String, Long> connections) {
        int fromIndex = nodeToIndex.get(node);

        for (String targetNode : connections.keySet()) {
            int toIndex = nodeToIndex.get(targetNode);
            matrix[fromIndex][toIndex] = connections.get(targetNode);
        }
    }

    /**
     * Gets for a certain node the best paths (according to dijkstra) to the rest of the nodes.
     * @param node node where all paths start.
     * @return Hashmap containing target node alias as key and the list of the path as value.
     */
    public HashMap<String, List<String>> dijkstra(String node) {
        int sourceIndex = nodeToIndex.get(node);
        long[] distances = new long[matrix.length];
        int[] previous = new int[matrix.length];
        Arrays.fill(distances, Long.MAX_VALUE);
        Arrays.fill(previous, -1);
        distances[sourceIndex] = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingLong(i -> distances[i]));
        queue.add(sourceIndex);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (int i = 0; i < matrix.length; i++) {
                if (distances[current] == Long.MAX_VALUE || matrix[current][i] == Long.MAX_VALUE) {
                    continue;  // Skip this edge if it leads to an overflow or if there's no direct connection
                }
                long newDist = distances[current] + matrix[current][i];
                if (newDist < distances[i]) {
                    queue.remove(i);
                    distances[i] = newDist;
                    previous[i] = current;
                    queue.add(i);
                }
            }
        }

        HashMap<String, List<String>> result = new HashMap<>();
        for (int i = 0; i < matrix.length; i++) {
            if (i != sourceIndex) {
                List<String> path = new ArrayList<>();
                for (int at = i; at != -1; at = previous[at]) {
                    path.add(0, indexToNode.get(at));
                }
                if (!path.isEmpty()) {
                    result.put(indexToNode.get(i), path);
                }
            }
        }

        return result;
    }
}
