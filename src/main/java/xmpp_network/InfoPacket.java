package xmpp_network;

import java.util.ArrayList;
import java.util.List;

public class InfoPacket extends Packet{
    private List<Route> routingTable;
    private int hopCount;


    public InfoPacket(){
        super();
        this.hopCount = 1;
    }
    public InfoPacket(String from, String to) {
        super(from, to);
        this.hopCount = 1;
        routingTable = new ArrayList<Route>();
    }
    public InfoPacket(String from, String to, List<Route> routingTable) {
        super(from, to);
        this.hopCount = 1;
        this.routingTable = routingTable;
    }


    public InfoPacket(String from, String to, List<Route> routingTable, int hopCount) {
        super(from, to);
        this.routingTable = routingTable;
        this.hopCount = hopCount;
    }

    public InfoPacket(String from){
        super(from);
    }

    public void changeRemitentAndDestination(String from, String to){
        this.from = from;
        this.to = to;
    }

    public void createDefault(ArrayList<String> nodes){
        ArrayList<Route> routingTable = new ArrayList<Route>();
        for (String node: nodes){
            routingTable.add(new Route(node));
        }
        this.routingTable = routingTable;
    }

    public List<Route> getRoutingTable() {
        return routingTable;
    }

    public void setRoutingTable(List<Route> routingTable) {
        this.routingTable = routingTable;
    }

    public int getHopCount() {
        return hopCount;
    }

    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
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

    public String stringifiedJSONRoutingTable(){
        StringBuilder jsonStrinfied = new StringBuilder("{");

        for (Route route: routingTable){
            if (route.getNextHop() != null){
                jsonStrinfied.append("\"").append(route.getEnd()).append("\":").append(route.getCost()).append(",");
            }
        }

        // Elimina la última coma
        if (jsonStrinfied.length() > 1) {
            jsonStrinfied.setLength(jsonStrinfied.length() - 1);
        }

        jsonStrinfied.append("}");
        return jsonStrinfied.toString();
    }


    @Override
    public String toString() {
        return "{\n" +
                "\"type\": \"info\",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\", \"hop_count\": "+hopCount+"},\n"+
                "\"payload\": "+stringifiedJSONRoutingTable()+
                "\n}";
    }
}

