package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlAuthDAOTest {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        DataAccess dataAccess = new DataAccess();
        authDAO = dataAccess.getAuthDAO();
        userDAO = dataAccess.getUserDAO();

        UserData userData = new UserData("bob", "password", "bob@gmail.com");
        userDAO.createUser(userData);

    }

    @AfterEach
    void tearDown() throws DataAccessException {
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    void createAuth() throws DataAccessException {
        AuthData authData = new AuthData("bob", "ValidAuth");
        authDAO.createAuth(authData);
        //check if is in the database
        assertDoesNotThrow(()->authDAO.getAuth(authData.authToken()));
    }
    @Test
    void createAuthUsernameInvalid() throws DataAccessException {
        AuthData authData = new AuthData("bobby", "ValidAuth");
        assertThrows(DataAccessException.class, ()->authDAO.createAuth(authData));
    }
//

    @Test
    void getAuth() throws DataAccessException {
        createAuth();
    }
    @Test
    void getAuthInvalidAuthToken() throws DataAccessException {
        createAuth();
        assertNull(authDAO.getAuth("invalidAuthToken"));
    }

    @Test
    void deleteAuth() throws DataAccessException {
        createAuth();
        authDAO.deleteAuth("ValidAuth");
        assertNull(authDAO.getAuth("ValidAuth"));
    }

    @Test
    void deleteAuthInvalidAuth() throws DataAccessException {
        createAuth();
        authDAO.deleteAuth("inValidAuth");
        assertNotNull(authDAO.getAuth("ValidAuth"));
    }

    @Test
    void clear() {
        //create Auth would throw an error if it wasn't cleared earlier
        assertDoesNotThrow(this::createAuth);

    }
}