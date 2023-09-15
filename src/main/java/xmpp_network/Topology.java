package xmpp_network;
/**
 * Classes to re-interpreter names.json and topology.json
 * @author LPELCRACK896
 */
import java.util.HashMap;
import java.util.List;

public class Topology {
    public String type;
    public Object config;

    public static class TopologyConfig {
        public HashMap<String, List<String>> data;
    }

    public static class NamesConfig {
        public HashMap<String, String> data;
    }
}

