package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(getUser(user.username()) != null){
            throw new DataAccessException("User already exists");
        }

        try(var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO UserData (username, password, email) VALUES (?,?,?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, user.username());
                ps.setString(2, user.password());
                ps.setString(3, user.email());

                ps.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException("Unable to add user");
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to connect to database");
        }


    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM UserData WHERE username=?";
//            var statement = "SELECT id, json FROM pet WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String usernameResult = rs.getString("username");
                        String passwordResult = rs.getString("password");
                        String emailResult = rs.getString("email");
                        return new UserData(usernameResult,passwordResult, emailResult);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public int countUsers() {
        return 0;
    }
}
