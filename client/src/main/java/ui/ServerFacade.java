package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.JoinGameRequest;
import model.UserData;
import websocket.commands.ConnectCommand;
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


        if (webSocketCommunicator == null) {
            throw new IllegalStateException("WebSocket is not connected");
        }

        ConnectCommand connectCommand = new ConnectCommand(authToken, serverGameID);
        NotificationMessage notification = new NotificationMessage("Observing game: " + serverGameID);
        webSocketCommunicator.sendMessage(new Gson().toJson(connectCommand));
//        webSocketCommunicator.sendMessage(message);


        return "joined "+ gameID + "successfully \n";
//        //In the future fix this
//        GameData gameData = httpCommunicator.makeRequest("PUT", path, request, GameData.class, authToken);
//        return new Render(new ChessGame().getBoard(), Color.BLACK).getRender();//buildBoards(new GameData(0, null, null, null, new ChessGame()));
    }

    public String observeGame(Integer gameID, String authToken) throws ResponseException {
        listGames(authToken);
        if (clientGameIDMap.get(gameID)==null) {
            throw new ResponseException(400, "Game with this ID not found");
        }
        String path = "/game";

        Integer serverGameID = clientGameIDMap.get(gameID);

        ConnectCommand connectCommand = new ConnectCommand(authToken, serverGameID);
        NotificationMessage notification = new NotificationMessage("Observing game: " + serverGameID);
        webSocketCommunicator.sendMessage(new Gson().toJson(connectCommand));
        return "Now observing game: " + serverGameID + "\n";
        //future change w websocket?
//        return new Render(new ChessGame().getBoard(), Color.BLACK).getRender();//buildBoards(new GameData(0, null, null, null, new ChessGame()));
    }

//    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
//        try {
//            URL url = (new URI(serverUrl + path)).toURL();
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            http.setRequestMethod(method);
//            http.setDoOutput(true);
//
//            if (authToken != null){
//                http.setRequestProperty("authorization", authToken);
//            }
//            writeBody(request, http);
//            http.connect();
//
//            throwIfNotSuccessful(http);
//
//
//            return readBody(http, responseClass);
//
//
//        } catch (Exception ex) {
//            throw new ResponseException(500, ex.getMessage());
//        }
//    }
//
//    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
//        if (request != null) {
//            http.addRequestProperty("Content-Type", "application/json");
//            String reqData = new Gson().toJson(request);
//            try (OutputStream reqBody = http.getOutputStream()) {
//                reqBody.write(reqData.getBytes());
//            }
//        }
//    }
//
//    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
//        var status = http.getResponseCode();
//        if (!isSuccessful(status)) {
//            String errorMessage = readErrorBody(http);
//            throw new ResponseException(status, errorMessage);
////            throw new ResponseException(status, "failure: " + status);
//        }
//    }
//
//    private String readErrorBody(HttpURLConnection http) throws IOException {
//        String errorMessage = "No error information provided";
//        try (InputStream errorStream = http.getErrorStream()) {
//            if (errorStream != null) {
//
//                InputStreamReader reader = new InputStreamReader(errorStream);
//                errorMessage = reader.toString();
//                Map<String, Object> errorMap = new Gson().fromJson(reader, Map.class);
//                errorMessage = (String) errorMap.getOrDefault("message", errorMessage);
//            }
//        }
//        return errorMessage;
//    }
//
//    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
//        T response = null;
//        if (http.getContentLength() < 0) {
//            try (InputStream respBody = http.getInputStream()) {
//                InputStreamReader reader = new InputStreamReader(respBody);
//                if (responseClass != null) {
//                    response = new Gson().fromJson(reader, responseClass);
//                }
//            }
//        }
//        return response;
//    }
//
//    private boolean isSuccessful(int status) {
//        return status / 100 == 2;
//    }

