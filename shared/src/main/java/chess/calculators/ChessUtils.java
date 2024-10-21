package chess.calculators;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.ArrayList;
import java.util.Collection;

public class ChessUtils {
    public static Collection<ChessMove> calculateLinearMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction: directions){
            ChessPosition newPosition = myPosition;
            while (true){
                newPosition = newPosition.move(direction[0],direction[1]);

                if(!board.isOnBoard(newPosition)){
                    break;//isn't on board.
                }
                if(board.isEmpty(newPosition)){
                    moves.add(new ChessMove(myPosition, newPosition, null));//addmove and continue;
                }
                else if (board.isEnemy(myPosition, newPosition)){
                    moves.add(new ChessMove(myPosition,newPosition,null));//addMove then break;
                    break;
                } else{
                    break;
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> calculateSingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction: directions){
            ChessPosition newPosition = myPosition.move(direction[0],direction[1]);

            if(!board.isOnBoard(newPosition)){
                continue;//isn't on board.
            }
            if(board.isEmpty(newPosition)){
                moves.add(new ChessMove(myPosition, newPosition, null));//addmove and continue;
            }
            else if (board.isEnemy(myPosition, newPosition)){
                moves.add(new ChessMove(myPosition,newPosition,null));//addMove then break;
                continue;
            } else{
                continue;
            }
        }
        return moves;
    }
}
