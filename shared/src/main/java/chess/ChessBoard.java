package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //throw new RuntimeException("Not implemented");
        squares[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
//        throw new RuntimeException("Not implemented");
        return squares[position.getRow()][position.getColumn()];

    }

    public boolean isEnemy(ChessPosition myPosition, ChessPosition newPosition){
        if (getPiece(newPosition)!=null){
            return (getPiece(myPosition).getTeamColor()!=getPiece(newPosition).getTeamColor());
        }
        else{
            return false;}
    }

    /** Returns boolean whether or not a position is valid
     *
     */
    public boolean isValidPosition(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();

        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isEmpty(ChessPosition position){
        return (getPiece(position)) == null;
    }


    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                squares[row][col] = null;
            }
        }

//        throw new RuntimeException("Not implemented");
    }
}
