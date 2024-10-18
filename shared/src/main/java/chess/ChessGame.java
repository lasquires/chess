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

        if (piece == null){//if the position doesn't have a piece, it can't move
            return new ArrayList<>();
        }

        //what piece's rules say the piece can do
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        //what it actually can do given the boardstate
        Collection<ChessMove> validMoves = new ArrayList<>();

        //TODO: If king is not in check, and the piece in this position
        // is a king or a rook, add castle to possibleMoves
        //TODO: if can en passant, add to possibleMoves

        for (ChessMove move: possibleMoves){
            //get temporary boardState to manipulate
            ChessBoard simBoard= new ChessBoard(board);

            //make the move on the simulation board
            simBoard.addPiece(move.getEndPosition(), piece); //promotion is irrelevant
            simBoard.addPiece(startPosition, null);

            //make game simulation based on the board
            ChessGame simGame = new ChessGame();
            simGame.setBoard(simBoard);

            //if the move didn't put the king in check, add the move
            if (!simGame.isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }

        }

        return validMoves;
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
        var piece = board.getPiece(startPos);
        if (piece == null){
            throw new InvalidMoveException("No piece found at this position");
        }
        if (piece.getTeamColor()!=this.turn){
            throw new InvalidMoveException("Not your turn");
        }
        if (!validMoves(startPos).contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }

        //TODO: if the move is for castling, do that logic here
        //TODO: if the move is for en passant, do that logic here


        board.addPiece(endPos, piece); //move
        if(move.getPromotionPiece()!=null){//lets user check for promotion
            board.addPiece(endPos,new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        board.addPiece(startPos, null); //clear old

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
        if (kingPos==null){//if for some reason the board doesn't have a king
            return false;
        }
        //simulates moves for all opponent's pieces
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

                        //test the game simulation instead
                        ChessGame simGame = new ChessGame();
                        simGame.setBoard(simBoard);

                        if (!simGame.isInCheck(teamColor)){
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
