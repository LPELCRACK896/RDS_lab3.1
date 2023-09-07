package network;

import java.util.ArrayList;
import java.util.HashMap;

public class Node {
    private final int id;
    private final String alias;
    private TablePaths table;

    private ArrayList<Node> neighbors;

    private ArrayList<TablePaths> packagesBuffer;
    private ArrayList<TablePaths> sentPackages;

    public Node (int id){
        this.id = id;
        this.alias = generateAlias(id);
        packagesBuffer = new ArrayList<>();
        sentPackages = new ArrayList<>();
    }

    @Override
    public String toString() {
        return alias;
    }

    public void setNodePaths(ArrayList<Path> nodePaths){
        this.table = new TablePaths(nodePaths, this.id, this.alias);
        this.neighbors = findOutNeighbors(this.table);
    }

    public TablePaths getTable(){
        return table;
    }
    public static String generateAlias(int num) {
        StringBuilder sb = new StringBuilder();

        while (num >= 0) {
            int remainder = num % 26;
            char letter = (char) (remainder + 'A');
            sb.insert(0, letter); // prepend the character

            num = (num / 26) - 1;
        }

        return sb.toString();
    }
    public static int parseAlias(String alias) {
        int num = 0;
        for (int i = 0; i < alias.length(); i++) {
            num = num * 26 + (alias.charAt(i) - 'A' + 1);
        }
        return num - 1;
    }

    public int sendTableToNeighbors(){
        int totalUpdates  = 0;
        for (Node neighbor: this.neighbors){
            HashMap<String, String> headers = new HashMap<>();
            headers.put("from", this.alias);
            headers.put("to", neighbor.alias);
            headers.put("hop_count", "1");
            System.out.println(new JSONPackage("info", headers, "Tabla de nodo con alias: "+table.getNodeId()));
            totalUpdates += neighbor.recieveNeighborTable(table, this);
        }
        return totalUpdates;
    }

    public int recieveNeighborTable(TablePaths neighborTable, Node node){
        int totalUpdates = 0;
        int cost = neighborTable.checkPathById(this.id).cost;

        for (int i = 0; i< neighborTable.getSize(); i++){
            Path neighborPath = neighborTable.checkPathById(i);
            Path currentPath = table.checkPathById(i);
            int total = cost + neighborPath.cost;

            if (neighborPath.step != null){
                if (total  < currentPath.cost){
                    Path updatedPath = new Path(node, currentPath.end, total);
                    this.table.replacePath(i, updatedPath);
                    totalUpdates++;
                }
            }
        }
        return  totalUpdates;





    }

    private ArrayList<Node> findOutNeighbors(TablePaths table){
        ArrayList<Node> neighbors =  new ArrayList<>();
        for (Path path: table.getTable()){
            if (path.step == path.end && path.end!=this){
                neighbors.add(path.step);
            }
        }
        return neighbors;
    }
    public int getId() {
        return id;
    }
    public String getAlias() {
        return alias;
    }


    public void floodingSend(int senderId, TablePaths nodePackage, int hops, int countHops){
        if (hops<1) return;
        if (sentPackages.contains(nodePackage)){
            System.out.println("El nodo "+this.id+" detuvo el envio de un paquete que ya envió con anterioridad: "+nodePackage.getNodeId());
            return;
        }
        sentPackages.add(nodePackage);
        for (Node neighbor: this.neighbors){
            if (neighbor.id != senderId){
                HashMap<String, String> headers = new HashMap<>();
                headers.put("from", this.alias);
                headers.put("to", neighbor.alias);
                headers.put("hop_count", String.valueOf(countHops));
                System.out.println(new JSONPackage("info", headers, "Tabla de nodo con alias: "+(nodePackage.getNodeAlias())));
                neighbor.floodingRecieve(this.id, nodePackage, hops, countHops);

            }
        }


    }

    public void floodingRecieve(int senderId, TablePaths nodePackage, int hops, int countHop){
        countHop += 1;

        if (packagesBuffer.contains(nodePackage)){
            System.out.println("El nodo "+this.id+" ya recibio el paquete: "+nodePackage.getNodeAlias());
            return;
        }
        packagesBuffer.add(nodePackage);

        // Reenvio
        int remainingHops = hops - 1;

        if (remainingHops>0){
            floodingSend(senderId, nodePackage, remainingHops, countHop);
        }
    }

}
