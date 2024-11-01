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

        Assertions.assertDoesNotThrow(()->dataAccess.getUserDAO().getUser("bob"));
        Assertions.assertDoesNotThrow(()->dataAccess.getAuthDAO().getAuth(authData.authToken()));
        Assertions.assertDoesNotThrow(()->dataAccess.getGameDAO().getGame(gameID));


        clearService.clear();

        Assertions.assertNull(dataAccess.getUserDAO().getUser("bob"));
        Assertions.assertNull(dataAccess.getAuthDAO().getAuth(authData.authToken()));
        Assertions.assertNull(dataAccess.getGameDAO().getGame(gameID));

    }

    @AfterAll
    static void breakDown() throws DataAccessException{
        new ClearService().clear();
    }
}
