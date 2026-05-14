package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Bewertungsfunktion;
import tablut.game.GameLogic;
import tablut.game.Perft;
import tablut.ki.SearchMoves;

import static org.junit.jupiter.api.Assertions.*;

public class BewertungsfunktionTests {


    String whiteWins1 = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";

    @Test
    void whiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 10000000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(7, moveKI.fromX),
                () -> assertEquals(6, moveKI.fromY),
                () -> assertEquals(9, moveKI.toX),
                () -> assertEquals(6, moveKI.toY)
        );
    }
}
