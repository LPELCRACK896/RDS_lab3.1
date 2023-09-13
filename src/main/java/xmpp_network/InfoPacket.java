package xmpp_network;

import java.util.List;

public class InfoPacket extends Packet{
    private List<Route> routingTable;


    public InfoPacket(String from, String to) {
        super(from, to);
    }

    public InfoPacket(String from, String to, List<Route> routingTable) {
        super(from, to);
        this.routingTable = routingTable;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

