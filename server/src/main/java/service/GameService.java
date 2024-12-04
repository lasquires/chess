package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;

import java.util.List;
import java.util.Objects;


public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService() throws DataAccessException {
        DataAccess dataAccess = new DataAccess(); //added

        gameDAO = dataAccess.getGameDAO();
        authDAO = dataAccess.getAuthDAO();
    }

    public int createGame(String gameName, String authToken) throws DataAccessException{
        if(authToken == null || authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        int gameID = gameDAO.findNextID();
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(gameData);
        return gameID;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        return gameDAO.listGames();
    }

    public void joinGame(int gameID, String playerColor, String authToken) throws DataAccessException {
        if(authDAO.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        String username = authDAO.getAuth(authToken).username();
        if(gameDAO.getGame(gameID)==null){
            throw new DataAccessException("bad request");
        }
        GameData gameData = gameDAO.getGame(gameID);

        if (Objects.equals(playerColor, "BLACK")
                && gameData.blackUsername()==null
                || gameData.blackUsername().equals(username)){
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
            gameDAO.updateGame(updatedGame);
        }
        else if (Objects.equals(playerColor, "WHITE")
                && (gameData.whiteUsername()==null
                || gameData.whiteUsername().equals(username))){
            GameData updatedGame = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            gameDAO.updateGame(updatedGame);
        }
        else if (Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor, "BLACK")){
            throw new DataAccessException("already taken");
        }
        else{
            throw new DataAccessException(playerColor + " is not a valid color. Must be \"WHITE\" or \"BLACK\".");
        }
    }

}
