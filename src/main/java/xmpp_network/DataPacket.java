package xmpp_network;

public class DataPacket extends Packet{

    private String body;
    private String messageId;

    public DataPacket(String from, String to) {
        super(from, to);
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
}

