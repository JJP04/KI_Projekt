package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.List;

public class Perft {


   static MoveFactory moveFactory = new MoveFactory();

    public static long perft(Board b, int depth) {


        if (depth == 0) return 1;
        if (GameLogic.isGameOver(b)) {
            return 0;
        }

        List<Move> moves = moveFactory.getAllMoves(b);
        long nodes = 0;

        for (Move move : moves) {
            Board copy = b.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);
            nodes += perft(copy, depth - 1);
        }
        return nodes;
    }


}
