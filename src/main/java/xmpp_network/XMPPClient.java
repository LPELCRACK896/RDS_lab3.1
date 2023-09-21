package xmpp_network;

import com.google.gson.*;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.*;

import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;


public class XMPPClient {
    private static final String XMPP_SERVER = "alumchat.xyz";
    private static final int PORT = 5222;
    private static final String DOMAIN = "alumchat.xyz";
    private String username;
    private String password;
    private AbstractXMPPConnection connection;

    private String selectedNode;
    private Character node;

    private int[] tablaEnrutamiento;

    private Character[] namesTabla = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private Character[] enlaces;


    public XMPPClient() {
        connect();
    }

//=================================================================================================GENERAL CONECTION=================================================================================================

    /**
     * @return boolean
     */
    public boolean connect() {
        try {
            // Configuración de la conexión
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(username, password)
                    .setXmppDomain(DOMAIN)
                    .setHost(XMPP_SERVER)
                    .setPort(PORT)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

            // Creamos la conexión
            connection = new XMPPTCPConnection(config);

            connection.connect();
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener((from, message, chat) -> {
                String notification = message.getBody();
                if (notification != null) {
                    receiveMessage(notification);
                }
            });

            System.out.println("\nSuccessful connection to the XMPP server.\n");
            return true;
        } catch (XmppStringprepException e) {
            e.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + e.getMessage());
            return false;
        } catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + ex.getMessage());
            return false;
        }
    }

