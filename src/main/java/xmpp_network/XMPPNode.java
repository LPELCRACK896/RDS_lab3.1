package xmpp_network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.jid.impl.JidCreate;

import java.io.IOException;
import java.util.*;

public class XMPPNode {
    private String JID;
    private String alias;
    private List<String> neighbors;
    private boolean authenticated;
    private HashMap<String, List<String>> topologyConfig;
    private HashMap<String, String> namesConfig;
    private InfoPacket infoPacket;
    private ChatManager chatManager;
    private AbstractXMPPConnection connection;
    private boolean isLoggedIn;


    // Constants

    private final String domain = "alumchat.xyz";
    private final String ip = "146.190.213.97";

    public XMPPNode (String JID, String password, HashMap<String, List<String>> topologyConfig, HashMap<String, String> namesConfig) {
        this.JID = JID;
        this.namesConfig = namesConfig;
        this.topologyConfig = topologyConfig;
        this.alias = figureOutOwnAlias();
        this.neighbors = figureOutNeighbors();


        this.connection = createConnection();
        this.chatManager = null;
        this.isLoggedIn = false;

        Set<String> networkMembers = namesConfig.keySet();
        ArrayList<String> arraylistNetworkMembers = new ArrayList<String>(networkMembers);

        this.infoPacket = new InfoPacket(getNameFromJIDWithDomain(this.JID));
        this.infoPacket.createDefault(arraylistNetworkMembers);


        if (connection != null) {
            login(JID, password);
            this.chatManager = ChatManager.getInstanceFor(connection);
        }

    }

    public void configureNode(){
        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);

            EchoPacket echoPacket = new EchoPacket(JID, destination);
            sendMessage(destination, echoPacket.toString());
        }
    }


    private String figureOutOwnAlias(){
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if ((this.JID+"@alumchat.xyz").equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null; // or some default value, or throw an exception
    }
    private List<String> figureOutNeighbors(){

        if (this.alias!=null){
            for (Map.Entry<String, List<String>> entry: topologyConfig.entrySet()){
                if (this.alias.equals(entry.getKey())){
                    return entry.getValue();
                }
            }

        }
        return null;

    }
    public void login(String JID, String password) {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.login(JID, password);
            this.isLoggedIn = true;

            System.out.println("Logged in successfully!");
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    public void logout() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Logged out successfully!");
            this.isLoggedIn = false;
        }
    }

    private String getNeighborJID(String alias){
        return namesConfig.get(alias);
    }


    private AbstractXMPPConnection createConnection() {
        System.out.println("Comienza a crear conexion");
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain("alumchat.xyz")
                    .setHost("146.190.213.97")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    //.enableDefaultDebugger()
                    .build();
            System.out.println("Termina creacion de conexion");
            AbstractXMPPConnection connection = new XMPPTCPConnection(config);
            try {
                connection.connect();
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                chatManager.addIncomingListener((from, message, chat) -> {
                    String response = message.getBody();
                    JsonObject newJSON = JsonParser.parseString(response).getAsJsonObject();
                    this.executeResponse(newJSON);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return connection;
        } catch (XmppStringprepException e) {
            System.err.println("Error creating XMPP connection: " + e.getMessage());
            return null;  // or you can throw a RuntimeException to terminate the program
        }
    }

    private void executeResponse(JsonObject response) {
        System.out.println();
        String type = response.get("type").getAsString();
        JsonObject payload;

        switch (type) {
            case "echo" -> {
                System.out.println("Recibio echo "+JID);
                echoResponseHandler(response);

            }
            case "message" -> {
                System.out.println("Recibio message "+JID);
                messageResponseHandler(response);
            }
            case "info" -> {
                System.out.println("Recibio info "+JID);
                infoResponseHandler(response);
            }
        }

    }
    private String getNameFromJIDWithDomain(String string){
        String[] stringSplited  = string.split("@", 2);
        return stringSplited[0];
    }
    private void echoResponseHandler(JsonObject response){
        System.out.println(response.toString());
        JsonObject payload = response.get("payload").getAsJsonObject();
        JsonObject headers = response.get("headers").getAsJsonObject();

        String sender = getNameFromJIDWithDomain(headers.get("from").getAsString());
        String reciever = getNameFromJIDWithDomain(headers.get("to").getAsString());



        boolean hasTimestamp2 = payload.has("timestamp2");
        // Esta recibiendo una respuesta ping
        if (hasTimestamp2) {
            long timeStamp1 = payload.get("timestamp1").getAsLong();
            long timeStamp2 = payload.get("timestamp2").getAsLong();
            long difference = timeStamp2 - timeStamp1;
            String alias = getAliasFromJID(sender);
            boolean isNeighbor =  neighbors.contains(alias);
            this.infoPacket.editARoute(alias, alias, difference, isNeighbor);
            return;
        }

        long timeStamp1 = payload.get("timestamp1").getAsLong();
        EchoPacket echoPacket = new EchoPacket(reciever, sender, timeStamp1);
        sendMessage(sender, echoPacket.toString());

    }

    private void infoResponseHandler(JsonObject response){

    }

    private void messageResponseHandler(JsonObject response){

    }

    public void sendMessage(String toJID, String messageContent) {

        toJID = toJID+"@alumchat.xyz";
        if (chatManager == null) {
            ChatManager.getInstanceFor(connection);
        }
        try {
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(toJID));
            chat.send(messageContent);
        } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    public InfoPacket getInfoPacket() {
        return infoPacket;
    }

    public void setInfoPacket(InfoPacket infoPacket) {
        this.infoPacket = infoPacket;
    }

    public String getJIDFromAlias(String alias){
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if (entry.getKey().equals(alias)){
                return getNameFromJIDWithDomain(entry.getValue());
            }
        }
        return null;

    }

    public String getAliasFromJID(String JID){
        JID =!JID.contains("@alumchat.xyz") ? JID+"@alumchat.xyz": JID;
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if (entry.getValue().equals(JID)){
                return entry.getKey();
            }
        }
        return null;
    }


}
