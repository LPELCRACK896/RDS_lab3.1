package xmpp_network;

import jdk.jshell.execution.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input;

        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();
        String mode = "lsr";

        XMPPNode node = new XMPPNode("gon20008", "admin123", topologyConfig, namesConfig, mode);
        node.setUseAliasOnEcho(true);
        while (true) {
            System.out.println("Intruccion: ");
            input = scanner.nextLine();

            switch (input) {
                case "flood" -> node.flood();
                case "echo" -> node.configureNode();

            }


        }

        //node2.configureNode();
        //node2.setUpDijkstraTable();


    }
}
