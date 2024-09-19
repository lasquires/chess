package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] knightMoves = {
                {2, -1},
                {2, 1},
                {1, -2},
                {1, 2},
                {-1, -2},
                {-1, 2},
                {-2, -1},
                {-2, 1}
        };
        return ChessUtils.calculateSingleMoves(board, myPosition, knightMoves, ChessPiece.PieceType.KNIGHT);
    }
}
