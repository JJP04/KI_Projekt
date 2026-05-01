package tablut.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.MoveFactory;
import tablut.game.Perft;

public class UnitTests {
   

 String a_1 = "9/9/9/9/9/9/3r5/1K7/9 w 0 1";
 String a_2 = "9/9/9/2r6/2R6/r8/9/9/9 s 0 1";

 String f_1 = "9/5r3/6rR1/5RK2/6R1r/9/1R8/9/1r7 w 23 1";
 String f_2 = "4r4/9/9/4R4/3rKr3/4r4/9/9/9 s 20 1";


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

    @Test
    void f_1() {
        Board b = FenParser.parse(f_1);
        assertEquals(47, Perft.perft(b,1));
    }

    @Test
    void f_2() {
        Board b = FenParser.parse(f_2);
        assertEquals(41, Perft.perft(b,1));
    }
}
