package xmpp_network;

public class Route {

    private String end;
    private String nextHop;
    private long cost;
    private boolean exist;
    private boolean isNeighbor;

    public Route(String end, String nextHop, long cost, boolean exist) {
        this.end = end;
        this.nextHop = nextHop;
        this.cost = cost;
        this.exist = exist;
        this.isNeighbor = false;
    }

    public Route(String end){
        this.end = end;
        this.nextHop = null;
        this.cost = Long.MAX_VALUE;
        this.exist = false;
        this.isNeighbor = false;
    }

    public boolean isNeighbor() {
        return isNeighbor;
    }

    public void setNeighbor(boolean neighbor) {
        isNeighbor = neighbor;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isTheSame(Route other){
        return other.getCost()==this.cost && this.end.equals(other.getEnd()) && this.nextHop.equals(other.getNextHop());
    }
}

