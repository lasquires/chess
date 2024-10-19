package service;

import model.*;
import dataaccess.*;

import java.util.Objects;
import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(){
        userDAO = DataAccess.getUserDAO();
        authDAO = DataAccess.getAuthDAO();
    }

    public AuthData setAuthData(UserData userData) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData =  new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public AuthData register(UserData userData) throws DataAccessException {

        if(userData.username()==null|| userData.password()==null|| userData.email()==null) {
            throw new DataAccessException("Error: you must provide a username, password, and email");
        }
        if(userDAO!=null && userDAO.getUser(userData.username())!=null){
            throw new DataAccessException("Error: username already taken");
        }
        userDAO.createUser(userData);

        return setAuthData(userData);
    }

    public AuthData login(UserData userData) throws DataAccessException {
        if(userDAO.getUser(userData.username())==null){
            throw new DataAccessException("Error: username not found");
        }
        if(!Objects.equals(userDAO.getUser(userData.username()).password(), userData.password())){
            throw new DataAccessException("Error: Incorrect password");
        }
        return setAuthData(userData);
    }

    public void logout(AuthData authData) throws DataAccessException {
        authDAO.deleteAuth(authData.authToken());
    }

}