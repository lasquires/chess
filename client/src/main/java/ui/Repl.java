package ui;


import websocket.messages.ServerMessage;

import java.util.Scanner;
import static ui.EscapeSequences.*;
public class Repl implements ServerMessageObserver{
    private final ChessClient client;


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
        System.out.print("\n" + RESET_TEXT_COLOR + client.getState() + " >>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println(SET_TEXT_COLOR_GREEN + message);
        printPrompt();
    }
}
