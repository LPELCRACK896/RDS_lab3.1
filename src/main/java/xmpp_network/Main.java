package xmpp_network;

import jdk.jshell.execution.Util;

import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        HashMap<String, List<String>> topologyConfig = Utils.getTopologyConfig();
        HashMap<String, String> namesConfig  = Utils.getNamesConfig();








        XMPPNode node1 = new XMPPNode("gon20008", "admin123", topologyConfig, namesConfig);

        XMPPNode node2 = new XMPPNode("test20001", "admin123", topologyConfig, namesConfig);
        System.out.println(System.currentTimeMillis());
        node1.ping("test20001");

        node1.logout();
        node2.logout();
    }
}
