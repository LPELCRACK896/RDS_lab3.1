package network;

import java.util.HashMap;

public class JSONPackage {


    private String type;
    private HashMap<String, String> headers;
    private String payload;

    public JSONPackage(String type, HashMap<String, String> headers, String payload) {
        this.type = type;
        this.headers = headers;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\"type\": "+type+"\n" +
                "\"headers\": "+headers+"\n" +
                "\"payload\": "+payload+"\n" +
                "}";
    }
}