//    private String buildBoards(GameData gameData){
//        ChessBoard chessBoard = gameData.game().getBoard();
//        StringBuilder sb = new StringBuilder();
//
//        //white board
//        buildWhiteBoard(sb, chessBoard);
//
//        sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
//
//        //black board
//        buildBlackBoard(sb, chessBoard);
//
//
//        return sb.toString();
//    }
//
//    private static void buildBlackBoard(StringBuilder sb, ChessBoard chessBoard) {
//        writeHeader(sb, ChessGame.TeamColor.BLACK);
//        for (int row = 8; row >=1; row--) {
//            writeRowNum(sb, row);
//            for (int col = 1; col <= 8; col++) {//int col = 8; col >= 1; col--) {
//                buildBoard(row, col, chessBoard, sb);
//            }
//            writeRowNum(sb, row);
//            sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
//        }
//        writeHeader(sb, ChessGame.TeamColor.BLACK);
//    }
//
//    private static void buildWhiteBoard(StringBuilder sb, ChessBoard chessBoard) {
//        writeHeader(sb, ChessGame.TeamColor.WHITE);
//        for (int row = 1; row <= 8; row++) {
//            writeRowNum(sb, row);
//            for (int col = 8; col >= 1; col--) {//int col = 1; col <= 8; col++) {
//                buildBoard(row, col, chessBoard, sb);
//            }
//            writeRowNum(sb, row);
//            sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
//        }
//        writeHeader(sb, ChessGame.TeamColor.WHITE);
//    }
//
//    private static void buildBoard(int row, int col, ChessBoard chessBoard, StringBuilder sb) {
//        ChessPosition position = new ChessPosition(row, col);
//        ChessPiece piece = chessBoard.getPiece(position);
//        boolean whiteSquare = (row + col) % 2 == 1;
//
//        // Set square color
//        if (whiteSquare) {
//            sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_YELLOW);//WHITE);
//        } else {
//            sb.append(EscapeSequences.SET_BG_COLOR_DARK_OLIVE_GREEN3);//GREY);
//        }
//
//        //fill in the squares
//        if (piece == null){
//            sb.append(EscapeSequences.EMPTY);
//            sb.append(EscapeSequences.RESET_BG_COLOR);
//        }
//        else {
//            drawPiece(piece, sb);
//            sb.append(EscapeSequences.RESET_TEXT_COLOR);
//        }
//    }
//
//    private static void writeRowNum(StringBuilder sb, int row) {
//        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        sb.append(" ").append(row).append("\u2003");//9 - row).append("\u2003");
//    }
//
//    private static void writeHeader(StringBuilder sb, ChessGame.TeamColor color) {
//        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
//        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        if (color == ChessGame.TeamColor.WHITE){
//            sb.append("  \u2003 h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a\u2003  \u2003");
//        }
//        else{
//            sb.append("  \u2003 a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h\u2003  \u2003");
//        }
//        sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
//    }
//
//    private static void drawPiece(ChessPiece piece, StringBuilder sb) {
//        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
//        if (Objects.requireNonNull(piece.getTeamColor()) == ChessGame.TeamColor.WHITE) {
//            switch (piece.getPieceType()) {
//                case KING:
//                    sb.append(EscapeSequences.WHITE_KING);
//                    break;
//                case QUEEN:
//                    sb.append(EscapeSequences.WHITE_QUEEN);
//                    break;
//                case BISHOP:
//                    sb.append(EscapeSequences.WHITE_BISHOP);
//                    break;
//                case KNIGHT:
//                    sb.append(EscapeSequences.WHITE_KNIGHT);
//                    break;
//                case ROOK:
//                    sb.append(EscapeSequences.WHITE_ROOK);
//                    break;
//                case PAWN:
//                    sb.append(EscapeSequences.WHITE_PAWN);
//                    break;
//                default:
//                    sb.append(EscapeSequences.EMPTY);
//                    break;
//            }
//        }
//        else{
//            switch (piece.getPieceType()) {
//                case KING:
//                    sb.append(EscapeSequences.BLACK_KING);
//                    break;
//                case QUEEN:
//                    sb.append(EscapeSequences.BLACK_QUEEN);
//                    break;
//                case BISHOP:
//                    sb.append(EscapeSequences.BLACK_BISHOP);
//                    break;
//                case KNIGHT:
//                    sb.append(EscapeSequences.BLACK_KNIGHT);
//                    break;
//                case ROOK:
//                    sb.append(EscapeSequences.BLACK_ROOK);
//                    break;
//                case PAWN:
//                    sb.append(EscapeSequences.BLACK_PAWN);
//                    break;
//                default:
//                    sb.append(EscapeSequences.EMPTY);
//                    break;
//            }
//        }
//    }
}
