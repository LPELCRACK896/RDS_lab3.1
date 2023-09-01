package network;

import java.util.ArrayList;

public class Node {
    private final int id;
    private final String alias;
    private TablePaths table;

    public Node (int id){
        this.id = id;
        this.alias = generateAlias(id);
    }

    @Override
    public String toString() {
        return alias;
    }

    public void setNodePaths(ArrayList<Path> nodePaths){
        this.table = new TablePaths(nodePaths);
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
    public int getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }
}
