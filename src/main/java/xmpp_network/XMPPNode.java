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
/**
 * @author LPELCRACK896
 */
public class XMPPNode {
    private String JID;
    private String mode;
    private String alias;
    private boolean isLoggedIn;
    private InfoPacket infoPackage;
    private List<String> neighbors;
    private ChatManager chatManager;
    private MessagePacket msgPacket;
    private ArrayList<String> networkMembers;
    private AbstractXMPPConnection connection;
    private HashMap<String, String> namesConfig;
    private HashMap<String, String> dijkstraTable;
    private HashMap<String, InfoPacket> tablesBuffer;
    private HashMap<String, List<String>> topologyConfig;

    /**
     * Constructor
     * @param JID JID in XMPP Server
     * @param password Password of JID
     * @param topologyConfig Topology representation
     * @param namesConfig Hashmap with node names (alias) and its corresponding JID.
     * @param mode Mode of routing (either "dv" or "lsr")
     */
    public XMPPNode (String JID, String password, HashMap<String, List<String>> topologyConfig, HashMap<String, String> namesConfig, String mode) {
        this.JID = JID;
        this.mode = mode; // Either "dv" or "lsr"
        this.isLoggedIn = false;
        this.namesConfig = namesConfig;
        this.chatManager = null;
        this.tablesBuffer = new HashMap<String, InfoPacket>();
        this.dijkstraTable = new HashMap<String, String>();
        this.topologyConfig = topologyConfig;
        this.networkMembers = extractNetworkMembers();
        this.alias = figureOutOwnAlias();
        this.neighbors = figureOutNeighbors();
        this.connection = createConnection();


        this.infoPackage = new InfoPacket(getNameFromJIDWithDomain(this.JID), getAliasFromJID(this.JID));
        this.infoPackage.createDefault(networkMembers);
        this.msgPacket = new MessagePacket(getNameFromJIDWithDomain(this.JID));

        if (connection != null) {
            login(JID, password);
            this.chatManager = ChatManager.getInstanceFor(connection);
        }
    }

    /*
     * #################
     * #################
     * MAIN METHODS
     * #################
     * #################
     */

