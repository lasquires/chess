package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessUtils {



    //for the "move until" things (Queen, Rook, Bishop)
    public static Collection<ChessMove> calculateLinearMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, ChessPiece.PieceType pieceType){
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction : directions){
            ChessPosition newPosition = myPosition;
            System.out.println("Calculating move from " + myPosition + " to " + newPosition);

            while (true){
                newPosition = newPosition.move(direction[0], direction[1]);

                if (!board.isValidPosition(newPosition)){
                    break;  //off board
                }

                if (board.isEmpty(newPosition)){
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                //capture enemy, then stop
                else if (board.isEnemy(myPosition, newPosition)) {
                    moves.add(new ChessMove(myPosition, newPosition, null)); //capture
                    break;
                }else{ //ally in the way, stop
                    break;
                }
            }
        }
        return moves;
    }


    //for the "move once" things (King, Knight, Pawn (under special circumstance)
    public static Collection<ChessMove> calculateSingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, ChessPiece.PieceType pieceType){
        Collection<ChessMove> moves = new ArrayList<>();
        for (int[] direction : directions){
            ChessPosition newPosition = myPosition;
            System.out.println("Calculating move from " + myPosition + " to " + newPosition);


            newPosition = newPosition.move(direction[0], direction[1]);

            if (!board.isValidPosition(newPosition)){
                continue;  //off board
            }

            if (board.isEmpty(newPosition)){
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            //capture enemy, then stop
            else if (board.isEnemy(myPosition, newPosition)) {
                moves.add(new ChessMove(myPosition, newPosition, null)); //capture
                continue;
            }else{ //ally in the way, stop
                continue;
            }

        }
        return moves;
    }

}
