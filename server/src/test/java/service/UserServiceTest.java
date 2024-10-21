package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {

    static UserService userService;
    static GameService gameService;
    static GameDAO gameDAO;
    static AuthDAO authDAO;
    static AuthData authData;
    static UserData userData;


    @BeforeEach
    void setUp() throws DataAccessException {
        userService = new UserService();
        gameService = new GameService();
        gameDAO = DataAccess.getGameDAO();
        authDAO = DataAccess.getAuthDAO();
        userData = new UserData("bob", "password", "bob@gmail.com");
    }

    @AfterAll
    static void breakDown() throws DataAccessException{
        new ClearService().clear();
    }

    @Test
    @Order (1)
    void registerNewUser() throws DataAccessException {
        authData = userService.register(userData);
        Assertions.assertEquals(AuthData.class, authData.getClass());
    }

    @Test
    @Order (2)
    void registerAlreadyRegisteredUser() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()->userService.register(userData), "This user was already registered");
    }

    @Test
    @Order (3)
    void loginExistingUser() throws DataAccessException {
        AuthData newAuthData = userService.login(userData);
        Assertions.assertNotEquals(authData, newAuthData);
    }

    @Test
    @Order (4)
    void loginNewUser() throws DataAccessException {
        UserData newUser = new UserData("bobby", "password", "bob@gmail.com");
        Assertions.assertThrows(DataAccessException.class, ()->userService.login(newUser), "You can't login a user that isn't registered");
    }


    @Test
    @Order(5)
    void logoutExistingUser() throws DataAccessException {
        userService.logout(authData.authToken());
        Assertions.assertThrows(DataAccessException.class, ()-> gameService.listGames(authData.authToken()),
                "User still logged in");
        //log user back in for next test
        authData = userService.login(userData);
    }

    @Test
    @Order(6)
    void logoutNonexistingAuthToken() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, ()-> userService.logout("1234567890"),
                "invalid authToken");
    }
}