package xmpp_network;

public class Route {

    private String end;
    private String nextHop;
    private float cost;
    private boolean exist;

    public Route(String end, String nextHop, float cost, boolean exist) {
        this.end = end;
        this.nextHop = nextHop;
        this.cost = cost;
        this.exist = exist;
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

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }
}

