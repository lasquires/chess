package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition){
        System.out.println("In QueenMovesCalculator");
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] queenMoves = {
                {1, 0}, //up
                {-1, 0}, //down
                {0, -1}, //left
                {0, 1}, // right
                {1, 1}, //up-right
                {-1, 1}, //down-right
                {-1, -1}, //down-left
                {1, -1} // up-left
        };
        return ChessUtils.calculateLinearMoves(board, myPosition, queenMoves, ChessPiece.PieceType.QUEEN);
    }
}
