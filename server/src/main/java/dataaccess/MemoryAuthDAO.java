package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private Map<String, AuthData> authTokens = new HashMap<>();     //{authToken: AuthData, ...}

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if (authTokens.containsKey(authData.authToken())){
            throw new DataAccessException("AuthData already in use");
        }
        else{
            authTokens.put(authData.authToken(), authData);     //authTokens[authToken]=authData
        }

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authTokens.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authTokens.clear();
    }
    public int countActiveUsers(){
        return authTokens.size();
    }
}
