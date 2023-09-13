package xmpp_network;

import java.util.ArrayList;
import java.util.List;

public class InfoPacket extends Packet{
    private List<Route> routingTable;


    public InfoPacket(){
        super();
    }
    public InfoPacket(String from, String to) {
        super(from, to);
        routingTable = new ArrayList<Route>();
    }

    public void createDefault(ArrayList<String> nodes){
        ArrayList<Route> routingTable = new ArrayList<Route>();
        for (String node: nodes){
            routingTable.add(new Route(node));
        }
        this.routingTable = routingTable;
    }

    public Route findRoute(String alias){
        for (Route route: routingTable){
            if (route.getEnd().equals(alias)){
                return route;
            }
        }
        return null;
    }

    public void editARoute(String nodeAliasToEdit, String nextHop, long cost, boolean isNeighbor){

        Route route = findRoute(nodeAliasToEdit);
        if (route==null){
            return;
        }
        route.setCost(cost);
        route.setExist(true);
        route.setNeighbor(isNeighbor);
        route.setNextHop(nextHop);

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

