package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServerFacade{
    private final String serverUrl;
    private Map<Integer, Integer> clientGameIDMap;
    private WebSocketCommunicator webSocketCommunicator;
    private ServerMessageObserver serverMessageObserver;
    private Session session;
    private HttpCommunicator httpCommunicator;
//    private String currColor = null;


    public ServerFacade(String url, ServerMessageObserver serverMessageObserver) {
        serverUrl = url;
        this.httpCommunicator = new HttpCommunicator(serverUrl);
//        this.serverMessageObserver = serverMessageObserver;
        this.webSocketCommunicator = new WebSocketCommunicator(serverUrl, serverMessageObserver);
//        this.webSocketCommunicator.connect();
    }




    public AuthData register(String username, String password, String email) throws ResponseException {
        String path = "/user";
        UserData request = new UserData(username, password, email);
//        System.out.println(request.toString());
        return httpCommunicator.makeRequest("POST", path, request, AuthData.class, null);

    }

    public AuthData login(String username, String password) throws ResponseException {
        String path = "/session";
        UserData request = new UserData(username, password, null);
        return httpCommunicator.makeRequest("POST", path, request, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        httpCommunicator.makeRequest("DELETE", path, null, null, authToken);
    }

    public void createGame(String gameName, String authToken) throws ResponseException {

        String path = "/game";
        GameData request = new GameData(0,null, null, gameName, null);
        httpCommunicator.makeRequest("POST", path, request, null, authToken);

    }

    public String listGames(String authToken) throws ResponseException {
        String path = "/game";
        Object jsonObject = httpCommunicator.makeRequest("GET", path, null, Object.class, authToken);
        Gson gson = new Gson();
        String jsonString = gson.toJson(jsonObject);


        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();


        Map<String, Object> map = gson.fromJson(jsonString, mapType);

        List<LinkedTreeMap<String, Object>> gamesList = (List<LinkedTreeMap<String, Object>>) map.get("games");
        String outString = "";
        int index = 1;
        Map<Integer, Integer> clientMap = new HashMap<>();
        for (var game : gamesList){
            var id = (Double) game.get("gameID");

            clientMap.put(index, id.intValue());
            var gameName = game.get("gameName");
            String whiteUsername = "empty";
            String blackUsername = "empty";
            if (game.containsKey("whiteUsername")){
                whiteUsername = game.get("whiteUsername").toString();
            }
            if (game.containsKey("blackUsername")){
                blackUsername = game.get("blackUsername").toString();
            }

            outString += String.format("%d.\tGame name: %s \tWhite: %s\tBlack: %s\n", index, gameName, whiteUsername, blackUsername);
            index++;
        }
        clientGameIDMap = clientMap;
        return outString;
    }

    public String joinGame(Integer gameID, String playerColor, String authToken) throws ResponseException {
        String path = "/game";
        listGames(authToken);
        if (clientGameIDMap.get(gameID)==null) {
            throw new ResponseException(400, "Game with this ID not found");
        }
        playerColor = playerColor.toUpperCase();

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new ResponseException(400, "Invalid player color.");
        }

        Integer serverGameID = clientGameIDMap.get(gameID);
        JoinGameRequest request = new JoinGameRequest(serverGameID, playerColor);


        //added for websocket
        String message = new Gson().toJson(request);

        GameData gameData = httpCommunicator.makeRequest("PUT", path, request, GameData.class, authToken);

        if (webSocketCommunicator == null) {
            throw new IllegalStateException("WebSocket is not connected");
        }

        ConnectCommand connectCommand = new ConnectCommand(authToken, serverGameID);
        webSocketCommunicator.sendMessage(new Gson().toJson(connectCommand));

        return "joined "+ gameID + "successfully \n";
    }

    public String observeGame(Integer gameID, String authToken) throws ResponseException {
        listGames(authToken);
        if (clientGameIDMap.get(gameID)==null) {
            throw new ResponseException(400, "Game with this ID not found");
        }
        String path = "/game";

        Integer serverGameID = clientGameIDMap.get(gameID);
        ConnectCommand connectCommand = new ConnectCommand(authToken, serverGameID);
        NotificationMessage notification = new NotificationMessage("Observing game: " + serverGameID + "\n");
        webSocketCommunicator.sendMessage(new Gson().toJson(connectCommand));
        return "Now observing game: " + serverGameID + "\n";
    }

    public void leaveGame(String authToken, Integer gameID){
        Integer serverGameID = clientGameIDMap.get(gameID);
        LeaveGameCommand leaveGameCommand = new LeaveGameCommand(authToken, serverGameID);
        webSocketCommunicator.sendMessage(new Gson().toJson(leaveGameCommand));

    }
    public void makeMove(ChessMove move, Integer gameID, String authToken){
        Integer serverGameID = clientGameIDMap.get(gameID);
//        MakeMoveCommand makeMoveCommand = new MakeMoveCommand()
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, serverGameID, move);
        webSocketCommunicator.sendMessage((new Gson().toJson(makeMoveCommand)));

    }

    public void resign(String authToken, Integer gameID) {
        Integer serverGameID = clientGameIDMap.get(gameID);
        ResignCommand resignCommand = new ResignCommand(authToken, gameID);
        webSocketCommunicator.sendMessage(new Gson().toJson(resignCommand));
    }
}
