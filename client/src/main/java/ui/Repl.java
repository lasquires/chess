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

                client.drawBoard(gameData);
                System.out.println(SET_TEXT_COLOR_GREEN + client.drawBoard(gameData));

            }
            case NOTIFICATION -> {
                NotificationMessage notification = (NotificationMessage) message;
                System.out.println(SET_TEXT_COLOR_GREEN + "\n" + notification.getMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(SET_TEXT_COLOR_GREEN + "\n" + errorMessage.getErrorMessage());

            }
            default -> System.out.println(SET_TEXT_COLOR_GREEN + "\n" + "unknown message type: " + message.getServerMessageType());
        }
        printPrompt();
    }

}
