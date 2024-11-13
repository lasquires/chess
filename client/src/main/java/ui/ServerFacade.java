package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;
    private Map<Integer, Integer> clientGameIDMap;
    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData register(String username, String password, String email) throws ResponseException {
        String path = "/user";
        UserData request = new UserData(username, password, email);
//        System.out.println(request.toString());
        return this.makeRequest("POST", path, request, AuthData.class, null);

    }

    public AuthData login(String username, String password) throws ResponseException {
        String path = "/session";
        UserData request = new UserData(username, password, null);
        return this.makeRequest("POST", path, request, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        String path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
        System.out.println("Logged out successfully.");
    }

    public void createGame(String gameName, String authToken) throws ResponseException {
        String path = "/game";
        GameData request = new GameData(0,null, null, gameName, null);
        this.makeRequest("POST", path, request, null, authToken);

    }

    public String listGames(String authToken) throws ResponseException {
        String path = "/game";
        Object jsonObject = this.makeRequest("GET", path, null, Object.class, authToken);
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

            clientMap.put(index, id.intValue()); //TODO, see if this causes problems as an Int
            var gameName = game.get("gameName");
            String whiteUsername = "empty";
            String blackUsername = "empty";
            if (game.containsKey("whiteUsername")){
                whiteUsername = game.get("whiteUsername").toString();
            }
            if (game.containsKey("blackUsername")){
                whiteUsername = game.get("blackUsername").toString();
            }
            outString += String.format("%d.\tGame name: %s \tWhite: %s\tBlack: %s\n", index, gameName, whiteUsername, blackUsername);
            index++;
        }
        clientGameIDMap = clientMap;
        return outString;
    }

    public String joinGame(Integer gameID, String playerColor, String authToken) throws ResponseException {
        String path = "/game";
        if (clientGameIDMap == null){
            listGames(authToken);
        }
        JoinGameRequest request = new JoinGameRequest(clientGameIDMap.get(gameID), playerColor);
        //TODO get the type that this needs to return
        GameData gameData = this.makeRequest("PUT", path, request, GameData.class, authToken);
        return buildBoard(new GameData(0, null, null, null, new ChessGame()));
    }

    public String observeGame(Integer gameID, String authToken) throws ResponseException {
        String path = "/game";
        if (clientGameIDMap == null){
            listGames(authToken);
        }
        Integer request = clientGameIDMap.get(gameID);
        //TODO figure out observe implementation
        return "Game being observed";//this.makeRequest("PUT", path, request, GameData.class, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null){
                http.setRequestProperty("authorization", authToken);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

    private String buildBoard(GameData gameData){
        ChessBoard chessBoard = gameData.game().getBoard();
        StringBuilder sb = new StringBuilder();

        sb.append("    a   b   c   d   e   f   g   h\n");

        for (int row = 1; row <= 8; row++) {
            // Print rank label
            sb.append(8 - row).append(" ");
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = chessBoard.getPiece(position);
                boolean isLightSquare = (row + col) % 2 == 0;

                // Set background color
                if (isLightSquare) {
                    sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    sb.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                if (piece == null){
                    sb.append(EscapeSequences.EMPTY);
                    sb.append(EscapeSequences.RESET_BG_COLOR);
                    continue;
                }
                // Append piece symbol or empty space
                switch (piece.getTeamColor()) {
                    case WHITE:
                        switch (piece.getPieceType()) {
                            case KING:
                                sb.append(EscapeSequences.WHITE_KING);
                                break;
                            case QUEEN:
                                sb.append(EscapeSequences.WHITE_QUEEN);
                                break;
                            case BISHOP:
                                sb.append(EscapeSequences.WHITE_BISHOP);
                                break;
                            case KNIGHT:
                                sb.append(EscapeSequences.WHITE_KNIGHT);
                                break;
                            case ROOK:
                                sb.append(EscapeSequences.WHITE_ROOK);
                                break;
                            case PAWN:
                                sb.append(EscapeSequences.WHITE_PAWN);
                                break;
                            default:
                                sb.append(EscapeSequences.EMPTY);
                                break;
                        }
                        break;

                    case BLACK:
                        switch (piece.getPieceType()) {
                            case KING:
                                sb.append(EscapeSequences.BLACK_KING);
                                break;
                            case QUEEN:
                                sb.append(EscapeSequences.BLACK_QUEEN);
                                break;
                            case BISHOP:
                                sb.append(EscapeSequences.BLACK_BISHOP);
                                break;
                            case KNIGHT:
                                sb.append(EscapeSequences.BLACK_KNIGHT);
                                break;
                            case ROOK:
                                sb.append(EscapeSequences.BLACK_ROOK);
                                break;
                            case PAWN:
                                sb.append(EscapeSequences.BLACK_PAWN);
                                break;
                            default:
                                sb.append(EscapeSequences.EMPTY);
                                break;
                        }
                        break;

                    default:
                        sb.append(EscapeSequences.EMPTY);
                        break;
                }

                // Reset background color
                sb.append(EscapeSequences.RESET_BG_COLOR);
            }
            sb.append(" ").append(8 - row).append("\n");
        }

        // Print file labels again
        sb.append("    a   b   c   d   e   f   g   h\n");

        return sb.toString();

    }

}
