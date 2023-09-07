package network;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Network network = new Network(10);
        ArrayList<Node> nodes = network.getNodes();

        network.flooding();

        int updates = 1;
        while (updates!=0) {
            updates = network.distanceVectorRouting(1, false);
            System.out.println("Actualizaciones: "+updates);
        }
        for (Node node: nodes){
            System.out.println(node+"\n");
            System.out.println(node.getTable());
        }

        network.dijkstra(0);

    }
}