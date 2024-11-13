package handler;

import dataaccess.DataAccessException;
import spark.Response;

public class ErrorResponse {

    public static String handleError(Response response, DataAccessException error){

        String errorMessage = error.getMessage();

        int responseCode =  switch (errorMessage) {
            case "bad request" -> 400;
            case "unauthorized" -> 401;
            case "already taken" -> 403;
            default -> 500;
        };
        if (responseCode==500){
            errorMessage="("+errorMessage+")";
        }
        response.status(responseCode);
        errorMessage = "{\"message\": \"" + errorMessage+ "\" }";
        response.body(errorMessage);
        return errorMessage;
    }
}
