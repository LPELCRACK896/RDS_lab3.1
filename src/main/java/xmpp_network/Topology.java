package xmpp_network;

import java.util.HashMap;
import java.util.List;

public class Topology {
    public String type;
    public Object config; // Se puede usar Object aquí y hacer el casting después de leer los datos

    public static class TopologyConfig {
        public HashMap<String, List<String>> data;
    }

    public static class NamesConfig {
        public HashMap<String, String> data;
    }
}

