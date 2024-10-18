package service;

import model.*;
import dataaccess.*;

import java.util.Objects;
import java.util.UUID;


public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public AuthData setAuthData(UserData userData) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData authData =  new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public UserService(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
    }

    public AuthData register(UserData userData) throws DataAccessException {
        if(userData.username()==null|| userData.password()==null|| userData.email()==null) {
            throw new DataAccessException("Error: you must provide a username, password, and email");
        }
        if(userDAO.getUser(userData.username())!=null){
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