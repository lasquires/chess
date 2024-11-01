package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlGameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DataAccess dataAccess = new DataAccess();
        gameDAO = dataAccess.getGameDAO();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        gameDAO.clear();
    }


    @Test
    void createGameNewID() throws DataAccessException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        GameData gameGotten = gameDAO.getGame(gameID);
        assertNotNull(gameGotten, "Error in retreiving the game");
    }
    @Test
    void createGameUsedID() throws DataAccessException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        assertThrows(DataAccessException.class, ()->gameDAO.createGame(newGame));
    }

    @Test
    void getGameValidID() throws DataAccessException {
        createGameNewID();
    }
    @Test
    void getGameInvalidID() throws DataAccessException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        GameData gameGotten = gameDAO.getGame(9000);
        assertNull(gameGotten);
    }

    @Test
    void listGamesPopulated() throws DataAccessException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        assertFalse(gameDAO.listGames().isEmpty());
    }
    @Test
    void listGamesNotPopulated() throws DataAccessException {
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    void updateGameValidID() throws DataAccessException, InvalidMoveException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        GameData gameGotten = gameDAO.getGame(gameID);

        gameGotten.game().makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));

        gameDAO.updateGame(gameGotten);
        GameData updatedGame = gameDAO.getGame(gameID);
        assertFalse(updatedGame.game().equals(newGame.game()),"New move wasn't saved to database");
    }

    @Test
    void updateGameInvalidID() throws DataAccessException, InvalidMoveException {
        int gameID = 1;
        GameData newGame = new GameData(gameID, null, null, "newGame", new ChessGame());
        gameDAO.createGame(newGame);
        GameData gameGotten = gameDAO.getGame(gameID);

        gameGotten.game().makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        GameData invalidID = new GameData(9000,
                gameGotten.whiteUsername(),
                gameGotten.blackUsername(),
                newGame.gameName(),
                gameGotten.game());

        assertThrows(DataAccessException.class, ()->gameDAO.updateGame(invalidID), "you shouldn't be allowed to send in invalid ID");
    }

    @Test
    void clear() {
        assertDoesNotThrow(this::createGameNewID);
    }
}