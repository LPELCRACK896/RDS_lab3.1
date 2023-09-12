package xmpp_network;

public class Packet {
    private String from;
    private String to;

    public Packet(String from, String to) {
        this.from = from;
        this.to = to;
    }
}
