package handler;

import spark.Response;

public class ErrorResponse {

    public static String handleError(Response response, int errorCode, String customMessage){
        response.type("application/json");
        if (customMessage==null){
            customMessage = "unexpected Error";
        }
        response.status(errorCode);
        String responseBody =  switch (errorCode) {
            case 400 ->  "{\"message\": \"Error: bad request\" }";
            case 401 -> "{\"message\": \"Error: unauthorized\" }";
            case 403 -> "{\"message\": \"Error: already taken\" }";
            default -> "{\"message\": \"Error: (" + customMessage+ ")\" }";
        };
        response.body(responseBody);
        return responseBody;
    }
}
