package dataaccess;

import java.sql.SQLException;

public class DataAccess {
//    private static UserDAO userDAO = new MemoryUserDAO();
//    private static GameDAO gameDAO = new MemoryGameDAO();;
//    private static AuthDAO authDAO = new MemoryAuthDAO();
//
//
//    public static UserDAO getUserDAO() {
//        return userDAO;
//    }
//
//    public static GameDAO getGameDAO() {
//        return gameDAO;
//    }
//
//    public static AuthDAO getAuthDAO() {
//        return authDAO;
//    }


    private static UserDAO userDAO = new MySqlUserDAO();
    private static GameDAO gameDAO = new MySqlGameDAO();
    private static AuthDAO authDAO = new MySqlAuthDAO();


    public DataAccess() throws DataAccessException{
        configureDatabase();
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


    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS UserData (
              `username` VARCHAR(256) NOT NULL,
              `password` VARCHAR(256) NOT NULL,
              `email` VARCHAR(256) NOT NULL,
              PRIMARY KEY (`username`),
              --INDEX (`email`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  GameData (
              `gameID` INT NOT NULL AUTO_INCREMENT,
              `whiteUsername` VARCHAR(256),
              `blackUsername` VARCHAR(256),
              `gameName` VARCHAR(256),
              `game` BLOB,
              PRIMARY KEY (`gameID`),
              FOREIGN KEY (`whiteUsername`) REFERENCES UserData(`username`),
              FOREIGN KEY (`blackUsername`) REFERENCES UserData(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS  AuthData (
              `authToken` VARCHAR(256) NOT NULL,
              `username` VARCHAR(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              FOREIGN KEY (`username`) REFERENCES UserData(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """

    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }



}
