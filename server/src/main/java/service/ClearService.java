package service;

import dataaccess.*;

public class ClearService {
    public void clear() throws DataAccessException{
        DataAccess.getAuthDAO().clear();
        DataAccess.getGameDAO().clear();
        DataAccess.getUserDAO().clear();
    }
}
