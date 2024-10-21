package dataaccess;

public class DataAccess {
    private static UserDAO userDAO = new MemoryUserDAO();
    private static GameDAO gameDAO = new MemoryGameDAO();;
    private static AuthDAO authDAO = new MemoryAuthDAO();


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
