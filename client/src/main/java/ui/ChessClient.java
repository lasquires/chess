package ui;

import exception.ResponseException;
import model.AuthData;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private static boolean isLoggedIn = false;
    private final String serverUrl;
    private String authToken = null;
    private State state = State.SIGNEDOUT;


    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl=serverUrl;

    }

    public String eval(String input) throws ResponseException {
//        try{
            var tokens = input.toLowerCase().split(" ");
            var cmd = tokens.length > 0 ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "quit" -> "quit";
                case "register" -> register(params);
//                case "login" -> login(params);
//                case "logout" -> logout();
//                case "create" -> createGame(params);
//                case "list" -> listGames();
//                case "play" -> playGame(params);
//                case "observe" -> observeGame(params);
                default -> help();
            };
//        }catch(ResponseException ex){
//            return ex.getMessage();
//        }


    }

    private String register(String... params) throws ResponseException {
        if (params.length < 3){
            throw new ResponseException(400, "Expected 3 params, "+ params.length + " given.");
        }

        String username = params[0];
        String password = params[1];
        String email = params[2];
        try {
            AuthData authData = server.register(username, password, email);
            authToken = authData.authToken();
            state = State.SIGNEDIN;
            return "Successfully registered.";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }



    public String help(){
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
        return "";
    }
}
