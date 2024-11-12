package ui;

import server.Server;

import java.util.Scanner;
import static ui.EscapeSequences.*;
public class Repl {
    private final ChessClient client;


    public Repl(String serverUrl) {
        this.client = new ChessClient(serverUrl);
    }

    public void run(){
        printPrompt();
        System.out.println("♕ Welcome to Chess. Type Help to get started. ♕");
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
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}
