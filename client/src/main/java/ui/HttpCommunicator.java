package ui;

import com.google.gson.Gson;
import exception.ResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class HttpCommunicator {
    private final String baseUrl;

    public HttpCommunicator(String serverUrl){
        this.baseUrl = serverUrl;
    }
    public <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(baseUrl + path)).toURL();
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
            String errorMessage = readErrorBody(http);
            throw new ResponseException(status, errorMessage);
//            throw new ResponseException(status, "failure: " + status);
        }
    }

    private String readErrorBody(HttpURLConnection http) throws IOException {
        String errorMessage = "No error information provided";
        try (InputStream errorStream = http.getErrorStream()) {
            if (errorStream != null) {

                InputStreamReader reader = new InputStreamReader(errorStream);
                errorMessage = reader.toString();
                Map<String, Object> errorMap = new Gson().fromJson(reader, Map.class);
                errorMessage = (String) errorMap.getOrDefault("message", errorMessage);
            }
        }
        return errorMessage;
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
