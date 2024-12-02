package server;

import dataaccess.DataAccessException;
import handler.*;
import server.websocket.WebSocketHandler;
import spark.*;


public class Server {

    private final WebSocketHandler webSocketHandler;

    public Server() {
        try {
            this.webSocketHandler = new WebSocketHandler();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Test this
        Spark.webSocket("/ws", webSocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());
        Spark.get("/game", new ListGameHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game", new JoinGameHandler());
        Spark.delete("/db", new ClearHandler());

//        //This line initializes the server and can be removed once you have a functioning endpoint
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
