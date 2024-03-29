package network;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Network network = new Network(10);
        ArrayList<Node> nodes = network.getNodes();

        network.flooding();

        int updates = 1;
        while (updates!=0) {
            updates = network.distanceVectorRouting(1, true);
            System.out.println("Actualizaciones: "+updates);
        }

        network.printTopology();
        network.dijkstra(0);
        NetworkDiagram.display(network);

    }
}