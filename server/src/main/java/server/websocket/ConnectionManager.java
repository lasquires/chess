package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;


public class ConnectionManager {
//    public final ConcurrentHashMap<Integer, List<Connection>> connections = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(String username, Integer gameID, Session session) {
        var connection = new Connection(username, gameID, session);
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(username, connection);
//        connections.computeIfAbsent(gameID, k-> new ArrayList<>()).add(connection);//put(gameID, connection);
    }

    public void remove(Integer gameID, String username) {
//        connections.remove(gameID);
        var gameConnections = connections.get(gameID);

        if (gameConnections != null) {
            gameConnections.remove(username);

            if (gameConnections.isEmpty()) {
                connections.remove(gameID);
            }
        }

    }


    public void broadcast(Integer targetID, String excludeUserName, ServerMessage message) throws IOException {
        var gameConnections = connections.get(targetID);

        if (gameConnections == null || gameConnections.isEmpty()) {
            return;
        }

        var removeList = new ArrayList<String>();

        for (var entry : gameConnections.entrySet()){
            String username = entry.getKey();
            Connection connection = entry.getValue();

            if (!username.equals(excludeUserName)){
                if (connection.session.isOpen()){
                    connection.send(new Gson().toJson(message));
                }
                else{
                    removeList.add(username);
                }
            }

        }
        for (String username : removeList) {
            gameConnections.remove(username);
        }
        if (gameConnections.isEmpty()) {
            connections.remove(targetID);
        }
    }
}