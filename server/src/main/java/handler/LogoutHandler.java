package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws DataAccessException { //request is authtoken
        try{
            String authToken = request.headers("authorization");//new Gson().fromJson(request.body(), String.class);
            new UserService().logout(authToken);
            response.status(200);
            return new Gson().toJson(null);
        }catch(DataAccessException e){
            return ErrorResponse.handleError(response, e);
        }
    }
}
