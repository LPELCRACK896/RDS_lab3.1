package flooding;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String name;
    private final List<Edge> edges;
    private boolean visited;
    private int totalWeight;

    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
        this.visited = false;
        this.totalWeight = 0;
    }

    public String getName() {
        return name;
    }

    public void addNeighbor(Node neighbor, int weight) {
        edges.add(new Edge(neighbor, weight));
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int weight) {
        totalWeight = weight;
    }

    public boolean getVisited() {
        return visited;
    }
    
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void sendMessage(String message) {
        if (!visited) {
            System.out.println("Node " + name + " is sending message: " + message);

            // Simulate sending the message to all neighbors with weights
            for (Edge edge : edges) {
                Node neighbor = edge.getTarget();
                int edgeWeight = edge.getWeight();

                // Adjust the message propagation based on the edge weight
                int totalWeightToNeighbor = totalWeight + edgeWeight;
                neighbor.receiveMessage(message, totalWeightToNeighbor);
            }

            visited = true;
        }
    }

    public void receiveMessage(String message, int incomingWeight) {
        if (!visited || incomingWeight < totalWeight) {
            System.out.println("Node " + name + " received message: " + message);
            totalWeight = incomingWeight;
    
            visited = true; // Mark the node as visited here to prevent further propagation
    
            // Simulate sending the message to all neighbors
            for (Edge edge : edges) {
                Node neighbor = edge.getTarget();
                int edgeWeight = edge.getWeight();
                int totalWeightToNeighbor = totalWeight + edgeWeight;
                neighbor.receiveMessage(message, totalWeightToNeighbor);
            }
        }
    }
    
}
