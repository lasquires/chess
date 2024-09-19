package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
//        Collection<ChessMove> moves = new ArrayList<>();
        int[][] kingMoves = {
                {1, 0}, //up
                {-1, 0}, //down
                {0, -1}, //left
                {0, 1}, // right
                {1, 1}, //up-right
                {-1, 1}, //down-right
                {-1, -1}, //down-left
                {1, -1} // up-left
        };
        return ChessUtils.calculateSingleMoves(board, myPosition, kingMoves, ChessPiece.PieceType.KING);
    }
}
