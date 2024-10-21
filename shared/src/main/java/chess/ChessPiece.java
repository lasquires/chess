package chess;

import chess.calculators.BishopMovesCalculator;
import chess.calculators.KingMovesCalculator;
import chess.calculators.KnightMovesCalculator;
import chess.calculators.PawnMovesCalculator;
import chess.calculators.PieceMovesCalculator;
import chess.calculators.QueenMovesCalculator;
import chess.calculators.RookMovesCalculator;

import java.util.Collection;
import java.util.Objects;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private PieceMovesCalculator movesCalculator;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        this.movesCalculator = getCalculator(type);
    }

    private PieceMovesCalculator getCalculator(PieceType type) {
        switch(type){
            case KING -> {return new KingMovesCalculator();}
            case QUEEN ->{return new QueenMovesCalculator();}
            case BISHOP -> {return new BishopMovesCalculator();}
            case KNIGHT -> {return new KnightMovesCalculator();}
            case ROOK -> {return new RookMovesCalculator();}
            case PAWN -> {return new PawnMovesCalculator();}
            default -> new IllegalArgumentException("Invalid piece type");
        }
        return null;

    }


    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN;

    }
    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return this.movesCalculator.getMoves(board, myPosition);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
