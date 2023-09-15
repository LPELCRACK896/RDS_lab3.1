package xmpp_network;
/**
 * @author LPELCRACK896
 */
public class MessagePacket extends Packet{

    private String body;
    private int hopCount;

    /**
     * Constructor.
     * @param from Sender JID
     * @param to Receiver JID
     * @param body Message content.
     * @param hopCount Number of hops so far.
     */
    public MessagePacket(String from, String to, String body, int hopCount) {
        super(from, to);
        this.body = body;
        this.hopCount = hopCount;
    }

    /**
     * Constructor.
     * @param from Sender JID.
     */
    public MessagePacket(String from){
        super(from);
    }
    /*
     * #################
     * #################
     * SETTERS AND GETTERS
     * #################
     * #################
     */

    /**
     * GET Hop count
     * @return attr: hop count.
     */
    public int getHopCount() {
        return hopCount;
    }

    /**
     * SETTER Hop count
     * @param hopCount new hop count.
     */
    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    /**
     * GET body
     * @return attr: body
     */
    public String getBody() {
        return body;
    }

    /**
     * SETTER body
     * @param body new body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /*
     * #################
     * #################
     * OVERRIDE OBJECT
     * #################
     * #################
     */

    /**
     * toString
     * @return JSON Strigified version of message package.
     */
    @Override
    public String toString() {
        return "{" +
                "\"type\": \"message\",\n" +
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\", \"hop_count\": "+hopCount+"},\n" +
                "\"payload\": \""+body+"\"\n"+
                "}";
    }
}

