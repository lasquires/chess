package chess;

import java.util.ArrayList;
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
    private PieceType pieceType;
    private PieceMovesCalculator movesCalculator;

    //constructor
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType pieceType) {
        this.pieceColor = pieceColor;
        this.pieceType = pieceType;
        this.movesCalculator = getCalculator(pieceType);
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
        PAWN
    }


    private PieceMovesCalculator getCalculator(PieceType pieceType){
        System.out.println("in PieceMovesCalculator");
        switch (pieceType){
            case KING -> new KingMovesCalculator();
            case QUEEN -> new QueenMovesCalculator();
            case BISHOP -> {return new BishopMovesCalculator();} //idk why this
            case KNIGHT -> new KnightMovesCalculator();
            case ROOK -> new RookMovesCalculator();
            case PAWN -> new PawnMovesCalculator();
            default -> new IllegalArgumentException("Invalid piece type");

        }
        return null;
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
        //        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.pieceType;
//        throw new RuntimeException("Not implemented");
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", pieceType=" + pieceType +
                ", movesCalculator=" + movesCalculator +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, pieceType, movesCalculator);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        throw new RuntimeException("Not implemented");
        //following line is temporary
        System.out.println("In pieceMoves");
        System.out.println(this.movesCalculator.calculateMoves(board, myPosition));
        return this.movesCalculator.calculateMoves(board, myPosition);

        // TODO in piece move calculator calculate all possible moves given piece type
        // TODO: in piece move calculator look at board, see possible moves given rules
        // TODO here, set the piece to the new location, set old location to null

        // TODO return current board as array

//        return new ArrayList<>();
    }
}
