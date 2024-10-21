package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1,0},//right
                {0,1},//up
                {-1,0},//left
                {0,-1},//down
                {1,1},//right-up
                {-1,1},//left-up
                {-1,-1},//left-down
                {1,-1},//right-down


        };
        return ChessUtils.calculateSingleMoves(board, myPosition, directions);
    }
}
