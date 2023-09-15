package xmpp_network;

public class MessagePacket extends Packet{

    private String body;
    private String messageId;
    private int hopCount;

    public MessagePacket(String from, String to, String body, int hopCount) {
        super(from, to);
        this.body = body;
        this.hopCount = hopCount;
    }
    public MessagePacket(String from){
        super(from);
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\": \"message\",\n" +
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\", \"hop_count\": "+hopCount+"},\n" +
                "\"payload\": \""+body+"\"\n"+
                "}";
    }
}

