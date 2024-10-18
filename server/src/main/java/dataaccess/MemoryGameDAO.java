package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    private Map<Integer, GameData> games = new HashMap<>();

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if(games.containsKey(game.gameID())){
            throw new DataAccessException("Error: Game by this id already exists");
        }
        else{
            games.put(game.gameID(), game);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if (games.containsKey(gameID)){
            throw new DataAccessException("Error: Game by this id not found");
        }
        else{
            return games.get(gameID);
        }
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        games.put(game.gameID(), game); //TODO: Don't really understand, I'll come back
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
