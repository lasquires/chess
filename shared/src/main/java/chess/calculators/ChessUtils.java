package chess.calculators;
import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.ArrayList;
import java.util.Collection;

public class ChessUtils {
    public static Collection<ChessMove> CalculateLinearMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction: directions){
            ChessPosition newPosition = myPosition;
            while (true){
                newPosition = newPosition.move(direction[0],direction[1]);

                if(!board.IsOnBoard(newPosition)){
                    break;//isn't on board.
                }
                if(board.IsEmpty(newPosition)){
                    moves.add(new ChessMove(myPosition, newPosition, null));//addmove and continue;
                }
                else if (board.IsEnemy(myPosition, newPosition)){
                    moves.add(new ChessMove(myPosition,newPosition,null));//addMove then break;
                    break;
                } else{
                    break;
                }
            }
        }
        return moves;
    }

    public static Collection<ChessMove> CalculateSingleMoves(ChessBoard board, ChessPosition myPosition, int[][] directions) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] direction: directions){
            ChessPosition newPosition = myPosition.move(direction[0],direction[1]);

            if(!board.IsOnBoard(newPosition)){
                continue;//isn't on board.
            }
            if(board.IsEmpty(newPosition)){
                moves.add(new ChessMove(myPosition, newPosition, null));//addmove and continue;
            }
            else if (board.IsEnemy(myPosition, newPosition)){
                moves.add(new ChessMove(myPosition,newPosition,null));//addMove then break;
                continue;
            } else{
                continue;
            }
        }
        return moves;
    }
}
