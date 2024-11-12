import chess.*;

public class Main {

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);


    }
    public void displayPreLoginPrompt(){
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");
        System.out.println("Options:");
        System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
        System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
        System.out.println("Exit the program: \"q\", \"quit\"");
        System.out.println("Print this message: \"h\", \"help\"");
    }
}