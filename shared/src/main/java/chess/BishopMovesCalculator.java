package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        System.out.println("In bishopmovesCalculator");
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] bishopMoves = {
                {1, 1}, //down-right
                {-1, 1}, //up-right
                {-1, -1}, //up-left
                {1, -1} // down-left
        };
        return ChessUtils.calculateLinearMoves(board, myPosition, bishopMoves, ChessPiece.PieceType.BISHOP);
    }
}
