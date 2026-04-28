package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.ArrayList;
import java.util.List;

//Objekt mit allen Zügen
public class MoveFactory {
    //Liste mit allen Zügen
    public List<Move> getAllMoves(Board board) {
        List<Move> moves = new ArrayList<>();
        for (int x = 0; x < 11; x++) {
            for (int y = 0; y < 11; y++) {
                if (isOwnPiece(board, x, y) && !isKing(board, x, y)) {
                    moves.addAll(getMovesFrom(board, false, x, y));
                } else if (isKing(board, x, y) && !board.playBlackTurn) {
                    moves.addAll(getMovesFrom(board, true, x, y));
                }
            }
        }
        return moves;
    }

    private boolean isOwnPiece(Board board, int x, int y) {
        if (board.playingBoard[x][y] == 1 && board.playBlackTurn) {
            return true;
        } else if (board.playingBoard[x][y] == -1 && !board.playBlackTurn) {
            return true;
        }
        return false;
    }

    private boolean isKing(Board board, int x, int y) {
        return board.playingBoard[x][y] == Board.KING;
    }


    private List<Move> getMovesFrom(Board board, boolean isKing, int x, int y) {
        List<Move> moves = new ArrayList<>();

        int[][] dirs = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1}
        };

        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];

            while (isBorderOrForbidden(isKing, nx, ny) && isEmpty(board, nx, ny)) {
                moves.add(new Move(x, y, nx, ny));

                nx += d[0];
                ny += d[1];
            }
        }
        return moves;
    }

    private boolean isBorderOrForbidden(boolean isKing, int x, int y) {
        // Ecken
        if (!isKing && (x == 1 && y == 1) ||
                (x == 1 && y == 9) ||
                (x == 9 && y == 1) ||
                (x == 9 && y == 9)) {
            return false;
        }
        // Thron
        if (!isKing && x == 5 && y == 5) {
            return false;
        }
        //Innerhalb
        if (x >= 0 && x < 11 && y >= 0 && y < 11) {
            return true;
        }
        return false;
    }

//Zug König


    private boolean isEmpty(Board board, int x, int y) {
        return board.playingBoard[x][y] == board.EMPTY;
    }
}
