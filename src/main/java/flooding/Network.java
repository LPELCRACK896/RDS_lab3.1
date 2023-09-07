package flooding;

import java.util.HashMap;
import java.util.Map;

public class Network {
    final Map<String, Node> nodes;

    public Network() {
        this.nodes = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.put(node.getName(), node);
    }

    public void addConnection(String nodeName1, String nodeName2, int weight) {
        Node node1 = nodes.get(nodeName1);
        Node node2 = nodes.get(nodeName2);
        if (node1 != null && node2 != null) {
            node1.addNeighbor(node2, weight);
            node2.addNeighbor(node1, weight);
        }
    }
    

    public void printNodeInfo() {
        for (Node node : nodes.values()) {
            System.out.println("Nodo: " + node.getName());
            System.out.println("Visited: " + node.getVisited());
            System.out.println("Vecinos:");
            for (Edge edge : node.getEdges()) {
                Node neighbor = edge.getTarget();
                int weight = edge.getWeight();
                System.out.println("    " + neighbor.getName() + ", con peso: " + weight);
            }
            System.out.println();
        }
    }
    
}
