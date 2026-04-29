package tablut.ki;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.MoveFactory;

import java.util.List;
import java.util.Random;

public class SearchMoves {


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
