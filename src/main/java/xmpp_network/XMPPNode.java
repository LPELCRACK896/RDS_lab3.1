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
    private MessagePacket msgPacket;
    private HashMap<String, InfoPacket> tablesBuffer;
    private HashMap<String, String> dijkstraTable;

    private String mode;


    // Constants

    private final String domain = "alumchat.xyz";
    private final String ip = "146.190.213.97";

    public XMPPNode (String JID, String password, HashMap<String, List<String>> topologyConfig, HashMap<String, String> namesConfig, String mode) {
        this.JID = JID;
        this.namesConfig = namesConfig;
        this.topologyConfig = topologyConfig;
        this.alias = figureOutOwnAlias();
        this.neighbors = figureOutNeighbors();
        this.connection = createConnection();
        this.chatManager = null;
        this.isLoggedIn = false;
        this.tablesBuffer = new HashMap<String, InfoPacket>();
        this.dijkstraTable = new HashMap<String, String>();
        this.mode = mode; // Either "dv" or "lsr"

        Set<String> networkMembers = namesConfig.keySet();
        ArrayList<String> arraylistNetworkMembers = new ArrayList<String>(networkMembers);

        this.infoPacket = new InfoPacket(getNameFromJIDWithDomain(this.JID), getAliasFromJID(this.JID));
        this.infoPacket.createDefault(arraylistNetworkMembers);

        this.msgPacket = new MessagePacket(getNameFromJIDWithDomain(this.JID));


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

    public void sendInfoToNeighbors(){
        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);
            sendInfoPackage(destination, 1, false);
        }

    }

    public void flood(){
        Set<String> networkMembers = namesConfig.keySet();
        ArrayList<String> arraylistNetworkMembers = new ArrayList<String>(networkMembers);

        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);
            sendInfoPackage(destination,arraylistNetworkMembers.size()-1 , false);
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

            //System.out.println("Logged in successfully!");
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }
    public void logout() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            //System.out.println("Logged out successfully!");
            this.isLoggedIn = false;
        }
    }

    private String getNeighborJID(String alias){
        return namesConfig.get(alias);
    }

    private AbstractXMPPConnection createConnection() {
        //System.out.println("Comienza a crear conexion");
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain("alumchat.xyz")
                    .setHost("146.190.213.97")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    //.enableDefaultDebugger()
                    .build();
            //System.out.println("Termina creacion de conexion");
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
        //System.out.println();
        String type = response.get("type").getAsString();
        JsonObject payload;

        switch (type) {
            case "echo" -> {
                //System.out.println("Recibio echo "+JID);
                echoResponseHandler(response);

            }
            case "message" -> {
                //System.out.println("Recibio message "+JID);
                messageResponseHandler(response);
            }
            case "info" -> {
                //System.out.println("Recibio info "+JID);
                infoResponseHandler(response);
            }
        }

    }

    private String getNameFromJIDWithDomain(String string){
        String[] stringSplited  = string.split("@", 2);
        return stringSplited[0];
    }
    private void echoResponseHandler(JsonObject response){
        //System.out.println(response.toString());
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
    private void reSendInfo(InfoPacket infoPacketR, String toJID, int hopCount){
        infoPacketR.setTo(toJID);
        infoPacketR.setHopCount(hopCount);
        sendMessage(getNameFromJIDWithDomain(toJID), infoPacketR.toString());
    }

    private HashMap<String, Long> parseJsonTable(JsonObject payload){
        HashMap<String, Long> hashJsonTable =  new HashMap<String, Long>();
        for (Map.Entry<String, String> entry: namesConfig.entrySet()){
            if (payload.has(entry.getKey())){
                hashJsonTable.put(entry.getKey(), payload.get(entry.getKey()).getAsLong());
            }

        }

        return hashJsonTable;
    }

    private void infoResponseHandler(JsonObject response){

        String from = response.get("headers").getAsJsonObject().get("from").getAsString();
        String to = response.get("headers").getAsJsonObject().get("to").getAsString();
        int hopCount = response.get("headers").getAsJsonObject().get("hop_count").getAsInt();
        HashMap<String, Long> othersTable = parseJsonTable(response.get("payload").getAsJsonObject());
        String aliasFrom = getAliasFromJID(from);

        InfoPacket recievedPacket = new InfoPacket(from, aliasFrom);
        Set<String> networkMembers = namesConfig.keySet();
        ArrayList<String> arraylistNetworkMembers = new ArrayList<String>(networkMembers);
        recievedPacket.createFromHash(aliasFrom, arraylistNetworkMembers, othersTable );

        boolean recievedBefore = this.saveTable(recievedPacket, aliasFrom);
        if (recievedBefore){
            System.out.println(this.JID + " recibio un paquete repetido de "+from);
        }

        if (mode.equals("dv")){
            infoPacket.updateTable(othersTable, aliasFrom);
        }

        hopCount -= 1;
        if (hopCount>0){
            for (String neighbor: neighbors){
                String neighborJID = getJIDFromAlias(neighbor);
                if (!from.equals(neighborJID)){
                    sendOthersInfoPackage(recievedPacket, neighborJID, hopCount, false);
                }


            }
        }

    }
    private void messageResponseHandler(JsonObject response){
        String from = response.get("headers").getAsJsonObject().get("from").getAsString();
        String to = response.get("headers").getAsJsonObject().get("to").getAsString();
        int hopCount = response.get("headers").getAsJsonObject().get("hop_count").getAsInt();
        String payload = response.get("payload").getAsString();

        if (to.equals(this.JID)){
            //System.out.println(to+" recibió de "+from+" tras "+hopCount+" saltos");
            //System.out.println(payload);
        }
        else{
            MessagePacket msgPacket = new MessagePacket(from, to, payload, hopCount+1);
            sendMessageUsingTable(to, msgPacket.toString());
        }
    }

    public void sendMessage(String toJID, String messageContent) {

        toJID = !toJID.contains("@alumchat.xyz") ? toJID+"@alumchat.xyz": toJID;
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

    public void sendMessageUsingTable(String toJID, String messageContent){
        toJID = !toJID.contains("@alumchat.xyz") ? toJID+"@alumchat.xyz": toJID;
        if (chatManager == null) {
            ChatManager.getInstanceFor(connection);
        }
        try {
            String aliasDestination = getAliasFromJID(toJID);
            Route routeToDestintion =  this.infoPacket.findRoute(aliasDestination);

            if (!routeToDestintion.isExist()){
                //System.out.println("No se encontro ruta desde "+JID+" a "+toJID);
                return;
            }

            String aliasNextHop = routeToDestintion.getNextHop();
            String JIDNextHop = getJIDFromAlias(aliasNextHop);
            // hola
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(JIDNextHop));
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

    public void sendInfoPackage(String toJID, int hopCount, boolean useRouting){
        infoPacket.setTo(toJID);
        infoPacket.setHopCount(hopCount);
        if (!useRouting) {
            sendMessage(getNameFromJIDWithDomain(toJID), infoPacket.toString());
            return;
        }
        sendMessageUsingTable(getNameFromJIDWithDomain(toJID), infoPacket.toString());

    }

    public void sendOthersInfoPackage(InfoPacket infoPacket, String toJID, int hopCount, boolean useRouting){
        infoPacket.setTo(toJID);
        infoPacket.setHopCount(hopCount);
        if (!useRouting) {
            sendMessage(getNameFromJIDWithDomain(toJID), infoPacket.toString());
            return;
        }
        sendMessageUsingTable(getNameFromJIDWithDomain(toJID), infoPacket.toString());
    }

    public void sendMessagePackage(String toJID, int hopCount, String body, boolean useRouting){
        msgPacket.setHopCount(hopCount);
        msgPacket.setTo(toJID);
        msgPacket.setBody(body);

        if(!useRouting){
            sendMessage(getNameFromJIDWithDomain(toJID), msgPacket.toString());
            return;
        }
        sendMessageUsingTable(getNameFromJIDWithDomain(toJID), msgPacket.toString());


    }

    public HashMap<String, InfoPacket> filtrNodesTables (ArrayList<String> interestNodes){
        HashMap<String, InfoPacket> nodesTableOfInterest = new HashMap<>();

        for (String node: interestNodes){
            if (this.tablesBuffer.containsKey(node)){
                nodesTableOfInterest.put(node, this.tablesBuffer.get(node));
            }
        }

        return nodesTableOfInterest;

    }

    public boolean saveTable(InfoPacket packetToSave, String alias){

        for (Map.Entry<String, InfoPacket> entry: this.tablesBuffer.entrySet()){
            if (alias.equals(entry.getKey())) {
                if (entry.getValue().isTheSame(packetToSave)) {
                    return false;
                }
            }
        }
        this.tablesBuffer.put(alias, packetToSave);
        return true;


    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
