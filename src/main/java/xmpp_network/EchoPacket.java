
package xmpp_network;

/**
 * @author LPELCRACK896
 */
public class EchoPacket extends Packet{

    public long timestamp1;
    public long timestamp2;

    /**
     * Constructor used by sender.
     * @param from Sender JID
     * @param to Receiver JID
     */
    public EchoPacket(String from, String to) {
        super(from, to);
        timestamp1 = System.currentTimeMillis();
        timestamp2 = -1;
    }

    /**
     * Constructor use by receiver.
     * @param from Receiver JID
     * @param to Sender JID
     * @param timestamp1 Sender timestamp.
     */
    public EchoPacket(String from, String to, long timestamp1) {
        super(from, to);
        this.timestamp1 = timestamp1;
        timestamp2 = System.currentTimeMillis();
    }

    /*
     * #################
     * #################
     * AUXILIARY METHODS
     * #################
     * #################
     */
    /**
     * Stringifier in case it has two timestamps, meaning it was created by an echo receiver, and it will return to the original sender both timestamps.
     * @return JSON as String responding with both timestamps (original sender and receiver's)
     */
    public String toStringOnTwoTimestamps(){
        return "{" +
                "\"type\": \"echo\""+",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\"},\n"+
                "\"payload\": {\"timestamp1\": \""+timestamp1+"\", \"timestamp2\": \""+timestamp2+"\"}"+
                '}';
    }

    /**
     * Stringifier in case it has one timestamp, meaning it was created by a sender, it will send to an echo receiver single timestamp.
     * @return JSON as String responding with single timestamp (original sender)
     */
    public String toStringOnOneTimeStamp(){
        return "{" +
                "\"type\": \"echo\""+",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\"},\n"+
                "\"payload\": {\"timestamp1\": \""+timestamp1+"\" }"+
                '}';
    }

    /*
     * #################
     * #################
     * OVERRIDE OBJECT
     * #################
     * #################
     */
    /**
     * Stringifier.
     * @return JSON a String with different form depending on amount of timestamps (1 or 2).
     */
    @Override
    public String toString() {
        if (timestamp2 == -1){
            return toStringOnOneTimeStamp();
        }
        return  toStringOnTwoTimestamps();
    }
}