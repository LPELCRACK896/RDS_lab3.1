package network;

import java.util.ArrayList;

public class TablePaths {

    private ArrayList<Path> table;

    public TablePaths (ArrayList<Path> table){
        this.table = table;
    }

    public Path checkPathById(int id){
        return table.get(id);
    }
    public Path checkPathByAlias(String alias){
        return checkPathById(parseAlias(alias));
    }


    public static int parseAlias(String alias) {
        int num = 0;
        for (int i = 0; i < alias.length(); i++) {
            num = num * 26 + (alias.charAt(i) - 'A' + 1);
        }
        return num - 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+---------+---------+---------+\n");
        sb.append(String.format("| %-6s | %-6s | %-6s |\n", "Destino", "Siguiente", "Costo"));
        sb.append("+---------+---------+---------+\n");

        for (Path path: table){
            sb.append(String.format("| %-6s | %-6s | %-10d |\n", path.end, path.step, path.cost));
        }
        return sb.toString();
    }
}
