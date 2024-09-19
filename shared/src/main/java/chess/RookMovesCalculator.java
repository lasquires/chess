package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] rookMoves = {
                {1, 0}, //up
                {-1, 0}, //down
                {0, -1}, //left
                {0, 1}, // right
        };
        return ChessUtils.calculateLinearMoves(board, myPosition, rookMoves, ChessPiece.PieceType.ROOK);
    }
}
