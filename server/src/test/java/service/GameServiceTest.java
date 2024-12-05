package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameServiceTest {
    static UserService userService;
    static GameService gameService;
    static GameDAO gameDAO;
    static AuthDAO authDAO;
    static AuthData authData;
    static UserData userData;
    static int gameID;


    @BeforeAll
    static void setUp() throws DataAccessException{
        DataAccess dataAccess = new DataAccess();
        userService = new UserService();
        gameService = new GameService();
        gameDAO = dataAccess.getGameDAO();
        authDAO = dataAccess.getAuthDAO();
        userData = new UserData("bob", "password", "bob@gmail.com");
        authData = userService.register(userData);

    }


    @AfterAll
    static void breakDown() throws DataAccessException{
        new ClearService().clear();
    }

    @Test
    @Order(1)
    void createGameValidAuth() throws DataAccessException {
        gameID = gameService.createGame("bob's game", authData.authToken());
        Assertions.assertNotNull(gameID);
    }
    @Test
    @Order(2)
    void createGameInvalidAuth() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.createGame("bob's game",
                "1234567890"));
    }

    @Test
    @Order(3)
    void listGamesValidAuth() throws DataAccessException {
        List<GameData> gameDataList = gameService.listGames(authData.authToken());
        Assertions.assertNotEquals(0, gameDataList.size());
    }

    @Test
    @Order(4)
    void listGamesInvalidAuth() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()->gameService.listGames("1234567890"));
    }


    @Test
    @Order(5)
    void joinGameUnusedColor() throws DataAccessException {
        Assertions.assertDoesNotThrow(()->gameService.joinGame(gameID, "WHITE", authData.authToken()));
    }

    @Test
    @Order(6)
    void joinGameInvalidColor() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()->gameService.joinGame(gameID, "purple",
                authData.authToken()), "Can't add two players to same color.");
    }


}