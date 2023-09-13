package xmpp_network;

import jdk.jshell.execution.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;

        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();

        XMPPNode node2 = new XMPPNode("test20001", "admin123", topologyConfig, namesConfig);
        System.out.println(System.currentTimeMillis());

        while (true) {
            System.out.print("Ingrese la tecla q para salir: ");
            input = scanner.nextLine();

            if ("q".equalsIgnoreCase(input)) {
                System.out.println("Has presionado la tecla 'q'. Saliendo...");
                node2.logout();
                break;
            }
        }
        scanner.close();

        //XMPPNode node1 = new XMPPNode("gon20008", "admin123", topologyConfig, namesConfig);
        //node1.ping("test20001");
        //node1.logout();

    }
}
