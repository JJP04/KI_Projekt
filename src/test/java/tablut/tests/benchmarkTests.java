package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Bewertungsfunktion;
import tablut.game.Perft;
import tablut.ki.SearchMoves;

public class benchmarkTests {

    String start = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
    String mitte = "3rrr3/9/4R4/r3R3r/rrR1KRR1r/r3R3r/3R5/4r4/3rrr3 s 4 7";
    String ende = "7r1/R8/9/9/9/K8/9/1r7/9 s 0 39";

    @Test
    void benchmarkTestStart() {
        Board board = FenParser.parse(start);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Perft.perft(board, 1);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }

    @Test
    void benchmarkTestMitte() {
        Board board = FenParser.parse(mitte);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Perft.perft(board, 1);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }

    @Test
    void benchmarkTestEnde() {
        Board board = FenParser.parse(ende);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Perft.perft(board, 1);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }


    String whiteWins = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
    String blackWins = "";

    @Test
    void benchmarkTestWhiteWins() {
        Board board = FenParser.parse(whiteWins);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Bewertungsfunktion.ratePosition(board);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }

    @Test
    void benchmarkTestBlackWins() {
        Board board = FenParser.parse(whiteWins);
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Bewertungsfunktion.ratePosition(board);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }

    @Test
    void maxDepthInOneSec() {
        Board board = FenParser.parse(whiteWins);
        Move move = SearchMoves.findBestMoveAlphaBeta(board, 1000);
    }
}




