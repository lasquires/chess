package service;

import model.*;
import dataaccess.*;


public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDao;

    public UserService(){
        userDAO = new MemoryUserDAO();
        authDao = new MemoryAuthDAO();
    }

    public AuthData register(UserData user) {
        return null;
    }
    public AuthData login(UserData user) {
        return null;
    }
    public void logout(AuthData auth) {
        return;
    }
}