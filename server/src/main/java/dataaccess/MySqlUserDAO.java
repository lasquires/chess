package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO {
    @Override
    public void createUser(UserData user) throws DataAccessException {
        String checkUserName = "SELECT username FROM UserData WHERE username = ?";


    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
//        try (var conn = DatabaseManager.getConnection()) {
//            var statement = "SELECT username, password, email FROM UserData WHERE username=?";
////            var statement = "SELECT id, json FROM pet WHERE id=?";
//            try (var ps = conn.prepareStatement(statement)) {
//                ps.setInt(1, username);
//                try (var rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        return readPet(rs);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
//        }
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
