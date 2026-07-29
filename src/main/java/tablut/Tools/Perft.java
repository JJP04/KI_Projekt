package tablut.Tools;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;

import java.util.List;

public class Perft {

    public static long deadline = Long.MAX_VALUE;

    public static long perft(Board b, int depth) {
        if (System.currentTimeMillis() >= deadline) return 0;

        if (depth == 0) return 1;
        if (GameLogic.isGameOver(b)) {
            return 0;
        }

        List<Move> moves = MoveFactory.getAllMoves(b);
        long nodes = 0;

        for (Move move : moves) {
            if (System.currentTimeMillis() >= deadline) return nodes;
            Board copy = b.copy();
            MoveFactory.moveFigure(copy, move);
            nodes += perft(copy, depth - 1);
        }
        return nodes;
    }
}
