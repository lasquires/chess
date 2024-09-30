package chess;

import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,1},//right-up
                {-1,1},//left-up
                {-1,-1},//left-down
                {1,-1},//right-down
        };
        return ChessUtils.CalculateLinearMoves(board, myPosition, directions);
    }
}
