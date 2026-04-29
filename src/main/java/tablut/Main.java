package tablut;

import java.util.List;
import java.util.Scanner;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.GameClient;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.ki.SearchMoves;

public class Main {

    private static final String host = "localhost";
    private static final int port = 5000;
    private static final String token = "gruppeF";
    private static final String lobby = "lobbyF";


    public static void main(String[] args) throws Exception {
//        Board b = new Board();
//        // b.printBoard();
//
//        MoveFactory m = new MoveFactory();
//        List<Move> moves = m.getAllMoves(b);
//        System.out.println(moves.size());
//
//        for (Move move : moves) {
//            System.out.println(
//                    "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")"
//            );
//

        //Menü
        System.out.println("Tablut KI ");
        System.out.println("1. Gameserver");
        System.out.println("2. KI vs KI");
        System.out.println("3. Du vs KI");
        System.out.println("4. Du vs Du (Gut um Spiel zu Testen/Debugging)");
        System.out.print("Wahl: (Gebe die Nummer an)  ");

        Scanner scanner = new Scanner(System.in);
        int wahl = scanner.nextInt();

        switch (wahl) {
            case 1 -> gameserver();
            case 2 -> kiVsKi();
            case 3 -> menschVsKi();
            case 4 -> menschVsMensch();
            default -> System.out.println("Ungültige Wahl!");
        }


    }


    public static void gameserver() throws Exception {
        System.out.println("Verbinde mit GameServer!");
        GameClient client = new GameClient(host, port);
        client.runConnection(token, lobby);
    }

    public static void kiVsKi() {
        System.out.println("kiVsKi");

        Board board = new Board();
        board.printBoard();

        while (!GameLogic.isGameOver(board)) {
            Move move = SearchMoves.makeRandomMove(board);

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
        gameEnd(board);


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
        gameEnd(board);


    }

    public static void menschVsMensch() {
        Board board = new Board();
        Scanner scanner = new Scanner(System.in);


        board.printBoard();
        while (!GameLogic.isGameOver(board)) {

            System.out.print((board.playBlackTurn ? "Schwarz" : "Weiß") + " am Zug: ");

//            //Nur für Test
//            MoveFactory factory = new MoveFactory();
//            List<Move> moves = factory.getAllMoves(board);
//            if (moves.isEmpty()) {
//                System.out.println("Verloren Kein zug Möglich");
//                break;
//            }


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
        gameEnd(board);


    }

    //Maybe Lieber in GameLogic
    private static void gameEnd(Board board) {
        System.out.println("Spielende nach " + board.countMoves + " Zügen");
        if (GameLogic.whiteWin(board)) System.out.println("Weiß gewinnt!");
        else if (GameLogic.blackWin(board)) System.out.println("Schwarz gewinnt!");
        else System.out.println("Unentschieden!");
    }
}

