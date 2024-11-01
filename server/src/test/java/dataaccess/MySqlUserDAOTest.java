package dataaccess;

import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlUserDAOTest {

    private UserDAO userDAO;
    @BeforeEach
    void setUp() throws DataAccessException {
        DataAccess dataAccess = new DataAccess();
        userDAO = dataAccess.getUserDAO();
    }

    @AfterEach
    void tearDown() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    void createUser() throws DataAccessException {
        UserData userData = new UserData("bob", "password", "bob@gmail.com");
        userDAO.createUser(userData);
        //check if is in the database
        assertDoesNotThrow(()->userDAO.getUser(userData.username()));
    }
    @Test
    void createUserAlreadyInDB() throws DataAccessException {
        UserData userData = new UserData(null, "password", "bob@gmail.com");
        createUser();
        assertThrows(DataAccessException.class, this::createUser);
    }


    @Test
    void getUser() throws DataAccessException {
        //it's the same createUser I guess...
        createUser();
    }
    @Test
    void getUserWrongUsername() throws DataAccessException {
        //it's the same createUser I guess...
        createUser();
        assertNull(userDAO.getUser("bobby"));
    }

    @Test
    void clear() throws DataAccessException {
        //if clear wasn't working, it wouldn't allow me to create a new user since it's already in the database
        createUser();
    }

}