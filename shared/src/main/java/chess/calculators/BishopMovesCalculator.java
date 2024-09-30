package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;


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
