package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator{
    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPiece myPawn = board.getPiece(myPosition);
        ChessGame.TeamColor color = myPawn.getTeamColor();

        //Default is white, if black, it will change
        int startRow = 2;
        int modifier = 1;
        int promotionRow = 8;
        if (color== ChessGame.TeamColor.BLACK){
            startRow = 7;
            modifier = -1;
            promotionRow = 1;
        }

        List<int[]> pawnMoves = new ArrayList<>();

        if(myPawn.getMoveHistory().isEmpty() && board.isEmpty(myPosition.move(modifier,0))
                && board.isEmpty(myPosition.move(modifier,0))) {
            pawnMoves.add(new int[]{modifier * 1, 0});
        }

        //if the pawn hasn't moved and two spaces in front of it is empty it can move forward two
        if(myPawn.getMoveHistory().isEmpty()
                && myPosition.getRow()== startRow
                &&board.isEmpty(myPosition.move(modifier,0))
                && board.isEmpty(myPosition.move(2*modifier,0))){
            pawnMoves.add(new int[] {2*modifier,0});
        }

        //if there is an enemy, it can move diagonally to capture
        ChessPosition Rdiagonal = myPosition.move(modifier,modifier);
        ChessPosition Ldiagonal = myPosition.move(modifier,modifier*-1);

        if (board.isEnemy(myPosition, Rdiagonal)){
            pawnMoves.add(new int[] {modifier,modifier});
        }
        if(board.isEnemy(myPosition,Ldiagonal)){
            pawnMoves.add(new int[]{modifier,modifier*-1});
        }


        int[][] pawnMovesArray = pawnMoves.toArray(new int[0][]);
        for (int[] direction : pawnMovesArray){
            ChessPosition newPosition = myPosition;
            System.out.println("Calculating move from " + myPosition + " to " + newPosition);


            newPosition = newPosition.move(direction[0], direction[1]);

            if (!board.isValidPosition(newPosition)){
                continue;  //off board
            }

            if (board.isEmpty(newPosition)){
                if(newPosition.getRow()==promotionRow){
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                }
                else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            //capture enemy, then stop
            else if (board.isEnemy(myPosition, newPosition)) {
                if(newPosition.getRow()==promotionRow){
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                }
                else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }else{ //ally in the way, stop
                continue;
            }

        }
        return moves;


//        return ChessUtils.calculateSingleMoves(board, myPosition, pawnMovesArray, ChessPiece.PieceType.PAWN);
    }
}
