package tablut;

import java.util.List;
import java.util.Scanner;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.client.GameClient;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.ki.SearchMoves;

public class Main {

    private static final String host = "localhost";
    private static final int port = 5000;
    private static final String token = "";
    private static final String lobby = "F";

    public static void main(String[] args) throws Exception {

        System.out.println("Tablut KI ");
        System.out.println("1. Gameserver");
        System.out.println("2. KI vs KI");
        System.out.println("3. KI vs KI - Manueller FenString");
        System.out.println("4. Du vs KI");
        System.out.println("5. Du vs Du (Gut um Spiel zu Testen/Debugging)");
        System.out.print("Wahl: (Gebe die Nummer an)  ");

        Scanner scanner = new Scanner(System.in);
        int wahl = scanner.nextInt();

        switch (wahl) {
            case 1 -> gameserver();
            case 2 -> kiVsKi(new Board());
            case 3 -> kiVsKi_FenString();
            case 4 -> menschVsKi();
            case 5 -> menschVsMensch();
            default -> System.out.println("Ungültige Wahl!");
        }
    }

    public static void gameserver() throws Exception {

        System.out.println("Verbinde mit GameServer!");
        GameClient client = new GameClient(host, port);
        System.out.print("Token eingeben: ");
        Scanner scanner = new Scanner(System.in);
        String token1 = scanner.nextLine();
        client.runConnection(token1, lobby);
    }

    public static void kiVsKi(Board board) {
        System.out.println("kiVsKi");
        board.printBoard();

        while (!GameLogic.isGameOver(board)) {
            Move move = SearchMoves.findBestMoveAlphaBeta(board, 10000000);

            String farbe = board.playBlackTurn ? "Schwarz" : "Weiß";
            if (move == null) {
                System.out.println("Kein Zug möglich!" + farbe + " hat verloren!");
                break;
            }
            GameLogic.moveFigure(board, move.fromX, move.fromY, move.toX, move.toY);
            System.out.println(farbe + " Zug: " + move.fromX + "," + move.fromY + "---> " + move.toX + "," + move.toY);

            board.printBoard();
        }
        System.out.println("Das Spiel ist vorbei!");
        GameLogic.gameEnd(board);
    }

    public static void kiVsKi_FenString() {
        System.out.println("Gebe einen FenString ein: ");
        Scanner scanner = new Scanner(System.in);
        String fenString = scanner.nextLine();

        //TestFenSt:
        String startstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        String whiteWins = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
        String blackWins = "4r4/1r2r4/2r1Kr3/3rRr3/9/2R1r2R1/9/4r4/9 w 0 1";

        Board board = FenParser.parse(fenString);
        kiVsKi(board);
    }

    public static void menschVsKi() {

        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Du vs KI");

        board.printBoard();
        while (!GameLogic.isGameOver(board)) {

            if (board.playBlackTurn) {

                System.out.print("Dein Zug: ");
                System.out.println("fromRow");
                int fromRow = scanner.nextInt();
                System.out.println("fromCol");
                int fromCol = scanner.nextInt();
                System.out.println("toRow");
                int toRow = scanner.nextInt();
                System.out.println("toCol");
                int toCol = scanner.nextInt();

                GameLogic.moveFigure(board, fromRow, fromCol, toRow, toCol);
            } else {

                Move move = SearchMoves.makeRandomMove(board);

                if (move == null) {
                    System.out.println("Kein Zug möglich!" + "Ki" + " hat verloren!");
                    break;
                }
                GameLogic.moveFigure(board, move.fromX, move.fromY, move.toX, move.toY);
                System.out.println("Ki" + " Zug: " + move.fromX + "," + move.fromY + "---> " + move.toX + "," + move.toY);
            }
            board.printBoard();
        }

        System.out.println("Das Spiel ist vorbei!");
        GameLogic.gameEnd(board);
    }

    public static void menschVsMensch() {
        Board board = new Board();
        Scanner scanner = new Scanner(System.in);

        board.printBoard();
        while (!GameLogic.isGameOver(board)) {

            System.out.print((board.playBlackTurn ? "Schwarz" : "Weiß") + " am Zug: ");

            System.out.print("Dein Zug: ");
            System.out.println("fromRow");
            int fromRow = scanner.nextInt();
            System.out.println("fromCol");
            int fromCol = scanner.nextInt();
            System.out.println("toRow");
            int toRow = scanner.nextInt();
            System.out.println("toCol");
            int toCol = scanner.nextInt();

            GameLogic.moveFigure(board, fromRow, fromCol, toRow, toCol);

            board.printBoard();
        }
        System.out.println("Das Spiel ist vorbei!");
        GameLogic.gameEnd(board);
    }
}

