package xmpp_network;
/**
 * @author LPELCRACK896
 */
public class Packet {
    public String from;
    public String to;

    /**
     * Constructor
     * @param from Sender JID
     * @param to Receiver JID
     */
    public Packet(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Constructor
     */
    public Packet(){
        this.from = null;
        this.to = null;
    }

    /**
     * Constructor
     * @param from Sender JID
     */
    public Packet(String from){
        this.from = from;
    }
    /*
     * #################
     * #################
     * SETTER AND GETTERS
     * #################
     * #################
     */

    /**
     * GET from
     * @return attr: from
     */
    public String getFrom() {
        return from;
    }

    /**
     * SETTER from
     * @param from new from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * GET to
     * @return attr: to
     */
    public String getTo() {
        return to;
    }

    /**
     * SETTER to
     * @param to new to
     */
    public void setTo(String to) {
        this.to = to;
    }
}
