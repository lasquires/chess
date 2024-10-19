package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.*;

public class ListGameHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            String authToken = request.headers("authorization");//new Gson().fromJson(request.body(), String.class);
            List<GameData> gameDataList = new GameService().listGames(authToken);

            var gameList = formatOutput(gameDataList);

            var responseData = new LinkedHashMap<>();
            responseData.put("games", gameList);

            response.status(200);
            return new Gson().toJson(responseData);
        }catch(DataAccessException e){
            return ErrorResponse.handleError(response, e);
        }
    }

    private static ArrayList<Object> formatOutput(List<GameData> gameDataList) {
        var gameList = new ArrayList<>();
        for (GameData gameData: gameDataList){
            var gameInfo = new LinkedHashMap<>();
            gameInfo.put("gameID", gameData.gameID());
            if (gameData.whiteUsername()==null){
                gameInfo.put("whiteUsername", "");
            }else{
                gameInfo.put("whiteUsername", gameData.whiteUsername());
            }
            if (gameData.blackUsername()==null){
                gameInfo.put("blackUsername", "");
            }else{
                gameInfo.put("blackUsername", gameData.blackUsername());
            }
            gameInfo.put("gameName", gameData.gameName());

            gameList.add(gameInfo);
        }
        return gameList;
    }
}
