package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ClearServiceTests {


    @Test
    public void simpleClearTest() throws DataAccessException {
        UserService userService = new UserService();
        GameService gameService = new GameService();
        ClearService clearService = new ClearService();
        DataAccess dataAccess = new DataAccess();

        UserData userData = new UserData("bob", "password", "bob@gmail.com");
        AuthData authData = userService.register(userData); //populate authData and UserData
        int gameID = gameService.createGame("bob's game", authData.authToken()); //populate gameData

        Assertions.assertNotEquals(0, dataAccess.getUserDAO().countUsers(), "Nothing was added to user db");
        Assertions.assertFalse(dataAccess.getGameDAO().listGames().isEmpty(), "Nothing was added to game db");
        Assertions.assertNotEquals(0, dataAccess.getAuthDAO().countActiveUsers(), "Nothing was added to auth db");

        clearService.clear();

        Assertions.assertEquals(0, dataAccess.getUserDAO().countUsers(), "The user dataset was not cleared");
        Assertions.assertTrue(dataAccess.getGameDAO().listGames().isEmpty(), "The game database was not cleared");
        Assertions.assertEquals(0, dataAccess.getAuthDAO().countActiveUsers(), "The authentication data was not cleared");
    }

    @AfterAll
    static void breakDown() throws DataAccessException{
        new ClearService().clear();
    }
}
