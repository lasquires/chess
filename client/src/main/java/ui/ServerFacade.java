package ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
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
import java.util.List;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }


    public AuthData register(String username, String password, String email) throws ResponseException {
        String path = "/user";
        UserData request = new UserData(username, password, email);
        System.out.println(request.toString());
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
        for (var game : gamesList){
//            System.out.println(game.keySet());
//            System.out.println(game.values());
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
            System.out.println(outString);
            index++;
        }
        return outString;
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

}
