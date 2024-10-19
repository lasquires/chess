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
        String body = request.body();

//        String username = null;
//        String password = null;
//        String email = null;
//        username = request.queryParams("username");
//        password = request.queryParams("password");
//        email = request.queryParams("email");

//        UserData userData = new UserData(username, password, email);
        Gson gson = new Gson();
        UserData userData = gson.fromJson(body, UserData.class);

        UserService userService = new UserService();

        AuthData authData = userService.register(userData);

        response.type("application/json");

        response.status(200);

        return new Gson().toJson(authData);



    }
}
