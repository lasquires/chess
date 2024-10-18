package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class AuthService {
    private AuthDAO authDAO;

    public AuthService(){
        authDAO = new MemoryAuthDAO();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
    public AuthData setAuthData(UserData userData) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        AuthData authData =  new AuthData(authToken, userData.username());
        authDAO.createAuth(authData);
        return authData;
    }

    public void removeAuthData(AuthData authData) throws DataAccessException {
        authDAO.deleteAuth(authData.authToken());
    }
}
