package xmpp_network;


public class EchoPacket extends Packet{

    public long timestamp1;
    public long timestamp2;

    public EchoPacket(String from, String to) {
        super(from, to);
        timestamp1 = System.currentTimeMillis();
        timestamp2 = -1;
    }

    public EchoPacket(String from, String to, long timestamp1) {
        super(from, to);
        this.timestamp1 = timestamp1;
        timestamp2 = System.currentTimeMillis();
    }


    public String toStringOnTwoTimestamps(){
        return "{" +
                "\"type\": \"echo\""+",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":"+super.to+"},\n"+
                "\"payload\": {\"timestamp1\": "+timestamp1+", \"timestamp2\": "+timestamp2+"}"+
                '}';
    }

    public String toStringOnOneTimeStamp(){
        return "{" +
                "\"type\": \"echo\""+",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\"},\n"+
                "\"payload\": {\"timestamp1\": "+timestamp1+" }"+
                '}';
    }

    @Override
    public String toString() {
        if (timestamp2 == -1){
            return toStringOnOneTimeStamp();
        }
        return  toStringOnTwoTimestamps();
    }
}