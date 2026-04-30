package tablut.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.MoveFactory;

public class UnitTests {
   

 String a_1 = "9/9/9/9/9/9/3r5/1K7/9 w 0 1";
 String a_2 = "9/9/9/2r6/2R6/r8/9/9/9 s 0 1";


 @Test
    void a_1() {
    Board b = FenParser.parse(a_1);
    MoveFactory m = new MoveFactory();
    List<Move> moves = m.getAllMoves(b);
    assertEquals(16, moves.size());
    }
 @Test
    void a_2() {
    Board b = FenParser.parse(a_2);
    MoveFactory m = new MoveFactory();
    List<Move> moves = m.getAllMoves(b);
    assertEquals(25, moves.size());
    }


}
