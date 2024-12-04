package server.websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO = new DataAccess().getAuthDAO();
//    private final GameDAO gameDAO = new DataAccess().getGameDAO();

    public WebSocketHandler() throws DataAccessException {
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("in onMessage()");
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

            // valid auth? get associated username else throw error

            String username = getUsername(command.getAuthToken());

            //put the session in the ConnectionManagerMap
            Integer gameID = command.getGameID();
            //TODO: make sure that the map is username: session (not gameID)
            connections.add(username, gameID, session);
//            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    System.out.println("Received command: " + command.getCommandType());
                    ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    connect(session, username, connectCommand);//(ConnectCommand) command);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(session, username, makeMoveCommand);
                }
                case LEAVE -> {
                    LeaveGameCommand leaveGameCommand = new Gson().fromJson(message, LeaveGameCommand.class);
                    leaveGame(session, username, leaveGameCommand);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    resign(session, username, resignCommand);
                }
                default -> throw new ResponseException(500, "unable to connect");
            }
        }
        catch (ResponseException ex) {
            // Serializes and sends the error message
            sendMessage(session.getRemote(), new ErrorMessage("Invalid Session"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session.getRemote(), new ErrorMessage("Unknown error occured"));
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
        System.out.println("In connect(): username = " + username + ", gameID = " + command.getGameID());
        try{
            //curr gameData
            GameData gameData = new DataAccess().getGameDAO().getGame(command.getGameID());

            //send loaded game to player
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData);
            session.getRemote().sendString(new Gson().toJson(loadGameMessage));

            //notify other players
            String role = findRole(username, gameData);
            connections.add(username, command.getGameID(), session);
            String message = username + " connected to the game " + role;
            NotificationMessage notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), username, notification);

            System.out.println("User " + username + " connected to game ID " + command.getGameID() + role);
//            System.out.println(command.getGameID());
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Unable to connect");
        }
        //A user connected to the game as a player (black or white).
        // The notification message should include the player’s name and which side they are playing (black or white).

        //A user connected to the game as an observer. The notification message should include the observer’s name.
    }

    private String findRole(String username, GameData gameData) throws DataAccessException {
//        GameData gameData = new DataAccess().getGameDAO().getGame(gameID);
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if (whiteUsername != null && whiteUsername.equals(username)){
            return "as white.";
        }
        if (blackUsername != null && blackUsername.equals(username)){
            return "as black.";
        }
        return "as an observer.";
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try{
            new LoadGameMessage(new GameData(1, null, null, "newgame", new ChessGame()));
            System.out.println("in makeMove()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("unable to make move");
        }
        //A player made a move. The notification message should include the player’s name and a description
        // of the move that was made. (This is in addition to the board being updated on each player’s screen.)
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
        try{
            System.out.println("in leaveGame()");
            Integer gameID = command.getGameID();

            connections.remove(gameID, username);
            NotificationMessage notification = new NotificationMessage(username + " has left the game.");
            connections.broadcast(gameID, username, notification);
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Unable to leave game");
        }
        //A player left the game. The notification message should include the player’s name.
    }

    private void resign(Session session, String username, ResignCommand command) {
        try{
            System.out.println("in resign()");
        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Unable to resign");
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