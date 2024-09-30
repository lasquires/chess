package chess;

import java.util.Collection;
import java.util.List;

public class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,0},//right
                {0,1},//up
                {-1,0},//left
                {0,-1}//down

        };
        return ChessUtils.CalculateLinearMoves(board, myPosition, directions);
    }
}
