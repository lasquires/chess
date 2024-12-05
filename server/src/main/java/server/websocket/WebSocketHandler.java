package server.websocket;


import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO = new DataAccess().getAuthDAO();
    private final GameDAO gameDAO = new DataAccess().getGameDAO();

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
            if (authToken == null || authDAO.getAuth(authToken) == null) {
                throw new ResponseException(500, "Invalid or missing auth token.");
            }
            return authDAO.getAuth(authToken).username();
    }


    private void connect(Session session, String username, ConnectCommand command) {
        System.out.println("In connect(): username = " + username + ", gameID = " + command.getGameID());
        try{
            //curr gameData
            Integer gameID = command.getGameID();
            GameData gameData = new DataAccess().getGameDAO().getGame(gameID);
            if(gameData == null){
                ErrorMessage error = new ErrorMessage("Unable to connect: Invalid game ID.");
                sendMessage(session.getRemote(), error);
                return;
            }

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
            sendMessage(session.getRemote(), error);
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

    private Map<ChessPosition, String> ChessPositionMapper() {
        Map<ChessPosition, String> positionMap = new HashMap<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                String alphabetString = "-abcdefgh";
                Character c = alphabetString.charAt(col);
                String value = c + String.valueOf(row);
                positionMap.put(new ChessPosition(row, col), value);
            }
        }
        return positionMap;
    }

    private void makeMove(Session session, String username, MakeMoveCommand command) {
        try{
//            new LoadGameMessage(new GameData(1, null, null, "newgame", new ChessGame()));
            System.out.println("in makeMove()");
            Integer gameID = command.getGameID();
            ChessMove move = command.getMove();

            var positionMap = ChessPositionMapper();

            String startPos = positionMap.get(move.getStartPosition());
            String endPos = positionMap.get(move.getEndPosition());


            GameData gameData = gameDAO.getGame(gameID);

            ChessGame.TeamColor turn = gameData.game().getTeamTurn();//.toString();

            if (gameData.game().isGameOver()){
                ErrorMessage error = new ErrorMessage("Game has already ended");
                sendMessage(session.getRemote(), error);
                return;
            }


            String attackingPlayer = null;
            String defendingPlayer = null;
            if (turn == ChessGame.TeamColor.BLACK){
                attackingPlayer = gameData.blackUsername();
                defendingPlayer = gameData.whiteUsername();
            }
            if (turn == ChessGame.TeamColor.WHITE){
                attackingPlayer = gameData.whiteUsername();
                defendingPlayer = gameData.blackUsername();
            }


            if (!Objects.equals(username, attackingPlayer)){
                ErrorMessage error =  new ErrorMessage("It's not your turn.");
                sendMessage(session.getRemote(), error);
                return;
            }


            gameData.game().makeMove(move);


            connections.broadcast(gameID,null,new LoadGameMessage(gameData));


            connections.broadcast(gameID, username, new NotificationMessage(
                    turn + " moved from " + startPos + " to "+ endPos + "."));

            if (gameData.game().isInCheckmate(gameData.game().getTeamTurn())){

                NotificationMessage notificationMessage = new NotificationMessage(defendingPlayer+ " is in checkmate");
                connections.broadcast(gameID,null, notificationMessage);
                gameData.game().setGameOver();
                gameDAO.updateGame(gameData);
                return;

            }

            if (gameData.game().isInCheck(gameData.game().getTeamTurn())){
                NotificationMessage notificationMessage = new NotificationMessage(defendingPlayer+ " is in check");
                connections.broadcast(gameID,null, notificationMessage);
            }
            if (gameData.game().isInStalemate(gameData.game().getTeamTurn())){
                NotificationMessage notificationMessage = new NotificationMessage("Game has ended in stalemate.");
                connections.broadcast(gameID,null, notificationMessage);
                gameData.game().setGameOver();

            }

            gameDAO.updateGame(gameData);


        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Invalid move");
            sendMessage(session.getRemote(), error);
        }
        //A player made a move. The notification message should include the player’s name and a description
        // of the move that was made. (This is in addition to the board being updated on each player’s screen.)
    }

    private void leaveGame(Session session, String username, LeaveGameCommand command) {
        try{
            System.out.println("in leaveGame()");
            Integer gameID = command.getGameID();

            GameData gameData = gameDAO.getGame(gameID);
            String whiteUsername = gameData.whiteUsername();
            if (Objects.equals(username, whiteUsername)){
                whiteUsername = null;
            }

            String blackUsername = gameData.blackUsername();
            if (Objects.equals(blackUsername, username)){
                blackUsername = null;
            }


            GameData updatedGame = new GameData(gameID, whiteUsername, blackUsername, gameData.gameName(), gameData.game());
            gameDAO.updateGame(updatedGame);
            connections.remove(gameID, username);
            NotificationMessage notification = new NotificationMessage(username + " has left the game.");
            connections.broadcast(gameID, username, notification);


        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Invalid move");
            sendMessage(session.getRemote(), error);
        }
        //A player left the game. The notification message should include the player’s name.
    }

    private void resign(Session session, String username, ResignCommand command) {
        try{
            System.out.println("in setGameOver()");
            Integer gameID = command.getGameID();
            GameData gameData = gameDAO.getGame(gameID);
            if (!Objects.equals(username, gameData.whiteUsername()) && !Objects.equals(username, gameData.blackUsername())){
                ErrorMessage error =  new ErrorMessage("You can't resign as an observer.");
                sendMessage(session.getRemote(), error);
                return;
            }
            if(!gameData.game().isGameOver()){
                gameData.game().setGameOver();
                gameDAO.updateGame(gameData);
                NotificationMessage notificationMessage = new NotificationMessage(username + " resigned.");
                connections.broadcast(gameID, null, notificationMessage);
            }
            else{
                ErrorMessage error =  new ErrorMessage("Game is already over");
                sendMessage(session.getRemote(), error);
            }


        }
        catch (Exception ex){
            ErrorMessage error =  new ErrorMessage("Unable to setGameOver");
            sendMessage(session.getRemote(), error);
        }
        //A player resigned the game. The notification message should include the player’s name.
    }
}