    /**
     * Start the nodes by sending echo packets to ping neighbors
     */
    public void configureNode(){
        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);
            EchoPacket echoPacket = new EchoPacket(JID, destination);
            xmppChatDirect(destination, echoPacket.toString());
        }
    }

    /**
     * Send routing table to neighbors
     */
    public void sendInfoToNeighbors(){
        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);
            sendInfoPackage(destination, 1, false);
        }
    }

    /**
     * Sends the table to the neighbors with hops enough to flood the network.
     */
    public void flood(){
        Set<String> networkMembers = namesConfig.keySet();
        ArrayList<String> arraylistNetworkMembers = new ArrayList<String>(networkMembers);

        for (String neighbor: neighbors){
            String destination = getJIDFromAlias(neighbor);
            sendInfoPackage(destination,arraylistNetworkMembers.size()-1 , false);
        }
    }

    /**
     * Logs in XMPP server
     * @param JID JID to log in.
     * @param password password to log in.
     */
    public void login(String JID, String password) {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.login(JID, password);
            this.isLoggedIn = true;

        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    /**
     * Logout from the server.
     */
    public void logout() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            this.isLoggedIn = false;
        }
    }

    /**
     * Creates connection to XMPP server
     * @return The connection
     */
    private AbstractXMPPConnection createConnection() {
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain("alumchat.xyz")
                    .setHost("146.190.213.97")
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    //.enableDefaultDebugger()
                    .build();

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

    /**
     * Method that will run on receiving packaged (used as listener)
     * @param response The message received from some other node.
     */
    private void executeResponse(JsonObject response) {
        String type = response.get("type").getAsString();
        switch (type) {
            case "echo" ->  echoResponseHandler(response);
            case "message" -> messageResponseHandler(response);
            case "info" -> infoResponseHandler(response);
        }
    }

    /**
     * Handle messages of type echo
     * @param response The message received from some other node.
     */
    private void echoResponseHandler(JsonObject response){
        JsonObject payload = response.get("payload").getAsJsonObject();
        JsonObject headers = response.get("headers").getAsJsonObject();

        String sender = getNameFromJIDWithDomain(headers.get("from").getAsString());
        String reciever = getNameFromJIDWithDomain(headers.get("to").getAsString());

        boolean hasTimestamp2 = payload.has("timestamp2");
        // Receives as a ping response
        if (hasTimestamp2) {
            long timeStamp1 = payload.get("timestamp1").getAsLong();
            long timeStamp2 = payload.get("timestamp2").getAsLong();
            long difference = timeStamp2 - timeStamp1;
            String alias = getAliasFromJID(sender);
            boolean isNeighbor =  neighbors.contains(alias);
            this.infoPackage.editARoute(alias, alias, difference, isNeighbor);
            return;
        }

        long timeStamp1 = payload.get("timestamp1").getAsLong();
        EchoPacket echoPacket = new EchoPacket(reciever, sender, timeStamp1);
        xmppChatDirect(sender, echoPacket.toString());

    }

    /**
     * Handles messages of type info
     * @param response The message received from some other node.
     */
    private void infoResponseHandler(JsonObject response){

        String from = response.get("headers").getAsJsonObject().get("from").getAsString();
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
            infoPackage.updateTable(othersTable, aliasFrom);
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

    /**
     * Handles messages of type message
     * @param response The message received from some other node.
     */
    private void messageResponseHandler(JsonObject response){
        String from = response.get("headers").getAsJsonObject().get("from").getAsString();
        String to = response.get("headers").getAsJsonObject().get("to").getAsString();
        int hopCount = response.get("headers").getAsJsonObject().get("hop_count").getAsInt();
        String payload = response.get("payload").getAsString();

        if (to.equals(this.JID)){
            System.out.println(to+" recibió de "+from+" tras "+hopCount+" saltos");
            System.out.println(payload);
        }
        else{
            System.out.println("Mensage redirigido a "+to+" desde "+this.JID);
            MessagePacket msgPacket = new MessagePacket(from, to, payload, hopCount+1);
            xmppChatUsingTable(to, msgPacket.toString());
        }
    }

    /**
     * Sends messages directly to some other JID
     * @param toJID target JID node.
     * @param body content of message.
     */
    public void xmppChatDirect(String toJID, String body) {
        toJID = !toJID.contains("@alumchat.xyz") ? toJID+"@alumchat.xyz": toJID;
        if (chatManager == null) {
            ChatManager.getInstanceFor(connection);
        }
        try {
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(toJID));
            chat.send(body);
        } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Sends messages to some other JID, not directly but through the route that was calculated.
     * @param toJID target JID node.
     * @param body content of message.
     */
    public void xmppChatUsingTable(String toJID, String body){
        toJID = !toJID.contains("@alumchat.xyz") ? toJID+"@alumchat.xyz": toJID;
        if (chatManager == null) {
            ChatManager.getInstanceFor(connection);
        }
        try {
            String aliasDestination = getAliasFromJID(toJID);
            Route routeToDestintion =  this.infoPackage.findRoute(aliasDestination);
            if (!routeToDestintion.isExist()){
                return;
            }
            String aliasNextHop = routeToDestintion.getNextHop();
            String JIDNextHop = getJIDFromAlias(aliasNextHop);
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(JIDNextHop));
            chat.send(body);
        } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    /**
     * Sends own package to a certain JID.
     * @param toJID target JID.
     * @param hopCount limit number of hops.
     * @param useRouting indicates if it will use the routing calculated.
     */
    public void sendInfoPackage(String toJID, int hopCount, boolean useRouting){
        infoPackage.setTo(toJID);
        infoPackage.setHopCount(hopCount);
        if (!useRouting) {
            xmppChatDirect(getNameFromJIDWithDomain(toJID), infoPackage.toString());
            return;
        }
        xmppChatUsingTable(getNameFromJIDWithDomain(toJID), infoPackage.toString());

    }

    /**
     * Sends some other info package to a certain JID.
     * @param infoPacket the other infoPacket to send.
     * @param toJID target JID.
     * @param hopCount limit number of hops.
     * @param useRouting indicates if it will use the routing calculated.
     */
    public void sendOthersInfoPackage(InfoPacket infoPacket, String toJID, int hopCount, boolean useRouting){
        infoPacket.setTo(toJID);
        infoPacket.setHopCount(hopCount);
        if (!useRouting) {
            xmppChatDirect(getNameFromJIDWithDomain(toJID), infoPacket.toString());
            return;
        }
        xmppChatUsingTable(getNameFromJIDWithDomain(toJID), infoPacket.toString());
    }

    public void sendMessagePackage(String toJID, int hopCount, String body, boolean useRouting){
        msgPacket.setHopCount(hopCount);
        msgPacket.setTo(toJID);
        msgPacket.setBody(body);

        if(!useRouting){
            xmppChatDirect(getNameFromJIDWithDomain(toJID), msgPacket.toString());
            return;
        }
        xmppChatUsingTable(getNameFromJIDWithDomain(toJID), msgPacket.toString());


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


    /*
     * #################
     * #################
     * Auxiliary methods
     * #################
     * #################
     */

    /**
     * Based on name topology and its own JID figure out its name on graph.
     * @return Alias of JID on network.
     */
    private String figureOutOwnAlias(){
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if ((this.JID+"@alumchat.xyz").equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Checks topology and find out the neighbors
     * @return List of the aliases of the neighbors of this node.
     */
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

    /**
     * Based on the JID gets the alias on network.
     * @param JID JID to get node alias.
     * @return The alias on network.
     */
    public String getAliasFromJID(String JID){
        JID =!JID.contains("@alumchat.xyz") ? JID+"@alumchat.xyz": JID;
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if (entry.getValue().equals(JID)){
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Turns a String JSON table routing into a usable hashmap.
     * @param payload payload containing the routing table
     * @return Hashmap representing the routing table.
     */
    private HashMap<String, Long> parseJsonTable(JsonObject payload){
        HashMap<String, Long> hashJsonTable =  new HashMap<String, Long>();
        for (Map.Entry<String, String> entry: namesConfig.entrySet()){
            if (payload.has(entry.getKey())){
                hashJsonTable.put(entry.getKey(), payload.get(entry.getKey()).getAsLong());
            }
        }
        return hashJsonTable;
    }

    /**
     * Splits complete JID (containing the XMPP domain) to only get the JID.
     * @param string Complete JID including @ >domain>
     * @return Only JID name
     */
    private String getNameFromJIDWithDomain(String string){
        String[] stringSplited  = string.split("@", 2);
        return stringSplited[0];
    }

    /**
     * Based on alias gets the JID. Using names topology.
     * @param alias Alias node name.
     * @return JID.
     */
    public String getJIDFromAlias(String alias){
        for (Map.Entry<String, String> entry : namesConfig.entrySet()) {
            if (entry.getKey().equals(alias)){
                return getNameFromJIDWithDomain(entry.getValue());
            }
        }
        return null;
    }

    /**
     * Hashmap containing the infoPacket of given nodes.
     * @param interestNodes Nodes
     * @return Hashmap containing only the info of the given nodes.
     */
    public HashMap<String, InfoPacket> filtrNodesTables (ArrayList<String> interestNodes){
        HashMap<String, InfoPacket> nodesTableOfInterest = new HashMap<>();

        for (String node: interestNodes){
            if (this.tablesBuffer.containsKey(node)){
                nodesTableOfInterest.put(node, this.tablesBuffer.get(node));
            }
        }
        return nodesTableOfInterest;
    }

    /**
     * From namesTopology extract the network members aliases.
     * @return List of network members.
     */
    private ArrayList<String> extractNetworkMembers(){
        Set<String> networkMembersKeySet = namesConfig.keySet();
        return new ArrayList<String>(networkMembersKeySet);
    }

    /*
     * #################
     * #################
     * SETTERS AND GETTERS
     * #################
     * #################
     */

    /**
     * GET: infoPackage
     * @return attr: infoPackage
     */
    public InfoPacket getInfoPackage() {
        return infoPackage;
    }

    /**
     * SETTER: Info package
     * @param infoPackage new info package
     */
    public void setInfoPackage(InfoPacket infoPackage) {
        this.infoPackage = infoPackage;
    }

    /**
     * GET: mode
     * @return attr: mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * SETTER mode
     * @param mode new mode
     */
    public void setMode(String mode) {
        this.mode = mode;
    }
}
