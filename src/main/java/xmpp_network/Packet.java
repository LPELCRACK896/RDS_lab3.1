package xmpp_network;

public class Packet {
    public String from;
    public String to;

    public Packet(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public Packet(){
        this.from = null;
        this.to = null;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
