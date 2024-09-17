package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    /**needs to be able to calculate all possible moves for a piece given its type and position
     * @param the current boardstate
     * @param piece's current position
     * @return collection of all possible moves
    **/
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition);
}
