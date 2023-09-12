package xmpp_network;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

public class Utils {

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
