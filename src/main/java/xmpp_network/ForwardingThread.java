package xmpp_network;

public class ForwardingThread extends Thread {
    private XMPPNode node;

    public ForwardingThread(XMPPNode node) {
        this.node = node;
    }

    @Override
    public void run() {
        // Lógica para manejar el forwarding
    }
}