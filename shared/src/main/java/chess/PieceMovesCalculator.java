package chess;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition);
}
