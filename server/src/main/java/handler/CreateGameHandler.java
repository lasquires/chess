package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            String authToken = request.headers("authorization");
            Map<String, String> requestMap = new Gson().fromJson(request.body(), Map.class);
            String gameName = requestMap.get("gameName");
            int gameID = new GameService().createGame(gameName,authToken);
            response.status(200);
            return new Gson().toJson(Map.of("gameID", gameID));
        }
        catch(DataAccessException e){
            return ErrorResponse.handleError(response,e);
        }
    }
}
