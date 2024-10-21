package service;

import model.*;
import dataaccess.*;

import java.util.Objects;
import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(){
        this.userDAO = DataAccess.getUserDAO();
        this.authDAO = DataAccess.getAuthDAO();
    }

    private AuthData setAuthData(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData =  new AuthData(userData.username(), authToken);
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData register(UserData userData) throws DataAccessException {

        if(userData.username()==null|| userData.password()==null|| userData.email()==null) {
            throw new DataAccessException("bad request");
        }
        if(userDAO!=null && userDAO.getUser(userData.username())!=null){
            throw new DataAccessException("already taken");
        }
        userDAO.createUser(userData);

        return setAuthData(userData);
    }

    public AuthData login(UserData userData) throws DataAccessException {
        if(userDAO.getUser(userData.username())==null){
            throw new DataAccessException("unauthorized");
        }
        if(!Objects.equals(userDAO.getUser(userData.username()).password(), userData.password())){
            throw new DataAccessException("unauthorized");
        }
        return setAuthData(userData);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDAO==null || authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

}