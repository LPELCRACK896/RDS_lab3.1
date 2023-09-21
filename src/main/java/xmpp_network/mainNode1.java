package xmpp_network;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class mainNode1 {
    public static void main(String[] args) {

        // System
        Scanner scanner = new Scanner(System.in);
        String input;
      // Meta
        String mode = "lsr";
        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();

        // User
        String user = "test20001";
        String password = "admin123";

        XMPPNode node = new XMPPNode(user, password, topologyConfig, namesConfig, mode);
        boolean exit = false;
        while (!exit){


            System.out.print("Ingrese la tecla q para salir: ");
            input = scanner.nextLine();
            if ("q".equalsIgnoreCase(input)) {
                System.out.println("Has presionado la tecla 'q'. Saliendo...");
                exit = true;
            }

        }
        node.logout();

    }
}
