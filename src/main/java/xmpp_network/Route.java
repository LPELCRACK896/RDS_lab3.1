package xmpp_network;
/**
 * @author LPELCRACK896
 */
public class Route {

    private String end;
    private String nextHop;
    private long cost;
    private boolean exist;
    private boolean isNeighbor;

    /**
     * Constructor
     * @param end the objective of the route (alias)
     * @param nextHop the next step to get to the end
     * @param cost total cost of taking this path.
     * @param exist indicates if the route does exist.
     */
    public Route(String end, String nextHop, long cost, boolean exist) {
        this.end = end;
        this.nextHop = nextHop;
        this.cost = cost;
        this.exist = exist;
        this.isNeighbor = false;
    }
    /**
     * Constructor
     * @param end the objective of the route (alias)
     */
    public Route(String end){
        this.end = end;
        this.nextHop = null;
        this.cost = Long.MAX_VALUE;
        this.exist = false;
        this.isNeighbor = false;
    }

    /*
     * #################
     * #################
     * Auxiliary methods
     * #################
     * #################
     */

    /**
     * Checks if some other route is the same as "this" route based on the end, cost and next hop.
     * @param other route to compare
     * @return boolean indicating if its some same route.
     */
    public boolean isTheSame(Route other){
        return other.getCost()==this.cost && this.end.equals(other.getEnd()) && this.nextHop.equals(other.getNextHop());
    }

    /*
     * #################
     * #################
     * GETTERS AND SETTERS
     * #################
     * #################
     */

    /**
     * GET isNeighbor
     * @return attr: isNeighbor
     */
    public boolean isNeighbor() {
        return isNeighbor;
    }

    /**
     * SETTER isNeighbor
     * @param neighbor new isNeighbor
     */
    public void setNeighbor(boolean neighbor) {
        isNeighbor = neighbor;
    }

    /**
     * GET end
     * @return attr: end
     */
    public String getEnd() {
        return end;
    }

    /**
     * SETTER end
     * @param end new end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * GET next hop
     * @return attr: next hop
     */
    public String getNextHop() {
        return nextHop;
    }

    /**
     * SETTER next hop
     * @param nextHop new next hop
     */
    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    /**
     * GET cost
     * @return attr: cost
     */
    public long getCost() {
        return cost;
    }

    /**
     * SETTER cost
     * @param cost new cost
     */
    public void setCost(long cost) {
        this.cost = cost;
    }

    /**
     * GET exist
     * @return attr: exist
     */
    public boolean isExist() {
        return exist;
    }

    /**
     * SETTER exist
     * @param exist new exist
     */
    public void setExist(boolean exist) {
        this.exist = exist;
    }


}

