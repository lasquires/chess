package service;

import model.*;
import dataaccess.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;


public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService() throws DataAccessException {
        DataAccess dataAccess = new DataAccess(); //added
        this.userDAO = dataAccess.getUserDAO();
        this.authDAO = dataAccess.getAuthDAO();
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

        UserData hashedUserData = new UserData(userData.username(),
                BCrypt.hashpw(userData.password(), BCrypt.gensalt()),
                userData.email());

        userDAO.createUser(hashedUserData);

        return setAuthData(hashedUserData);
    }

    public AuthData login(UserData userData) throws DataAccessException {
        UserData userInDB = userDAO.getUser(userData.username());
        if(userInDB == null){
            throw new DataAccessException("unauthorized");
        }

        if(!BCrypt.checkpw(userData.password(), userInDB.password())){
            throw new DataAccessException("unauthorized");
        }
        return setAuthData(userInDB);
    }

    public void logout(String authToken) throws DataAccessException {
        if (authDAO==null || authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }

}