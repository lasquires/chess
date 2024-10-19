package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            UserData userData = new Gson().fromJson(request.body(), UserData.class);
            AuthData authData = new UserService().login(userData);
            response.status(200);
            return  new Gson().toJson(authData);
        }
        catch(DataAccessException e){
            return ErrorResponse.handleError(response, e);
        }
    }
}
