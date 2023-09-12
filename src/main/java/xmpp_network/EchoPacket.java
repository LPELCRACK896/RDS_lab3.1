package xmpp_network;


public class EchoPacket extends Packet{

    private long timestamp;
    public EchoPacket(String from, String to) {
        super(from, to);
        long timeStamp = System.currentTimeMillis();
    }



}