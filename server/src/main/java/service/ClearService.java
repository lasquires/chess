package service;

import dataaccess.*;

public class ClearService {
//    private GameDAO gameDAO;
//    private AuthDAO authDAO;
//    private UserDAO userDAO;
    public void clear() throws DataAccessException{
        DataAccess.getAuthDAO().clear();
        DataAccess.getGameDAO().clear();
        DataAccess.getUserDAO().clear();
    }
}
