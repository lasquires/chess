package chess;

import java.util.ArrayList;
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
        if (piece == null){// || piece.getTeamColor() != this.turn){
            return new ArrayList<>();
        }
        var possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        var kingPos = findKing(piece.getTeamColor());
        boolean kingInCheck = isInCheck(piece.getTeamColor());

        for (var move: possibleMoves){
            ChessBoard simBoard= new ChessBoard(board);
            simBoard.addPiece(move.getEndPosition(), piece);
            simBoard.addPiece(startPosition, null);

            //make game simulation--not just board, to check for color
            ChessGame simGame = new ChessGame();
            simGame.setBoard(simBoard);

            if(kingInCheck){//if king in check in game
                if (!simGame.isInCheck(piece.getTeamColor())){ //and if the sim game doesn't have him in check
                    validMoves.add(move);
                }
            }else{
                //add in all moves that don't put the king in check
                if (!simGame.isInCheck(piece.getTeamColor())){ //and if the sim game doesn't have him in check
                    validMoves.add(move);
                }
            }

        }

        return validMoves;
        //TODO: add in logic for check, castling, and en passant
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
        if(move.getPromotionPiece()!=null){
            board.addPiece(endPos,new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }

        board.addPiece(startPos, null); //clear old
        //TODO: add in logic for check, castling, and en passant
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
        if (kingPos==null){
            return false;
        }
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                var pos = new ChessPosition(row, col);
                var piece = board.getPiece(pos);
                if (piece!=null && piece.getTeamColor()!=teamColor){
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(board,pos);
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
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor)){
            //simulate all possible moves for all pieces where piece.teamColor()=teamColor;
            return simulateAllMoves(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //for move in all moves, if newPosition isInCheck and startPosition !isInCheck, return true;
        if(!isInCheck(teamColor)){
            //simulate all possible moves for all pieces where piece.teamColor()=teamColor;
            //will return if is in stalemate
            return simulateAllMoves(teamColor);
        }
        return false;
    }

    private boolean simulateAllMoves(TeamColor teamColor) {
        for (int row=1;row<=8;row++){
            for (int col=1;col<=8;col++){
                ChessPosition pos = new ChessPosition(row,col);
                ChessPiece piece= board.getPiece(pos);
                if (piece != null && piece.getTeamColor()==teamColor){
                    //for move in moves, simulate, then see if the king is in check
                    Collection<ChessMove> possibleMoves = validMoves(pos);
                    for (var move: possibleMoves){
                        ChessBoard simBoard = new ChessBoard(board);
                        simBoard.addPiece(move.getEndPosition(), piece);//add move
                        simBoard.addPiece(pos,null);//clear old
                        if (!isInCheck(teamColor)){
                            return false; //can make a move that gets doesn't result in check
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board=board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
