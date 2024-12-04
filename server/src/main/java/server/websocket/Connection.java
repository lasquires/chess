package server.websocket;


import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String username;
    public Session session;
    public Integer gameID;

    public Connection(String username, Integer gameID, Session session) {
        this.gameID = gameID;
        this.session = session;
        this.username = username;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
//        session.getRemote().sendString(new Gson().toJson(msg));
    }
}