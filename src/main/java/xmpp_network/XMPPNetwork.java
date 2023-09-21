package xmpp_network;

import java.awt.*;
import java.util.ArrayList;
/**
 * @author LPELCRACK896
 */
public class XMPPNetwork {
    private ArrayList<XMPPNode> xmppNodes;
    private String mode;

    /**
     * Constructor.
     * @param xmppNodes nodes members of network.
     * @param mode indicates the routing mode of network. Either "dv" or "lsr".
     */
    public XMPPNetwork(ArrayList<XMPPNode> xmppNodes, String mode){
        this.xmppNodes = xmppNodes;
        this.mode = mode; // Either "dv" or "lsr"
    }

    /**
     * Start all nodes by logging in all members.
     */
    public void configureNodes(){
        for (XMPPNode node: xmppNodes){
            node.setMode(mode);
            node.configureNode();
        }
        System.out.println(Colors.cyanText("Finalizo inicalizaciones de nodos"));
        System.out.println(Colors.cyanText("Esperando respuestas por 10 segundos"));
        nothingForAWhile(1000);
    }

    /**
     * Routes the network based on mode.
     */
    public void routing (){
        switch (mode)
        {
            case "dv" -> distanceVector();
            case "lsr" -> linkStateRouting();
        }
    }

    /**
     * Routes the network using link state routing
     */
    private void linkStateRouting(){
        // Flooding
        for (XMPPNode node: xmppNodes){
            node.flood();
        }
        nothingForAWhile(5000);
        for (XMPPNode node: xmppNodes){
            node.setUpDijkstraTable();
        }
    }

    /**
     * Routes the network using distance vector.
     */
    private void distanceVector(){
        int totalIterations = xmppNodes.size();
        for (int i = 0; i<totalIterations;i++ ){
            for (XMPPNode node :xmppNodes){
                node.sendInfoToNeighbors();
                nothingForAWhile(2000);
            }

        }

    }

    /**
     * Sleeps thread for a while
     * @param howMuch Number of ms that process will sleep.
     */
    public static void nothingForAWhile (int howMuch){
        try {
            Thread.sleep(howMuch);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    /**
     * Ends the network by logging off all node networks members.
     */
    public void end(){
        for (XMPPNode node :xmppNodes){
            node.logout();
        }
    }

    /*
     * #################
     * #################
     * SETTERS AND GETTERS
     * #################
     * #################
     */
    /**
     * GET XmppNodes
     * @return attr: xmppNodes
     */
    public ArrayList<XMPPNode> getXmppNodes() {
        return xmppNodes;
    }

    /**
     * SETTER XmppNodes
     * @param xmppNodes new xmppNodes
     */
    public void setXmppNodes(ArrayList<XMPPNode> xmppNodes) {
        this.xmppNodes = xmppNodes;
    }


}
