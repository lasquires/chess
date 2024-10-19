package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

public class ListGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        try{
            String authToken = request.headers("authorization");//new Gson().fromJson(request.body(), String.class);
            List<GameData> gameDataList = new GameService().listGames(authToken);
            response.status(200);
            return new Gson().toJson(gameDataList);
        }catch(DataAccessException e){
            return ErrorResponse.handleError(response, e);
        }
    }
}
