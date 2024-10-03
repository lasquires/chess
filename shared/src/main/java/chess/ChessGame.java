package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor turn;

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", turn=" + turn +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turn);
    }

    public ChessGame() {
        this.board = new ChessBoard();
        this.turn = TeamColor.WHITE;
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        var piece = board.getPiece(startPosition);
        if (piece == null || piece.getTeamColor() != this.turn){
            return null;
        }
        return piece.pieceMoves(board, startPosition);
        //TODO: add in logic for check, castling, and en passent
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        var startPos = move.getStartPosition();
        var endPos = move.getEndPosition();
        if (!validMoves(startPos).contains(move)){
            throw new InvalidMoveException("Invalid move");
        }
        //else make the move
        var piece = board.getPiece(startPos);
        board.addPiece(endPos, piece); //move
        board.addPiece(startPos, null); //clear old
        //TODO: add in logic for check, castling, and en passent
        //switch turns
        if (this.turn==TeamColor.WHITE){
            this.turn=TeamColor.BLACK;
        }
        else{
            this.turn=TeamColor.WHITE;
        }



    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //get position of king of given color
        var kingPos = findKing(teamColor);
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                var pos = new ChessPosition(row, col);
                var piece = board.getPiece(pos);
                if (piece!=null && piece.getTeamColor()!=teamColor){
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board, pos);
                    for (var move: possibleMoves){
                        if (move.getEndPosition().equals(kingPos)){     //I hate how nested this is but oh well;
                            return true; //is in check;
                        }
                    }
                }
            }
        }
        return false;

    }

    private chess.ChessPosition findKing(TeamColor teamColor) {
        for (int row=1; row<=8; row++){
            for (int col=1;col<=8;col++){
                var pos = new ChessPosition(row, col);
                var piece = board.getPiece(pos);
                if (piece!= null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor()== teamColor){
                    return pos;
                }
            }
        }
        throw new IllegalStateException("King not found");
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
