package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Objects;

public class Renderer {
    private static StringBuilder sb;
    private static ChessBoard chessBoard;
    private static GameData gameData;
    private String username;


    public Renderer(GameData gameData, String username){
        this.gameData = gameData;
        chessBoard = gameData.game().getBoard();
        this.username = username;

    }
    public String getRender(){
        sb = new StringBuilder();
        if (Objects.equals(gameData.blackUsername(), username)){
            buildBlackBoard();
        }
        else{
            buildWhiteBoard();
        }
        return sb.toString();
    }

    private static void buildWhiteBoard() {
        writeHeader(ChessGame.TeamColor.BLACK);
        for (int row = 8; row >=1; row--) {
            writeRowNum(row);
            for (int col = 1; col <= 8; col++) {//int col = 8; col >= 1; col--) {
                buildBoard(row, col, chessBoard);
            }
            writeRowNum(row);
            sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }
        writeHeader(ChessGame.TeamColor.BLACK);
    }

    private static void buildBlackBoard() {
        writeHeader(ChessGame.TeamColor.WHITE);
        for (int row = 1; row <= 8; row++) {
            writeRowNum(row);
            for (int col = 8; col >= 1; col--) {//int col = 1; col <= 8; col++) {
                buildBoard(row, col, chessBoard);
            }
            writeRowNum(row);
            sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }
        writeHeader(ChessGame.TeamColor.WHITE);
    }

    private static void buildBoard(int row, int col, ChessBoard chessBoard) {
        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = chessBoard.getPiece(position);
        boolean whiteSquare = (row + col) % 2 == 1;

        // Set square color
        if (whiteSquare) {
            sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_YELLOW);//WHITE);
        } else {
            sb.append(EscapeSequences.SET_BG_COLOR_DARK_OLIVE_GREEN3);//GREY);
        }

        //fill in the squares
        if (piece == null){
            sb.append(EscapeSequences.EMPTY);
            sb.append(EscapeSequences.RESET_BG_COLOR);
        }
        else {
            drawPiece(piece);
            sb.append(EscapeSequences.RESET_TEXT_COLOR);
        }
    }

    private static void writeRowNum(int row) {
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        sb.append(" ").append(row).append("\u2003");//9 - row).append("\u2003");
    }

    private static void writeHeader(ChessGame.TeamColor color) {
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        if (color == ChessGame.TeamColor.WHITE){
            sb.append("  \u2003 h\u2003 g\u2003 f\u2003 e\u2003 d\u2003 c\u2003 b\u2003 a\u2003  \u2003");
        }
        else{
            sb.append("  \u2003 a\u2003 b\u2003 c\u2003 d\u2003 e\u2003 f\u2003 g\u2003 h\u2003  \u2003");
        }
        sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");
    }

    private static void drawPiece(ChessPiece piece) {
        sb.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        if (Objects.requireNonNull(piece.getTeamColor()) == ChessGame.TeamColor.WHITE) {
            switch (piece.getPieceType()) {
                case KING:
                    sb.append(EscapeSequences.WHITE_KING);
                    break;
                case QUEEN:
                    sb.append(EscapeSequences.WHITE_QUEEN);
                    break;
                case BISHOP:
                    sb.append(EscapeSequences.WHITE_BISHOP);
                    break;
                case KNIGHT:
                    sb.append(EscapeSequences.WHITE_KNIGHT);
                    break;
                case ROOK:
                    sb.append(EscapeSequences.WHITE_ROOK);
                    break;
                case PAWN:
                    sb.append(EscapeSequences.WHITE_PAWN);
                    break;
                default:
                    sb.append(EscapeSequences.EMPTY);
                    break;
            }
        }
        else{
            switch (piece.getPieceType()) {
                case KING:
                    sb.append(EscapeSequences.BLACK_KING);
                    break;
                case QUEEN:
                    sb.append(EscapeSequences.BLACK_QUEEN);
                    break;
                case BISHOP:
                    sb.append(EscapeSequences.BLACK_BISHOP);
                    break;
                case KNIGHT:
                    sb.append(EscapeSequences.BLACK_KNIGHT);
                    break;
                case ROOK:
                    sb.append(EscapeSequences.BLACK_ROOK);
                    break;
                case PAWN:
                    sb.append(EscapeSequences.BLACK_PAWN);
                    break;
                default:
                    sb.append(EscapeSequences.EMPTY);
                    break;
            }
        }
    }


}