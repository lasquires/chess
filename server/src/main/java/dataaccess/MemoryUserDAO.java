package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private Map<String, UserData> users = new HashMap<>();
    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (!users.isEmpty() && users.containsKey(user.username())){
            throw new DataAccessException("User already exists");
        }
        else{
            users.put(user.username(), user);
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }


}
