package dataaccess;

import model.UserData;

public interface UserDAO {
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;

    int countUsers();
    // TODO add more methods
}
