package server.websocket;


import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO = new DataAccess().getAuthDAO();

    public WebSocketHandler() throws DataAccessException {
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            // valid auth? get associated username else throw error

            String username = getUsername(command.getAuthToken());

            //put the session in the ConnectionManagerMap
            Integer gameID = command.getGameID();
            //TODO: make sure that the map is username: session (not gameID)
            connections.add(gameID, session);
//            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    connect(session, username, connectCommand);//(ConnectCommand) command);
                }
                case MAKE_MOVE -> {
                    makeMove(session, username, (MakeMoveCommand) command);
                }
                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
                case RESIGN -> resign(session, username, (ResignCommand) command);
                default -> throw new ResponseException(500, "unable to connect");
            }
        }
        catch (ResponseException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage(ServerMessage.ServerMessageType.ERROR));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage(ServerMessage.ServerMessageType.ERROR));
        }
    }

    private void sendMessage(RemoteEndpoint remote, ErrorMessage errorMessage) {
        try {
//            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
            remote.sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getUsername(String authToken) throws DataAccessException, ResponseException {
//        try {
            if (authToken == null || authDAO.getAuth(authToken) == null) {
                throw new ResponseException(500, "Invalid or missing auth token.");
            }
            return authDAO.getAuth(authToken).username();
//        } catch (Exception ex) {
//            throw new UnauthorizedException("Failed to validate auth token: " + ex.getMessage());
//        }
    }


    private void connect(Session session, String username, ConnectCommand command) {
        try{
            System.out.println("in connect()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        }
        //A user connected to the game as a player (black or white).
        // The notification message should include the player’s name and which side they are playing (black or white).

        //A user connected to the game as an observer. The notification message should include the observer’s name.
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try{
            System.out.println("in makeMove()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        }
        //A player made a move. The notification message should include the player’s name and a description
        // of the move that was made. (This is in addition to the board being updated on each player’s screen.)
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
        try{
            System.out.println("in leaveGame()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        }
        //A player left the game. The notification message should include the player’s name.
    }

    private void resign(Session session, String username, ResignCommand command) {
        try{
            System.out.println("in resign()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage(ServerMessage.ServerMessageType.ERROR);
        }
        //A player resigned the game. The notification message should include the player’s name.
    }


    //TODO: A player is in check. The notification message should include the player’s name (this notification is generated by the server).
    //TODO: A player is in checkmate. The notification message should include the player’s name (this notification is generated by the server).
//
//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(visitorName, session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    private void exit(String visitorName) throws IOException {
//        connections.remove(visitorName);
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(visitorName, notification);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast("", notification);
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
}