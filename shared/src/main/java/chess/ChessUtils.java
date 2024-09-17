package chess;

import java.util.ArrayList;
import java.util.Collection;

public class ChessUtils {
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
                    moves.add(new ChessMove(myPosition, newPosition, pieceType));
                    }
                //capture enemy, then stop
                else if (board.isEnemy(myPosition, newPosition)) {
                    moves.add(new ChessMove(myPosition, newPosition, pieceType)); //capture
                    break;
                }else{ //ally in the way, stop
                    break;
                }
            }
        }
        return moves;
    }

}
