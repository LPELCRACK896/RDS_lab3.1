package xmpp_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoPacket extends Packet{
    private List<Route> routingTable;
    private int hopCount;
    private String aliasOwner;


    public InfoPacket(String from, String aliasOwner){
        super(from);
        this.aliasOwner = aliasOwner;
        routingTable = new ArrayList<Route>();

    }



    public void changeRemitentAndDestination(String from, String to){
        this.from = from;
        this.to = to;
    }

    public void createDefault(ArrayList<String> nodes){
        ArrayList<Route> routingTable = new ArrayList<Route>();
        for (String node: nodes){
            if (node.equals(this.aliasOwner)){
                // Ruta hacia si mismo
                routingTable.add(new Route(node, node, 0, true));
            }else{
                routingTable.add(new Route(node));
            }
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

    public int updateTable(HashMap<String, Long> othersTable, String aliasTableOwner){
        int totalUpdates = 0;
        Route routeToOwner = findRoute(aliasTableOwner);
        if (routeToOwner.isExist()) {
            long costToOwner = routeToOwner.getCost();
            String nextHop = routeToOwner.getNextHop();
            for (Map.Entry<String, Long> entry : othersTable.entrySet()) {
                Route route = findRoute(entry.getKey());
                if (!route.isExist()) {
                    totalUpdates += 1;
                    route.setCost(entry.getValue() + costToOwner);
                    route.setNextHop(nextHop);
                    route.setExist(true);
                }
                else {
                    long currentCost = route.getCost();
                    long newCost = costToOwner + entry.getValue();

                    if (newCost<currentCost){
                        route.setCost(newCost);
                        route.setNextHop(nextHop);
                        totalUpdates += 1;
                    }
                }

            }
        }
        else{
            System.err.println("No es posible utilizar la tabla para actualizar");
        }


        return totalUpdates;
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

