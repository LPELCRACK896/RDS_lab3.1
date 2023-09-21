package xmpp_network;

public class XMPPNodeRunnable implements Runnable {
    private final XMPPNode xmppNode;
    private volatile boolean running = true;

    public XMPPNodeRunnable(XMPPNode xmppNode) {
        this.xmppNode = xmppNode;
    }

    @Override
    public void run() {
        while (running) {
            xmppNode.configureNode(); // or any other method
            try {
                Thread.sleep(1000); // sleep for some time, for example, 1000 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupted status
                break;
            }
        }
    }

    public void stop() {
        running = false;
    }
}
