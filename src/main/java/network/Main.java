package network;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Network network = new Network(10);
        ArrayList<Node> nodes = network.getNodes();

        for (Node node: nodes){
            System.out.printf(node.toString()+"\n");
            System.out.println(node.getTable());
        }
    }
}