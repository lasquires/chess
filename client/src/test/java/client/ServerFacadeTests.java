package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.util.Arrays;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private String authToken;
    private static String testUsername;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:"+port);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    void registerNewUser() throws ResponseException {
        testUsername = java.time.LocalDateTime.now().toString(); //guaranteed to be a unique name everytime
        authToken = facade.register(testUsername, "asdf", "asdf").authToken();
        Assertions.assertNotNull(authToken);
    }
    @Test
    @Order(2)
    void registerExistingUser() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        Assertions.assertThrows(ResponseException.class, ()->facade.register("asdf", "asdf", "asdf"));
    }

    @Test
    @Order(3)
    void loginRegisteredUser() throws ResponseException {
        if (testUsername==null){
            registerNewUser();
        }
        Assertions.assertDoesNotThrow(()->facade.login(testUsername, "asdf"));
    }
    @Test
    @Order(4)
    void loginUnregisteredUser() throws ResponseException {
        String newUsername = java.time.LocalDateTime.now().toString();
        Assertions.assertThrows(ResponseException.class,()->facade.login(newUsername, "asdf"));
    }

    @Test
    @Order(5)
    void logoutExistingUser() throws ResponseException {
        registerNewUser();
        Assertions.assertDoesNotThrow(()->facade.logout(authToken));
    }

    @Test
    @Order(5)
    void logoutInvalidAuth() throws ResponseException {
        if(testUsername==null){
            registerNewUser();
        }
        Assertions.assertThrows(ResponseException.class,()->facade.logout("invalidAuthToken"));
    }

    @Test
    void createGameValidAuth() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        Assertions.assertDoesNotThrow(()->facade.createGame("bob's game", authToken));
    }

    @Test
    void createGameInvalidAuth() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        Assertions.assertThrows(ResponseException.class, ()->facade.createGame("bob's game", "authToken"));
    }

    @Test
    void listGamesValidAuth() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        String gameList = facade.listGames(authToken);
        Assertions.assertNotNull(gameList);
    }
    @Test
    void listGamesInvalidAuth() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        Assertions.assertThrows(ResponseException.class, ()->facade.listGames("authToken"));
    }

    @Test
    void joinGameValidID() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        createGameValidAuth();
        Integer gameListSize = Arrays.stream(facade.listGames(authToken).split("\n")).toList().size();
        String game = facade.joinGame(gameListSize, "white", authToken);
        Assertions.assertNotNull(game);

    }
    @Test
    void joinGameInvalidColor() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        createGameValidAuth();
        Integer gameListSize = Arrays.stream(facade.listGames(authToken).split("\n")).toList().size();
        Assertions.assertThrows(ResponseException.class, ()->facade.joinGame(gameListSize, "purple", authToken));
    }
    @Test
    void observeGameValidGameID() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        createGameValidAuth();
        Integer gameListSize = Arrays.stream(facade.listGames(authToken).split("\n")).toList().size();
        String game = facade.observeGame(gameListSize, authToken);
        Assertions.assertNotNull(game);
    }
    @Test
    void observeGameInvalidGameID() throws ResponseException {
        if (authToken==null){
            registerNewUser();
        }
        createGameValidAuth();
        Integer gameListSize = Arrays.stream(facade.listGames(authToken).split("\n")).toList().size();
        Assertions.assertThrows(ResponseException.class, ()->facade.observeGame(gameListSize+5, authToken));
    }
}
