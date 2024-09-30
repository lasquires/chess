package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        var pieceColor = board.getPiece(myPosition).getTeamColor();

        int startRow = 2;
        int promotionRow= 8;
        int modifier = 1;
        if (pieceColor== ChessGame.TeamColor.BLACK){
            startRow = 7;
            promotionRow= 1;
            modifier = -1;
        }

        //move straight
        ChessPosition newPosition = myPosition.move(modifier,0);
        if (board.IsEmpty(newPosition)){
            CheckPromotions(myPosition, moves, promotionRow, newPosition);
            newPosition = myPosition.move(2*modifier,0);
            if (board.IsEmpty(newPosition)&&
                    myPosition.getRow()==startRow){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

        //attack right
        newPosition = myPosition.move(modifier, modifier);
        if (board.IsEnemy(myPosition, newPosition)){
            CheckPromotions(myPosition, moves, promotionRow, newPosition);
        }

        //attack left
        newPosition = myPosition.move(modifier, -1*modifier);
        if (board.IsEnemy(myPosition, newPosition)){
            CheckPromotions(myPosition, moves, promotionRow, newPosition);
        }




        return moves;
    }

    private void CheckPromotions(ChessPosition myPosition, Collection<ChessMove> moves, int promotionRow, ChessPosition newPosition) {
        if (newPosition.getRow()==promotionRow){
            moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
        }
        else{
            moves.add(new ChessMove(myPosition, newPosition,null));
        }
    }

}
