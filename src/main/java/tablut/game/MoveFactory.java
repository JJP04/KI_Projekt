package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.ArrayList;
import java.util.List;

//Objekt mit allen Zügen
public class MoveFactory {

    //Liste mit allen Zügen, für jede Figur
    public List<Move> getAllMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                if (isOwnFigur(board, x, y)) {
                    moves.addAll(getFigurMoves(board, x, y));
                }
            }
        }
        return moves;
    }

    //Alle Moves für eine Figur
    private List<Move> getFigurMoves(Board board, int x, int y) {
        List<Move> figureMoves = new ArrayList<>();

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int nx = x + direction[0];
            int ny = y + direction[1];

            while (GameLogic.isleagalField(board, nx, ny)) {
                figureMoves.add(new Move(x, y, nx, ny));

                nx += direction[0];
                ny += direction[1];
            }
        }
        return figureMoves;
    }

    //Überprüfung ob die Figur zum Spieler gehört, der am Zug ist
    private boolean isOwnFigur(Board board, int x, int y) {
        if (board.playingBoard[x][y] == 1 && board.playBlackTurn) {
            return true; //schwarzer Zug
        } else if ((board.playingBoard[x][y] == -1 || board.playingBoard[x][y] == board.KING) && !board.playBlackTurn) { //weiße Figur oder König
            return true; //weißer Zug
        }
        return false;
    }
}
