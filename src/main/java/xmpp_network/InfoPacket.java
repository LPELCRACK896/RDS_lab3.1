package xmpp_network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LPELCRACK896
 */
public class InfoPacket extends Packet{
    private List<Route> routingTable;
    private int hopCount;
    private String aliasOwner;

    /**
     * Constructor.
     * @param from JID sender
     * @param aliasOwner Node graph name. Expects to respect the names.json.
     */
    public InfoPacket(String from, String aliasOwner){
        super(from);
        this.aliasOwner = aliasOwner;
        routingTable = new ArrayList<Route>();

    }

    /*
     * #################
     * #################
     * MAIN METHODS
     * #################
     * #################
     */

    /**
     * Changed attributes from and to.
     * @param from Sender JID.
     * @param to Receiver JID.
     */
    public void changeRemitentAndDestination(String from, String to){
        this.from = from;
        this.to = to;
    }

    /**
     * Instantiate routing table using a hashmap that contains node graph name as key (such as A, B... etc.) and a long value which represents cost.
     * @param originalOwner Node graph name (alias) that owns the table.
     * @param nodes Name (alias) of nodes members of network.
     * @param existing the hashmap to use as reference for existing paths.
     */
    public void createFromHash(String originalOwner, ArrayList<String> nodes, HashMap<String, Long> existing){
        ArrayList<Route> routingTable = new ArrayList<Route>();
        for (String node: nodes){
            if (existing.containsKey(node)){
                routingTable.add(new Route(node, originalOwner, existing.get(node), true));
            }
            else{
                if (node.equals(this.aliasOwner)){
                    routingTable.add(new Route(node, node, 0, true)); // Route to himself with no cost
                }else{
                    routingTable.add(new Route(node));
                }
            }

        }
        this.routingTable = routingTable;

    }

    /**
     * Instantiate routing table using the name of the network members.
     * @param nodes Name (alias) of nodes members of network.
     */
    public void createDefault(ArrayList<String> nodes){
        ArrayList<Route> routingTable = new ArrayList<Route>();
        for (String node: nodes){
            if (node.equals(this.aliasOwner)){
                routingTable.add(new Route(node, node, 0, true)); // Route to himself with no cost
            }else{
                routingTable.add(new Route(node));
            }
        }
        this.routingTable = routingTable;
    }

    /**
     * Finds the route to a certain node based on its name.
     * @param alias Node name (alias) that is looking for a path.
     * @return a route to the objective node.
     */
    public Route findRoute(String alias){
        for (Route route: routingTable){
            if (route.getEnd().equals(alias)){
                return route;
            }
        }
        return null;
    }

    /**
     * Change the values of an existing route with a certain end.
     * @param nodeAliasToEdit The end node graph name.
     * @param nextHop the new next step on the route
     * @param cost the new cost.
     * @param isNeighbor to indicate if this new route is neighbour.
     */
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

    /**
     * Using structures that represents some other's table, check and updates if it has some cheaper route to any of the ends (adding the cost that it takes to go to the table owner).
     * @param othersTable Hashmap representing the other's table.
     * @param aliasTableOwner Node graph name of table owner.
     */
    public void updateTable(HashMap<String, Long> othersTable, String aliasTableOwner){
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

        System.out.println("La tabla de "+this.aliasOwner+ " actualizo "+totalUpdates+" en su tabla");
    }

    /*
     * #################
     * #################
     * Auxiliary methods
     * #################
     * #################
     */

    /**
     * Turns a routing table that contains Route objects to a simplified version as hashmap that only includes the end, and it's cost.
     * @param routingTable Original routing table of some node.
     * @return Simplified version of it as hashmap.
     */
    public static HashMap<String, Long> getSimplifiedTable(List<Route> routingTable){
        HashMap<String, Long> simplifiedTable = new HashMap<String, Long>();

        for (Route route: routingTable){
            simplifiedTable.put(route.getEnd(), route.getCost());
        }
        return simplifiedTable;

    }

    /**
     * Gets a stringified version of a JSON representation of the table route that is meant to be sent as part of the package.
     * @return JSON stringified version of routing table.
     */
    public String stringifiesJSONRoutingTable(){
        StringBuilder jsonStringed = new StringBuilder("{");
        for (Route route: routingTable){
            if (route.getNextHop() != null){
                jsonStringed.append("\"").append(route.getEnd()).append("\":").append(route.getCost()).append(",");
            }
        }
        // Removes last comma
        if (jsonStringed.length() > 1) {
            jsonStringed.setLength(jsonStringed.length() - 1);
        }
        jsonStringed.append("}");
        return jsonStringed.toString();
    }

    /**
     * Returns if this InfoPacket is essentially the same as the InfoPacket passed as parameter.
     * @param other the other InfoPacket to compare.
     * @return Value if it's the same.
     */
    public boolean isTheSame(InfoPacket other){

        if (!this.from.equals(other.getFrom())){
            return false;
        }
        for (Route route:routingTable){
            String alias = route.getEnd();
            Route otherRoute = other.findRoute(alias);

            if (otherRoute==null){
                return false;
            }
            if (!route.isExist()){
                if (otherRoute.isExist()){
                    return true;
                }
            }
            else if (!route.isTheSame(otherRoute)){
                return false;
            }
        }
        return true;
    }

    /*
     * #################
     * #################
     * SETTER AND GETTERS
     * #################
     * #################
     */

    /**
     * GET Routing Table
     * @return attr: Routing table.
     */
    public List<Route> getRoutingTable() {
        return routingTable;
    }

    /**
     * SETTER Routing table.
     * @param routingTable new value of Routing table.
     */
    public void setRoutingTable(List<Route> routingTable) {
        this.routingTable = routingTable;
    }

    /**
     * GET Hop count.
     * @return attr: Hop Count.
     */
    public int getHopCount() {
        return hopCount;
    }

    /**
     * SETTER Hop Count
     * @param hopCount new value of hop count.
     */
    public void setHopCount(int hopCount) {
        this.hopCount = hopCount;
    }

    /*
     * #################
     * #################
     * OVERRIDE OBJECT
     * #################
     * #################
     */

    /**
     *
     * @return JSON stringified version of packet.
     */
    @Override
    public String toString() {
        return "{\n" +
                "\"type\": \"info\",\n"+
                "\"headers\": {\"from\":\""+super.from+"\",\"to\":\""+super.to+"\", \"hop_count\": "+hopCount+"},\n"+
                "\"payload\": "+ stringifiesJSONRoutingTable()+
                "\n}";
    }
}



