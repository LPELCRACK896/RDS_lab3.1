package xmpp_network;

import com.sun.source.tree.Scope;
import jdk.jshell.execution.Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("Intruccion: ");
            input = scanner.nextLine();

            switch (input) {
                case "old" -> {
                    HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
                    HashMap<String, String> namesConfig  = Utils.getNamesConfig();
                    String mode = "dv";

                    XMPPNode node = new XMPPNode("lop20768", "Axel.129", topologyConfig, namesConfig, mode);
                    node.setUseAliasOnEcho(true);

                    String input1;
                    input1 = scanner.nextLine();
                    switch (input1) {
                        case "flood" -> node.flood();
                        case "echo" -> node.configureNode();
                        case "message" -> {
                            String message = scanner.nextLine();
                            node.sendMessage(message);
                        }
                    }
                }
                case "distance" -> {
                    int choice;

                    XMPPClient xmppClient = new XMPPClient();

                    String selectedNode = xmppClient.selectNode(false);
                    if (selectedNode != null) {
                        String userNode = selectedNode.replace("@alumchat.xyz", "");
                        xmppClient.login(userNode, "123");
                        int[] distanceVector = xmppClient.tablaEnrutamiento(selectedNode);
                        System.out.println("\nVECTOR DE DISTANCIA INICIAL:\n " + Arrays.toString(distanceVector));
                        xmppClient.broadcastMessage();
                    }
                    do {
                        System.out.println("\nMENU");
                        System.out.println("1. Vector distancia");
                        System.out.println("2. Enviar mensaje");
                        System.out.println("3. Salir");
                        System.out.print("Select an option: ");
                        choice = scanner.nextInt();
                        switch (choice) {
                            case 1:
                                System.out.println("\nVECTOR DE DISTANCIA:\n " + Arrays.toString(xmppClient.getTablaEnrutamiento()) + "\n ENLACES \n " + Arrays.toString(xmppClient.getEnlaces()));
                                break;
                            case 2:
                                String messageNode = xmppClient.selectNode(true);
                                System.out.print("Ingrese el mensaje a enviar: ");
                                scanner.nextLine();
                                String cm = scanner.nextLine();
                                System.out.println(messageNode);
                                System.out.println(cm);
                                xmppClient.chatMessage(messageNode, cm);
                                break;
                            case 3:
                                xmppClient.disconnect();
                                System.out.println("Gracias por usar el programa");
                                break;
                            default:
                                System.out.println("Invalid option.");
                        }
                    }while (choice != 3);
                }
            }


        }

    }
}
