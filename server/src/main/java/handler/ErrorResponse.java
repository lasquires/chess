package handler;

import dataaccess.DataAccessException;
import spark.Response;

public class ErrorResponse {

    public static String handleError(Response response, DataAccessException error){
//        response.type("application/json");

        String errorMessage = error.getMessage();

        int responseCode =  switch (errorMessage) {
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
//            case 400 ->  "{\"message\": \"Error: bad request\" }";
//            case 401 -> "{\"message\": \"Error: unauthorized\" }";
//            case 403 -> "{\"message\": \"Error: already taken\" }";
//            default -> "{\"message\": \"Error: (" + customMessage+ ")\" }";
        };
        response.status(responseCode);
        errorMessage = "{\"message\": \"Error: " + errorMessage+ "\" }";
        response.body(errorMessage);
        return errorMessage;
    }
}
