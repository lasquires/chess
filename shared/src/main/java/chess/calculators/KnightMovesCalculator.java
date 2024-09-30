package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

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