//=================================================================================================ACCOUNT SETS=================================================================================================

    /**
     * @param username
     * @param password
     * @return boolean
     */
    public boolean login(String username, String password) {
        try {
            connection.login(username, password);
            this.username = username;
            this.password = password;
            return true;
        } catch (SASLErrorException saslError) {
            System.err.println("Login failed: Invalid credentials.");
            return false;
        } catch (SmackException | IOException | XMPPException | InterruptedException ex) {
            ex.printStackTrace();
            System.err.println("Failed to establish connection to the XMPP server: " + ex.getMessage());
            return false;
        }
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    //=================================================================================================ROUTING DV=================================================================================================
    public int[] getTablaEnrutamiento(){
        return this.tablaEnrutamiento;
    }

    public Character[] getEnlaces(){
        return this.enlaces;
    }

    public String selectNode(boolean chatbox) {
        try {
            File file = new File("src/main/resources/names.txt");
            Scanner scanner = new Scanner(file);
            StringBuilder jsonData = new StringBuilder();

            while (scanner.hasNextLine()) {
                jsonData.append(scanner.nextLine());
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonData.toString());
            JSONObject config = (JSONObject) json.get("config");

            while (true) {
                System.out.println("\n---DISTANCE VECTOR---\nSeleccione un nodo de la lista: ");
                int nodeNumber = 1;
                Iterator<String> keys = config.keySet().iterator();

                while (keys.hasNext()) {
                    String key = keys.next();
                    System.out.println(nodeNumber + ". " + config.get(key));
                    nodeNumber++;
                }

                Scanner inputScanner = new Scanner(System.in);

                try {
                    System.out.print("Ingrese el número del nodo: ");
                    int selectedNode = inputScanner.nextInt();
                    if (selectedNode > 0 && selectedNode <= config.size()) {
                        keys = config.keySet().iterator();
                        String selectedKey = null;

                        for (int i = 0; i < selectedNode; i++) {
                            selectedKey = keys.next();
                        }
                        if(!chatbox){
                            this.selectedNode = config.get(selectedKey).toString();
                            this.node = selectedKey.charAt(0);
                        }
                        return config.get(selectedKey).toString();
                    } else {
                        System.out.println("Ingrese un número válido.");
                    }
                } catch (java.util.InputMismatchException e) {
                    System.out.println("Ingrese un número válido.");
                }
            }

        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        }

        return null; // Si ocurre un error, retorna null
    }

    public int[] tablaEnrutamiento(String email) {
        int[] arrayTopologia;
        try {
            File topoFile = new File("src/main/resources/topo.txt");
            File namesFile = new File("src/main/resources/names.txt");

            Scanner topoScanner = new Scanner(topoFile);
            Scanner namesScanner = new Scanner(namesFile);

            StringBuilder topoData = new StringBuilder();
            StringBuilder namesData = new StringBuilder();

            while (topoScanner.hasNextLine()) {
                topoData.append(topoScanner.nextLine());
            }

            while (namesScanner.hasNextLine()) {
                namesData.append(namesScanner.nextLine());
            }

            JSONParser parser = new JSONParser();
            JSONObject topoJson = (JSONObject) parser.parse(topoData.toString());
            JSONObject namesJson = (JSONObject) parser.parse(namesData.toString());

            JSONObject topologia = (JSONObject) topoJson.get("config");
            JSONObject keys = (JSONObject) namesJson.get("config");

            String graph = null;

            for (Object key : keys.keySet()) {
                if (keys.get(key).equals(email)) {
                    graph = key.toString();
                    break;
                }
            }

            List<String> graphFriends = (List<String>) topologia.get(graph);

            int numNodes = keys.size();
            arrayTopologia = new int[numNodes];
            this.enlaces = new Character[numNodes];
            Arrays.fill(arrayTopologia, 9999);
            Arrays.fill(this.enlaces, ' ');

            int graphIndex = -1;

            for (int i = 0; i < numNodes; i++) {
                String key = (String) keys.keySet().toArray()[i];
                if (key.equals(graph)) {
                    graphIndex = i;
                    break;
                }
            }

            if (graphIndex != -1) {
                arrayTopologia[graphIndex] = 0;

                for (int i = 0; i < numNodes; i++) {
                    String key = (String) keys.keySet().toArray()[i];
                    if (graphFriends.contains(key) && i != graphIndex) {
                        arrayTopologia[i] = 1;
                        this.enlaces[i] = key.charAt(0);
                    }
                }
            }

        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
            arrayTopologia = null;
        }

        this.tablaEnrutamiento = arrayTopologia;
        return this.tablaEnrutamiento;
    }

    public void broadcastMessage(){
        Map<Character, String> nombres = cargarNombresDesdeArchivo();
        for (int i = 0; i < this.tablaEnrutamiento.length; i++) {
            if (this.tablaEnrutamiento[i] == 1) {
                Character to = this.namesTabla[i];
                String destinatario = nombres.get(to);
                String mensaje = construirMensaje("info", this.node, this.namesTabla[i], 1, tablaEnrutamiento, null);
                sendMessage(destinatario, mensaje);

                //System.out.println(destinatario);
                //System.out.println("Mensaje enviado: " + mensaje); //Descomentar para ver que json se esta enviando
                System.out.println("Se envió actualización de nodo a: " + this.namesTabla[i]);
            }
        }
    }

    private String construirMensaje(String type, Character from, Character to, int hopCount, int[] tablaEnrutamiento, String message) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        // Agregar campos al JSON
        jsonBuilder.append("\"type\": \"").append(type).append("\", ");
        jsonBuilder.append("\"headers\": {");
        jsonBuilder.append("\"from\": \"").append(from).append("\", ");
        jsonBuilder.append("\"to\": \"").append(to).append("\", ");
        jsonBuilder.append("\"hop_count\": ").append(hopCount).append("}, ");
        if (message != null) {
            jsonBuilder.append("\"payload\": \"").append(message).append("\"");
        } else {
            jsonBuilder.append("\"payload\": \"").append(Arrays.toString(tablaEnrutamiento)).append("\"");
        }

        jsonBuilder.append("}");

        return jsonBuilder.toString();
    }

    // Método para cargar el archivo names.txt y crear un mapa de letras a direcciones de correo electrónico.
    private Map<Character, String> cargarNombresDesdeArchivo() {
        Map<Character, String> nombres = new HashMap<>();

        try {
            // Lee el archivo names.txt
            FileReader fileReader = new FileReader("src/main/resources/names.txt");
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(fileReader).getAsJsonObject();

            // Obtiene el objeto "config" del archivo JSON
            JsonObject configObject = jsonObject.getAsJsonObject("config");

            // Itera sobre las claves (A, B, C, ...) y sus valores (direcciones de correo)
            for (Map.Entry<String, JsonElement> entry : configObject.entrySet()) {
                char letra = entry.getKey().charAt(0);
                String direccion = entry.getValue().getAsString();
                nombres.put(letra, direccion);
            }

            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nombres;
    }



    public void updateTablaEnrutamiento(int[] distanceVect, Character from){
        boolean flagUpdate = false;
        for (int i = 0; i < distanceVect.length; i++) {
            if (distanceVect[i] == 1 && this.tablaEnrutamiento[i] == 9999) {
                this.enlaces[i] = from;
                this.tablaEnrutamiento[i] = distanceVect[i] + 1;
                flagUpdate = true;
            }else if(distanceVect[i] +1 < this.tablaEnrutamiento[i]){
                this.enlaces[i] = from;
                this.tablaEnrutamiento[i] = distanceVect[i] + 1;
                flagUpdate = true;
            }
        }
        if(!flagUpdate){
            System.out.println("Vector de distancia actualizado");
        }else{
            broadcastMessage();
        }
    }

//=================================================================================================MESSAGES=================================================================================================

    public void sendMessage(String contactJID, String messageBody) {
        if (connection != null && connection.isConnected()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(contactJID);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);
                Chat chat = chatManager.chatWith(jid);
                chat.send(messageBody);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error encountered while sending message: " + ex.getMessage());
            }
        }
    }

    public void chatMessage(String destinatario, String message){
        Map<Character, String> nombres = cargarNombresDesdeArchivo();
        int val = 0;
        int hops = 1;
        String dest = "";
        String salto = "";
        for (Map.Entry<Character, String> entry : nombres.entrySet()) {
            if (entry.getValue().equals(destinatario)) {
                dest = String.valueOf(entry.getKey());
                hops = this.tablaEnrutamiento[val];
                for (Map.Entry<Character, String> jumpentry : nombres.entrySet()) {
                    if (jumpentry.getKey().equals(this.enlaces[val])) {
                        salto = jumpentry.getValue();
                    }
                }
            }
            val +=1;
        }
        System.out.println(dest.charAt(0) + salto);
        String mensaje = construirMensaje("message", this.node, dest.charAt(0), hops, null, message);
        sendMessage(salto, mensaje);
    }

    public void receiveMessage(String notification){
        // Parsear la cadena JSON
        JsonElement jsonElement = JsonParser.parseString(notification);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Character from = 'A';

        if(jsonObject.get("type").getAsString().equals("info")){
            // Obtener el campo "payload" como un arreglo de enteros
            String elemento = jsonObject.get("payload").getAsString();
            elemento = elemento.replaceAll("\\[|\\]|\\s", "");
            String[] elementos = elemento.split(",");
            int[] enteros = new int[elementos.length];
            for (int i = 0; i < elementos.length; i++) {
                enteros[i] = Integer.parseInt(elementos[i]);
            }
            for (int i = 0; i < enteros.length; i++) {
                int numero = enteros[i];
                if (numero == 0) {
                    from = namesTabla[i];
                }
            }
            updateTablaEnrutamiento(enteros, from);

        }else if(jsonObject.get("type").getAsString().equals("message") || jsonObject.get("type").getAsString().equals("chat")){
            if(jsonObject.getAsJsonObject("headers").get("hop_count").getAsInt() - 1 == 0){
                System.out.println("\n Mensaje recibido de " + jsonObject.getAsJsonObject("headers").get("from").getAsString() + ": " + jsonObject.get("payload").getAsString());
            }else{
                System.out.println("\n Nodo de salto para mensaje de " + jsonObject.getAsJsonObject("headers").get("from").getAsString() + " hacia " + jsonObject.getAsJsonObject("headers").get("to").getAsString());
                Map<Character, String> nombres = cargarNombresDesdeArchivo();
                for (int i = 0; i < this.namesTabla.length; i++) {
                    if (this.namesTabla[i].equals(jsonObject.getAsJsonObject("headers").get("to").getAsString().charAt(0))) {
                        Character to = this.enlaces[i];
                        String destinatario = nombres.get(to);
                        String m = construirMensaje("message", jsonObject.getAsJsonObject("headers").get("from").getAsString().charAt(0), jsonObject.getAsJsonObject("headers").get("to").getAsString().charAt(0), jsonObject.getAsJsonObject("headers").get("hop_count").getAsInt() - 1, null, jsonObject.get("payload").getAsString());
                        sendMessage(destinatario, m);
                    }
                }
            }
        }

    }

//=================================================================================================LISTENERS=================================================================================================

    /**
     * @param listener
     */
    public void registerMessageListener(StanzaListener listener) {
        if (connection != null && connection.isConnected()) {
            connection.addAsyncStanzaListener(listener, MessageTypeFilter.NORMAL);
        }
    }
}

//=================================================================================================EXTRA=================================================================================================

//class Mensaje {
//    private String type;
//    private Headers headers;
//    private int[] payload;
//
//    public Mensaje(String type, String from, String to, int hopCount, int[] payload) {
//        this.type = type;
//        this.headers = new Headers(from, to, hopCount);
//        this.payload = payload;
//    }
//}
//
//class ChatMessage {
//    private String type;
//    private Headers headers;
//    private String payload;
//
//    public ChatMessage(String type, String from, String to, int hopCount, String payload) {
//        this.type = type;
//        this.headers = new Headers(from, to, hopCount);
//        this.payload = payload;
//    }
//}
//
//class Headers {
//    private String from;
//    private String to;
//    private int hop_count;
//
//    public Headers(String from, String to, int hop_count) {
//        this.from = from;
//        this.to = to;
//        this.hop_count = hop_count;
//    }
//}
