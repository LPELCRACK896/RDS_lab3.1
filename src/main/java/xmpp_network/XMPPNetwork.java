package xmpp_network;

import java.util.ArrayList;

public class XMPPNetwork {
    private ArrayList<XMPPNode> xmppNodes;
    private String mode;

    public XMPPNetwork(ArrayList<XMPPNode> xmppNodes, String mode){
        this.xmppNodes = xmppNodes;
        this.mode = mode; // Either "dv" or "lsr"
    }

    public void configureNodes(){
        for (XMPPNode node: xmppNodes){
            node.setMode(mode);
            node.configureNode();
        }
        System.out.println("Finalizo inicalizaciones de nodos");
        System.out.println("Esperando respuestas por 10 segundos");
        nothingForAWhile(10000);
    }

    public void routing (){
        switch (mode)
        {
            case "dv" -> distanceVector();
            case "lsr" -> linkStateRouting();
        }
    }

    private void linkStateRouting(){

        for (XMPPNode node: xmppNodes){
            node.flood();
        }
        System.out.println();
        //

    }
    private void distanceVector(){
        int totalIterations = xmppNodes.size();
        for (int i = 0; i<totalIterations;i++ ){
            for (XMPPNode node :xmppNodes){
                node.sendInfoToNeighbors();
                nothingForAWhile(2000);
            }

        }

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

    /**
     * SETTERS AND GETTERS
     */
    /**
     *
     * @return
     */
    public ArrayList<XMPPNode> getXmppNodes() {
        return xmppNodes;
    }

    public void setXmppNodes(ArrayList<XMPPNode> xmppNodes) {
        this.xmppNodes = xmppNodes;
    }


}
