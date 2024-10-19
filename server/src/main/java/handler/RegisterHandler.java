package handler;

import model.*;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import com.google.gson.Gson;


public class RegisterHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        UserData userData = new Gson().fromJson(request.body(), UserData.class);
        AuthData authData = new UserService().register(userData);
        response.status(200);
        return  new Gson().toJson(authData);

    }
}
