package network;

public class Path {

    public Node end;
    public Node step;
    public int cost;

    public Path(Node step, Node end, int cost) {
        this.end = end;
        this.cost = cost;
        this.step = step;
    }
}
