package ui;


import model.GameData;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;
public class Repl implements ServerMessageObserver{
    private final ChessClient client;
    private Renderer renderer;


    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl, this);
    }

    public void run(){
        printPrompt();
        System.out.println(SET_TEXT_COLOR_BLUE +"\n♕ Welcome to Chess. Type Help to get started. ♕");
//        System.out.println(client.help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                System.out.println(SET_TEXT_COLOR_BLUE + "Error: " + e.getMessage());
//                var msg = e.toString();
//                System.out.print(msg);
            }
        }
        System.out.println();

    }

    private void printPrompt() {
        System.out.print(RESET_TEXT_COLOR + client.getState() + " >>> " + SET_TEXT_COLOR_GREEN);
    }


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;

                GameData gameData = loadGameMessage.getGame();
                String playerColor = client.getPlayerColor();
                renderer = new Renderer(gameData.game());
                System.out.println("\n"+ renderer.getRender(playerColor));
                // Let Chess Client deal with logic? so that we do need to rewrite everything...

            }
            case NOTIFICATION -> {
                NotificationMessage notification = (NotificationMessage) message;
                System.out.println(notification.getMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(errorMessage.getErrorMessage());

            }
            default -> {
                System.out.println("Unhandled message type: " + message.getServerMessageType());
            }
        }
    }

//    @Override
//    public void notify(ServerMessage message) {
//        System.out.println(SET_TEXT_COLOR_GREEN + message);
//        printPrompt();
//
//    }
}
