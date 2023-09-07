package flooding;

public class Main {
    public static void main(String[] args) {
        Network network = createNetwork();

        // Simulate sending a message from a node
        Node sourceNode = network.nodes.get("A");
        sourceNode.sendMessage("Hola mundo");

        network.printNodeInfo();
    }

    private static Network createNetwork() {
        Network network = new Network();
    
        // Create nodes
        Node nodeA = new Node("A");
        Node nodeB = new Node("B");
        Node nodeC = new Node("C");
        Node nodeI = new Node("I");
        Node nodeF = new Node("F");
        Node nodeD = new Node("D");
        Node nodeE = new Node("E");
        Node nodeG = new Node("G");
        Node nodeH = new Node("H");
    
        // Add nodes to the network
        network.addNode(nodeA);
        network.addNode(nodeB);
        network.addNode(nodeC);
        network.addNode(nodeI);
        network.addNode(nodeF);
        network.addNode(nodeD);
        network.addNode(nodeE);
        network.addNode(nodeG);
        network.addNode(nodeH);
    
        // Define connections (edges) between nodes
        network.addConnection("A", "B", 7);
        network.addConnection("A", "C", 7);
        network.addConnection("A", "I", 1);
        network.addConnection("B", "F", 2);
        network.addConnection("C", "D", 5);
        network.addConnection("I", "D", 6);
        network.addConnection("D", "E", 1);
        network.addConnection("D", "F", 1);
        network.addConnection("E", "G", 4);
        network.addConnection("G", "F", 3);
        network.addConnection("F", "H", 4);
    
        return network;
    }
    
}
