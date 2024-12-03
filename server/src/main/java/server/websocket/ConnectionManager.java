package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;


public class ConnectionManager {
    //TODO: make sure that the map is username: session (not gameID)
    public final ConcurrentHashMap<Integer, List<Connection>> connections = new ConcurrentHashMap<>();

    public void add(String username, Integer gameID, Session session) {
        var connection = new Connection(username, gameID, session);
        connections.computeIfAbsent(gameID, k-> new ArrayList<>()).add(connection);//put(gameID, connection);
    }

    public void remove(Integer gameID) {
        connections.remove(gameID);
    }


    public void broadcast(Integer targetID, String excludeUserName, ServerMessage message) throws IOException {
        var connectionList = connections.get(targetID);

        if (connectionList == null || connectionList.isEmpty()) {
            return;
        }

        var removeList = new ArrayList<Connection>();

        for (var c : connectionList) {
            if (c.session.isOpen()) {
                if (!c.username.equals(excludeUserName)) {
                    c.send(new Gson().toJson(message));//message.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        //remove what wasn't caught
        connectionList.removeAll(removeList);
        // Clean up any connections that were left open.
        if (connectionList.isEmpty()) {
            connections.remove(targetID);
        }
    }
}