package ui;

import chess.ChessBoard;
import exception.ResponseException;
import model.AuthData;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

public class ChessClient{//} implements ServerMessageObserver{
    private String state = "[LOGGED_OUT]";
    private final ServerFacade server;
    private final ServerMessageObserver serverMessageObserver;
//    private WebSocketCommunicator webSocketCommunicator;
//    private ChessBoard chessBoard;
    private String authToken = null;



    public ChessClient(String serverUrl, ServerMessageObserver serverMessageObserver) {
        server = new ServerFacade(serverUrl, serverMessageObserver);
        this.serverMessageObserver = serverMessageObserver;
    }

    public String eval(String input) throws ResponseException {
//        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                default -> help();
            };
    }

    private String register(String... params) throws ResponseException {

        if (params.length != 3){
            throw new ResponseException(400, "Expected 3 arguments, "+ params.length + " given.");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            AuthData authData = server.register(username, password, email);
            authToken = authData.authToken();
            state = "[LOGGED_IN]";
            System.out.println("Logged in as "+ username+ "\n");
            return help();
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String login(String... params) throws ResponseException {
        if (params.length != 2){
            throw new ResponseException(400, "Expected 2 arguments, "+ params.length + " given.");
        }
        String username = params[0];
        String password = params[1];
        try {
            AuthData authData = server.login(username, password);
            authToken = authData.authToken();
            state = "[LOGGED_IN]";
            System.out.println("Logged in as "+ username+ "\n");
            return help();
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String logout() throws ResponseException {
        if (authToken == null){
            throw new ResponseException(400, "You are not signed in.");
        }
        server.logout(authToken);
        authToken = null;
        state = "[LOGGED_OUT]";
        return "logged out.";

    }
    private String createGame(String... params) throws ResponseException {

        if (authToken == null){
            throw new ResponseException(400, "Must be logged in to create a game");
        }
        String gameName = params[0];
        try{
            server.createGame(gameName, authToken);
            return "Successfully created.";

        }catch(ResponseException e){
            return "Error: " + e.getMessage();
        }
    }
    private String listGames() throws ResponseException {
        if (authToken == null){
            throw new ResponseException(400, "You are not signed in.");
        }
        try{
            return server.listGames(authToken);
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }
    private String joinGame(String... params) throws ResponseException {
        if (params.length != 2){
            throw new ResponseException(400, "Expected 2 arguments, "+ params.length + " given.");
        }
        if (authToken == null){
            throw new ResponseException(400, "You are not signed in.");
        }
        String gameID = params[0];
        String playerColor = params[1].toUpperCase();

        return server.joinGame(Integer.valueOf(gameID), playerColor, authToken);
    }
    private String observeGame(String... params) throws ResponseException {
        if (params.length != 1){
            throw new ResponseException(400, "Expected 1 argument, "+ params.length + " given.");
        }
        if (authToken == null){
            throw new ResponseException(400, "You are not signed in.");
        }
        String gameID = params[0];
        return server.observeGame(Integer.valueOf(gameID), authToken);
    }

    public String help(){
        if (authToken != null){
            return """
                    Options:
                    create <NAME> - a game
                    list - games
                    join <ID> [WHITE|BLACK] - a game
                    observe <ID> - a game
                    logout - when you are done
                    quit - playing chess
                    help - with possible commands
                    """;

        }
        else {
            return """
                    Options:
                    login <USERNAME> <PASSWORD> - existing user account
                    register <USERNAME> <PASSWORD> <EMAIL> - new user account
                    quit - Exit the program
                    help - with possible commands
                    """;
        }
    }
    public String getState(){
        return state;
    }

//    @Override
//    public void notify(ServerMessage message) {
//        System.out.println("Server Update: " + message);
//    }
//    @Override
//    public void notify(ServerMessage message) {
//        if (message instanceof NotificationMessage notification) {
//            System.out.println(notification.getMessage());
//        } else {
//            System.out.println("Unknown server message type: " + message);
//        }
//
//    }

}
