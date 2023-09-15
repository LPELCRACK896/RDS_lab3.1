package xmpp_network;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
/**
 * Contains static methods to diverse uses.
 * - JSON parsing
 * @author LPELCRACK896
 */
public class Utils {

    /**
     * Turns topology.json into a proper Hashmap
     * @return hashmap equivalent to topology.json
     */
    public static HashMap<String, List<String>> getTopologyConfig(){
        ObjectMapper objectMapper = new ObjectMapper();


        // Leer topology.json
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("topology.json")) {
            Topology topologyData = objectMapper.readValue(is, Topology.class);
            return  (HashMap<String, List<String>>) topologyData.config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * Turns names.json into a proper hashmap
     * @return hashmap equivalent to names.json
     */
    public static HashMap<String, String> getNamesConfig(){
        ObjectMapper objectMapper = new ObjectMapper();

        // Leer names.json
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("names.json")) {
            Topology namesData = objectMapper.readValue(is, Topology.class);
            return (HashMap<String, String>) namesData.config;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


}
