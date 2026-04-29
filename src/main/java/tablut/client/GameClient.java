package tablut.client;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.ki.SearchMoves;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

    private Socket socket;
    private BufferedReader receive;
    private PrintWriter write;
    private Board board;

    private int colorGame;

    public GameClient(String host, int port) throws IOException {
        board = new Board();
        socket = new Socket(host, port);
        write = new PrintWriter(socket.getOutputStream(), true);
        receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));

    }


    public void runConnection(String token, String lobby) throws IOException {

        sendMessage("gspy");
        String msg = receiveMessage();
        if (!msg.equals("ok")) throw new IOException("Handshake fehlgeschlagen: " + msg);

        sendMessage("login " + token);
        msg = receiveMessage();
        if (!msg.equals("ok")) throw new IOException("login fehlgeschlagen: " + msg);

        sendMessage("join " + lobby);
        msg = receiveMessage();
        if (!msg.equals("ok")) throw new IOException("join fehlgeschlagen: " + msg);

        sendMessage("start");
        gameConfig();
        runGame();
        socket.close();

    }


    public void runGame() throws IOException {


        if (colorGame == 0) {
            kiMakeMove();
        }

        while (true) {

            String msg = receiveMessage();

            if (msg == null) {
                System.out.println("Fehler");
                throw new IOException("Message Null");
            }

            if (msg.startsWith("over")) {
                System.out.println("Spiel vorbei: " + msg);
                break;
            }


            if (msg.startsWith("move")) {
                String[] moves = msg.split("[ ,]");
                GameLogic.moveFigure(board, Integer.parseInt(moves[1]), Integer.parseInt(moves[2]), Integer.parseInt(moves[3]), Integer.parseInt(moves[4]));
                board.printBoard();
                kiMakeMove();
            }


            if (msg.startsWith("time")) {
                System.out.println("Valider Zug, Zeit: " + msg);
            }


        }


    }

    private void gameConfig() throws IOException {
        String msg = receiveMessage();

        while (!msg.equals("ok")) {
            System.out.println("Config: " + msg);


            if (msg.startsWith("set player")) {
                colorGame = Integer.parseInt(msg.split(" ")[2]);
                if (colorGame == 0) {
                    System.out.println("Ich bin: Schwarz");
                } else {
                    System.out.println("Ich bin: Weiß");
                }
            }

            msg = receiveMessage();
        }
        sendMessage("ok");
        System.out.println("Spiel startet!");
    }


    public void kiMakeMove() {

        Move move = SearchMoves.makeRandomMove(board);

        if (move == null) {
            System.out.println("Kein Valider Zug Möglich");
            return;
        }

        GameLogic.moveFigure(board, move.fromX, move.fromY, move.toX, move.toY);


        sendMessage("move " + move.fromX + "," + move.fromY + "," + move.toX + "," + move.toY);

        System.out.println("Mein Zug: " + move.fromX + "," + move.fromY + "---> " + move.toX + "," + move.toY);
        board.printBoard();

    }


    public void sendMessage(String msg) {
        write.println(msg);
        System.out.println("Send: " + msg);

    }

    public String receiveMessage() throws IOException {
        String message = receive.readLine();

        if (message == null) {
            throw new IOException("Verbindung zum Server unterbrochen!");
        }


        System.out.println("Receive: " + message);


        return message;


    }

}
