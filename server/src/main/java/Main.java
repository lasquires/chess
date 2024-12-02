import chess.*;
import dataaccess.DataAccessException;
import server.Server;


public class Main {
    public static void main(String[] args) throws DataAccessException {
        Server server = new Server();

        int port = server.run(8080);

        System.out.println("Server is running on port: " + port);



//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Server: " + piece);
    }
}