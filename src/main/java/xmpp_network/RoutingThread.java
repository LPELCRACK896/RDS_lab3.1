package xmpp_network;

public class RoutingThread extends Thread {
    private XMPPNode node;

    public RoutingThread(XMPPNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        // Lógica para manejar el routing
    }
}