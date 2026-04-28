package tablut;

import java.util.List;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.MoveFactory;

public class Main {
    public static void main(String[] args) {
        Board b = new Board();
        // b.printBoard();

        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        System.out.println(moves.size());

        for (Move move : moves) {
            System.out.println(
                    "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")"
            );
        }
    }
}