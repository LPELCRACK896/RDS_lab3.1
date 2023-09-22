package xmpp_network;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class mainNode3 {
    public static void main(String[] args) {

        // System
        Scanner scanner = new Scanner(System.in);
        String input;
      // Meta
        String mode = "lsr";
        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();

        // User
        String user = "test20003";
        String password = "admin123";
        String message;
        String destJID;

        XMPPNode node = new XMPPNode(user, password, topologyConfig, namesConfig, mode);
        XMPPNodeRunnable runnable = new XMPPNodeRunnable(node);
        //runnable.run(); // Manda echos constantemente
        boolean exit = false;

        while (!exit){


            System.out.print(
                    "- Ingrese \"flood message\" para enviar un mensaje a todos." +
                            "\n- Ingrese \"flood info\" para enviar la información del nodo." +
                            "\n- Ingrese \"message\" para enviar mensaje"+
                            "\n- Ingrese \"ping\" para mandar echo a vecinos"+
                            "\n- Ingrese la tecla q para salir: ");
            input = scanner.nextLine();
            switch (input.toLowerCase()){
                case "flood message"->{
                    System.out.println("Escribe mensaje:");
                    message = scanner.nextLine();
                    node.floodAllWaysMessage(message);
                }
                case "flood info"->{
                    node.floodInfoHops();
                }
                case "message"->{
                    System.out.println("Escribe mensaje:");
                    message = scanner.nextLine();
                    System.out.println("Escribir destinatario");
                    destJID = scanner.nextLine();
                    node.sendMessagePackage(destJID,0, message, true);
                }
                case "ping"->{
                    node.configureNode();
                }
                case "q"-> {
                    System.out.println("Has presionado la tecla 'q'. Saliendo...");
                    exit = true;

                }

            }
            System.out.println("\n\n");



        }
        node.logout();

    }
}
