package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlGameDAO implements GameDAO {
    @Override
    public void createGame(GameData game) throws DataAccessException {
        if(getGame(game.gameID())!=null){
            throw new DataAccessException("Game with this ID already exists");
        }
        updateGameData(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData WHERE gameID=?";
            try (var query = conn.prepareStatement(statement)) {
                query.setInt(1, gameID);
                try (var response = query.executeQuery()) {
                    if (response.next()) {
                        return deserialize(response);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> gameDataList = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM GameData";
            try (var query = conn.prepareStatement(statement)) {
                try (var response = query.executeQuery()) {
                    while (response.next()) {
                            gameDataList.add(deserialize(response));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return gameDataList;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (getGame(game.gameID())==null){
            throw new DataAccessException("Game with this ID not found");
        }
        updateGameData(game);
    }

    private static void updateGameData(GameData game) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            String statement = """
            INSERT INTO GameData (gameID, whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                whiteUsername = VALUES(whiteUsername),
                blackUsername = VALUES(blackUsername),
                gameName = VALUES(gameName),
                game = VALUES(game);
            """;
            try (var request = conn.prepareStatement(statement)) {
                request.setInt(1, game.gameID());
                request.setString(2, game.whiteUsername());
                request.setString(3, game.blackUsername());
                request.setString(4, game.gameName());
                request.setString(5, new Gson().toJson(game.game(), ChessGame.class));

                request.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException("Unable to update game");
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to connect to database");
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM GameData";
            try (var request = conn.prepareStatement(statement)) {
                request.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear users: " + e.getMessage());
        }
    }

    @Override
    public int findNextID() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT MAX(gameID) FROM GameData";
            try (var request = conn.prepareStatement(query)) {
                var result = request.executeQuery();
                if (result.next()){
                    return result.getInt(1)+1;
                }
                else{
                    return 1;
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Unable to clear users: " + e.getMessage());
        }
    }

    private GameData deserialize(ResultSet response) throws SQLException {
        int gameIDResult = response.getInt("gameID");
        String whiteUsername = response.getString("whiteUsername");
        String blackUsername = response.getString("blackUsername");
        String gameName = response.getString("gameName");
        String gameJson = response.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
        initializePieces(game);
        return new GameData(gameIDResult,whiteUsername, blackUsername, gameName, game);
    }

    private void initializePieces(ChessGame game){
        for (int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
                if (piece!=null){
                    piece.setMovesCalculator();
                }
            }
        }

    }

}
