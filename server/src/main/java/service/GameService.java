package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public GameService(){
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)==null){
            throw new DataAccessException("Error: Invalid authToken");
        }
        return gameDAO.listGames();
    }

}
