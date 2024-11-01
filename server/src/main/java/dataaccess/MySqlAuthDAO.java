package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO{


    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if(getAuth(authData.authToken()) != null){
            throw new DataAccessException("Authtoken already in use");
        }

        try(var conn = DatabaseManager.getConnection()){
            String statement = "INSERT INTO AuthData (authToken, username) VALUES (?,?)";
            try (var request = conn.prepareStatement(statement)) {
                request.setString(1, authData.authToken());
                request.setString(2, authData.username());

                request.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException("Unable to add authToken");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM AuthData WHERE authToken=?";
//            var statement = "SELECT id, json FROM pet WHERE id=?";
            try (var query = conn.prepareStatement(statement)) {
                query.setString(1, authToken);
                try (var userData = query.executeQuery()) {
                    if (userData.next()) {
                        String authTokenResult = userData.getString("authToken");
                        String userNameResult = userData.getString("username");
                        return new AuthData(userNameResult,authTokenResult);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM AuthData WHERE authToken = ?";
            try (var request = conn.prepareStatement(statement)) {
                request.setString(1, authToken);
                request.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable delete AuthData: %s", e.getMessage()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM AuthData";
            try (var request = conn.prepareStatement(statement)) {
                request.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable delete AuthData: %s", e.getMessage()));
        }
    }

}
