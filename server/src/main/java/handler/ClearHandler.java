package handler;


import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.ClearService;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.xml.crypto.Data;

public class ClearHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        try{
            new ClearService().clear();
            response.status(200);
            return new Gson().toJson(new Object());
        }
        catch (DataAccessException e){
            return ErrorResponse.handleError(response, e);
        }
    }
}
