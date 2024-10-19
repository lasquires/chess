package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class JoinGameHandler implements Route {

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            String authToken = request.headers("authorization");
            HashMap requestMap = new Gson().fromJson(request.body(), HashMap.class);
            String playerColor = requestMap.get("playerColor").toString();
            Number gameID = (Number) requestMap.get("gameID");
            int gameIDInt = gameID.intValue();
            new GameService().joinGame(gameIDInt, playerColor, authToken);
            response.status(200);
            return new Gson().toJson(new Object());
        }
        catch(DataAccessException e){
            return ErrorResponse.handleError(response,e);
        }
    }
}
