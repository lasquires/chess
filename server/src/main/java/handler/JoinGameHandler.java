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
    //
    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            String authToken = request.headers("authorization");
            HashMap requestMap = new Gson().fromJson(request.body(), HashMap.class);
            Object playerColor = requestMap.get("playerColor");
            Number gameID = (Number) requestMap.get("gameID");
            if (gameID==null || playerColor==null){
                throw new DataAccessException("bad request");
            }
            new GameService().joinGame(gameID.intValue(), playerColor.toString(), authToken);
            response.status(200);
            return new Gson().toJson(new Object());
        }
        catch(DataAccessException e){
            return ErrorResponse.handleError(response,e);
        }
    }
}
