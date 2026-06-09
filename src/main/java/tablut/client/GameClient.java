package tablut.client;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.ki.SearchMoves;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {

    private Socket socket;
    private BufferedReader receive;
    private PrintWriter write;
    private Board board;
    private int colorGame = -1;

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

        System.out.println("Bist du Ersteller? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.equals("yes") || ans.equals("j") || ans.equals("ja")) {
            sendMessage("start");
        }

        waitForConfig();
        gameConfig();
        runGame();
        socket.close();
    }

    private void waitForConfig() throws IOException {
        String msg = receiveMessage();
        while (!msg.equals("config")) {
            System.out.println("Warte auf Spielstart: " + msg);
            msg = receiveMessage();
        }
        System.out.println("Config empfangen.");
    }

    private void gameConfig() throws IOException {
        String msg = receiveMessage();
        while (!msg.equals("ok")) {
            System.out.println("Config: " + msg);
            if (msg.startsWith("set start_pos ")) {
                String startPos = msg.substring("set start_pos ".length()).trim();
                if (!startPos.isEmpty()) {
                    board = FenParser.serverFenparse(startPos);
                    System.out.println("Startposition gesetzt.");
                }
            }
            msg = receiveMessage();
        }
        sendMessage("ok");
        System.out.println("Config bestätigt!");
    }

    public void runGame() throws IOException {
        String firstMsg = receiveMessage();

        if (firstMsg.equals("start")) {
            colorGame = 0;
            System.out.println("Ich bin BLACK");
            kiMakeMove();
        } else if (firstMsg.equals("wait")) {
            colorGame = 1;
            System.out.println("Ich bin WHITE");
        } else {
            throw new IOException("Unbekannte Nachricht Config:" + firstMsg);
        }

        while (true) {
            String msg = receiveMessage();

            if (msg.startsWith("over")) {
                System.out.println("Spiel vorbei: " + msg);
                break;
            }

            if (msg.startsWith("move")) {
                handleOpponentMove(msg);
                board.printBoard();
                kiMakeMove();
                continue;
            }

            if (msg.startsWith("err")) {
                System.out.println("Fehler vom Server: " + msg);
                break;
            }

            System.out.println("Unbekannte Nachricht: " + msg);
        }
    }

    private void handleOpponentMove(String msg) {
        String[] parts = msg.split("[ ,]");
        if (parts.length != 5) {
            System.out.println("Ungültiges move-Format: " + msg);
            return;
        }
        try {
            int fromRow = Integer.parseInt(parts[1]) + 1;
            int fromCol = Integer.parseInt(parts[2]) + 1;
            int toRow = Integer.parseInt(parts[3]) + 1;
            int toCol = Integer.parseInt(parts[4]) + 1;
            System.out.println("Gegner zieht: (" + fromRow + "," + fromCol + ") -> (" + toRow + "," + toCol + ")");

            // Sicherstellen dass moveFigure mit richtigem Turn aufgerufen wird
            board.playBlackTurn = (colorGame == 1);

            Move move = new Move(fromRow, fromCol, toRow, toCol);
            MoveFactory.moveFigure(board, move );
            board.playBlackTurn = (colorGame == 0);

        } catch (NumberFormatException e) {
            System.out.println("Fehler Parsen: " + msg);
        }
    }

    public void kiMakeMove() throws IOException {
        Move move = SearchMoves.findBestMoveAlphaBeta(board, 5000);
        if (move == null) {
            System.out.println("Kein Zug möglich!");
            return;
        }

        // Zug senden noch nicht auf dem Board ausführen
        int sendFromRow = move.fromX - 1;
        int sendFromCol = move.fromY - 1;
        int sendToRow = move.toX - 1;
        int sendToCol = move.toY - 1;
       sendMessage("move " + sendFromRow + "," + sendFromCol + "," + sendToRow + "," + sendToCol);

        // Auf Server  warten
        String response = receiveMessage();

        if (response.startsWith("time")) {
            // jz Board aktualisieren
            MoveFactory.moveFigure(board, move);
            System.out.println("Zug akzeptiert, Zeit: " + response.split(" ")[1] + "s");
            System.out.println("Mein Zug: (" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")");
            board.printBoard();
        } else if (response.startsWith("over")) {
            System.out.println("Spiel vorbei: " + response);
        } else {
            System.out.println("Zug abgelehnt: " + response);
        }
    }

    public void sendMessage(String msg) {
        write.println(msg);
        System.out.println("--> " + msg);
    }

    public String receiveMessage() throws IOException {
        String message = receive.readLine();
        if (message == null) throw new IOException("Verbindung unterbrochen!");
        System.out.println("<-- " + message);
        return message;
    }
}