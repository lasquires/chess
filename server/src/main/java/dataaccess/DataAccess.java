package dataaccess;

public class DataAccess {
    private static UserDAO userDAO;
    private static GameDAO gameDAO;
    private static AuthDAO authDAO;

    public DataAccess(){
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }


    public static UserDAO getUserDAO() {
        return userDAO;
    }

    public static GameDAO getGameDAO() {
        return gameDAO;
    }

    public static AuthDAO getAuthDAO() {
        return authDAO;
    }
}
