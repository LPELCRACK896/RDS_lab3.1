package network;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Network {

    private final int size;
    private int[][] matrix;
    private final ArrayList<Node> nodes;

    public Network(int size) {
        this.size = size;
        this.nodes = new ArrayList<>();
        initializeMatrixAndNodes();

    }

    public int getSize() {
        return size;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    private void initializeMatrixAndNodes() {
        Random random = new Random();
        matrix = new int[size][size];
        int i;

        for (i = 0; i < size; i++) {
            nodes.add(new Node(i));
        }

        for (i = 0; i < size; i++) {
            ArrayList<Path> table = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    table.add(new Path(nodes.get(i), nodes.get(j), 0));
                    matrix[i][j] = 0;
                } else if (j < i) { // Ya se creo la arista
                    matrix[i][j] = matrix[j][i];
                    Path pathFromOppositeDirection  = nodes.get(j).getTable().checkPathById(i);
                    if (pathFromOppositeDirection.step == null)
                        table.add(new Path(null, nodes.get(j), Integer.MAX_VALUE));
                    else
                        table.add(new Path(nodes.get(j), nodes.get(j), pathFromOppositeDirection.cost));
                } else {
                    if (random.nextDouble() < 0.3) {
                        int cost = random.nextInt(24) + 1;
                        matrix[i][j] = cost;
                        table.add(new Path(nodes.get(j), nodes.get(j), cost));
                    } else {
                        matrix[i][j] = Integer.MAX_VALUE;
                        table.add(new Path(null, nodes.get(j), Integer.MAX_VALUE));
                    }
                }
            }
            nodes.get(i).setNodePaths(table);
        }
    }

    public int distanceVectorRouting(int iterations, boolean printAfterEveryIteration){
        iterations = iterations>0 ? iterations : 5;
        int totalUpdates = 0;
        int i = 0;
        while (i<iterations){
            for (Node node: nodes){

                totalUpdates += node.sendTableToNeighbors();
            }
            if (printAfterEveryIteration){
                System.out.println("Tablas tras iteracion No."+(i+1));
                for (Node node: nodes){
                    System.out.println(node+"\n");
                    System.out.println(node.getTable());
                }
            }
            i++;

        }
        return  totalUpdates;
    }
}

