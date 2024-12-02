package websocket.commands;

public class MakeMoveCommand extends UserGameCommand{

    public MakeMoveCommand(String authToken, Integer gameID) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }
}
