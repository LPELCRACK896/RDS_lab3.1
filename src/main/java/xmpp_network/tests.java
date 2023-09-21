package xmpp_network;


import org.minidns.record.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.lang.Thread;


public class tests {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input;

        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();

        EchoPacket echoPacketInitial = new EchoPacket("gon20008", "test20002");

        EchoPacket echoPacketSecond = new EchoPacket("gon20008", "test20002", System.currentTimeMillis());

        String mode = "dv"; // Either "dv" or "lsr"

        ArrayList<XMPPNode> xmppNodes = new ArrayList<>();


        xmppNodes.add( new XMPPNode("gon20008", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20001", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20002", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20003", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20004", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20005", "admin123", topologyConfig, namesConfig, mode));
        xmppNodes.add( new XMPPNode("test20006", "admin123", topologyConfig, namesConfig, mode));


        XMPPNetwork network = new XMPPNetwork(xmppNodes, mode);
        network.configureNodes();
        Thread.sleep(15000);
        network.routing();


        Thread.sleep(15000);
        int i = 0;
        for (XMPPNode node1: xmppNodes){
            System.out.println("Nodo "+i+":");
            System.out.println(node1.getInfoPackage());
        }
        XMPPNode node = xmppNodes.get(0);
        node.sendMessagePackage("test20003", 0, "Hoola", true);

        while (true) {
            input = scanner.nextLine();

            switch (input) {
                case "flood" -> node.flood();
                case "echo" -> node.configureNode();
            }
            System.out.print("Ingrese la tecla q para salir: ");


            input = scanner.nextLine();


            if ("q".equalsIgnoreCase(input)) {
                System.out.println("Has presionado la tecla 'q'. Saliendo...");
                break;
            }
        }
        //scanner.close();

    }
}
