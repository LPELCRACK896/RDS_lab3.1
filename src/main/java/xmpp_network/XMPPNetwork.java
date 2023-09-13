package xmpp_network;

import java.util.ArrayList;

public class XMPPNetwork {

    private ArrayList<XMPPNode> xmppNodes;
    public XMPPNetwork(ArrayList<XMPPNode> xmppNodes){
        this.xmppNodes = xmppNodes;
    }

    public void configureNodes(){
        for (XMPPNode node: xmppNodes){
            node.configureNode();
        }
        System.out.println("Esperando respuestas por 10 segundos");
        nothingForAWhile(10000);
    }

    public static void nothingForAWhile (int howMuch){
        try {
            Thread.sleep(howMuch);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    public void end(){
        for (XMPPNode node :xmppNodes){
            node.logout();
        }
    }
}
