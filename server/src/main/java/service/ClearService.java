package service;

import dataaccess.*;

public class ClearService {
    public void clear() throws DataAccessException{
        DataAccess dataAccess = new DataAccess(); //added
        dataAccess.getAuthDAO().clear();
        dataAccess.getGameDAO().clear();
        dataAccess.getUserDAO().clear();
    }
}
