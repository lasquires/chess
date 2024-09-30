package chess;

import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {2,1},
                {-2,1},
                {2,-1},
                {-2,-1},
                {1,2},
                {-1,2},
                {1,-2},
                {-1,-2}
        };
        return ChessUtils.CalculateSingleMoves(board, myPosition, directions);
    }
}
