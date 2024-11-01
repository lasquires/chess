package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if(getUser(user.username()) != null){
            throw new DataAccessException("User already exists");
        }

        try(var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO UserData (username, password, email) VALUES (?,?,?)";
            try (var request = conn.prepareStatement(statement)) {
                request.setString(1, user.username());
                request.setString(2, user.password());
                request.setString(3, user.email());

                request.executeUpdate();
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
            try (var query = conn.prepareStatement(statement)) {
                query.setString(1, username);
                try (var userData = query.executeQuery()) {
                    if (userData.next()) {
                        String usernameResult = userData.getString("username");
                        String passwordResult = userData.getString("password");
                        String emailResult = userData.getString("email");
                        return new UserData(usernameResult,passwordResult, emailResult);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM UserData";
            try (var request = conn.prepareStatement(statement)) {
                request.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear users: " + e.getMessage());
        }
    }


}


