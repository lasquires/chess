package chess.calculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition);
}
