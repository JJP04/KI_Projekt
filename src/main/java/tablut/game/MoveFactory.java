package tablut.game;

import tablut.board.Board;
import tablut.board.Move;
import tablut.ki.ZobristHashing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Objekt mit allen Zügen
public class MoveFactory {

    public static void moveFigure(Board board, Move move) {

        int figure = board.playingBoard[move.fromX][move.fromY];

        int x = move.fromX;
        int y = move.fromY;

        if (figure == Board.EMPTY || figure == Board.BORDER) {
            System.out.println("Keine Figur auf Feld");
            return;
        }
        board.playingBoard[move.fromX][move.fromY] = Board.EMPTY;
        board.playingBoard[move.toX][move.toY] = figure;

        if (figure == Board.BLACK) {
            board.blackSoldersPos[move.fromX][move.fromY] = false;
            board.blackSoldersPos[move.toX][move.toY] = true;
        } else if (figure == Board.WHITE) {
            board.whiteSoldersPos[move.fromX][move.fromY] = false;
            board.whiteSoldersPos[move.toX][move.toY] = true;
        } else if (figure == Board.KING) {
            board.kingPos[0] = move.toX;
            board.kingPos[1] = move.toY;
        }

        board.countMoves++;
        if (!board.isSearchCopy) {  // ← nur speichern wenn echtes Spiel
            GameLogic.savePosition(board);
        }

        //Schlagen Methode aufrufen
        GameLogic.toCapture(board, move, x, y);

        //Hash aktualisieren
        board.hash = ZobristHashing.updateHash(board.hash, figure, move.fromX, move.fromY, move.toX, move.toY);

        //Zug wechseln
        board.playBlackTurn = !board.playBlackTurn;
    }

    //Liste mit allen Zügen, für jede Figur
    public static List<Move> getAllMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        for (int x = 1; x < 10; x++) {
            for (int y = 1; y < 10; y++) {
                if (isOwnFigure(board, x, y)) {
                    moves.addAll(getFigurMoves(board, x, y));
                }
            }
        }
        return moves;
    }

    //Alle Moves für eine Figur
    public static List<Move> getFigurMoves(Board board, int x, int y) {
        List<Move> figureMoves = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int nx = x + direction[0];
            int ny = y + direction[1];

            while (GameLogic.islegalField(board, nx, ny, x, y)) {
                if (Board.throne[0] != nx || Board.throne[1] != ny) {
                    figureMoves.add(new Move(x, y, nx, ny));
                }
                nx += direction[0];
                ny += direction[1];
            }
        }
        return figureMoves;
    }

    //Überprüfung ob die Figur zum Spieler gehört, der am Zug ist
    private static boolean isOwnFigure(Board board, int x, int y) {
        if (board.playingBoard[x][y] == 1 && board.playBlackTurn) {
            return true; //schwarzer Zug
        } else if ((board.playingBoard[x][y] == -1 || board.playingBoard[x][y] == board.KING) && !board.playBlackTurn) { //weiße Figur oder König
            return true; //weißer Zug
        }
        return false;
    }

    //Zum Testen: Zufälligen Zug generieren
    public static Move makeRandomMove(Board b) {
        //Optimieren als Array Später
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        if (moves.isEmpty()) return null;
        Random rand = new Random();
        Move randomMove = moves.get(rand.nextInt(moves.size()));

        return randomMove;
    }

}
