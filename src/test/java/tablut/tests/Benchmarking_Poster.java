package tablut.tests;

import tablut.Tools.MainBenchmarkTest;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.ki.Bewertungsfunktion;

public class Benchmarking_Poster {
    public static void main(String[] args) {
        System.out.println("Benchmark Tests für unsere Tablut KI Meilenstein 3\n");

        String mittelstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        Board board = FenParser.parse(mittelstellung);
        board.printBoard();
        depthXinTime(board, 60_000);
    }

    public static void depthXinTime(Board board, int time) {
        MainBenchmarkTest.maxDepth = 5;
        long start;
        long end;
        System.out.println("\n Alpha-Beta:");
        start = System.nanoTime();
        Move move0 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, true, false, false, false, false, 0, 0);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        System.out.println("Best Move:" + move0.fromX + "," + move0.fromY + "--> " + move0.toX + "," + move0.toY);

        System.out.println("\n PSV:");
        start = System.nanoTime();
        Move move1 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, false, false, false, 0, 0);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move1.fromX + "," + move1.fromY + "--> " + move1.toX + "," + move1.toY);

        System.out.println("\n PSV + TT");
        start = System.nanoTime();
        Move move2 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, false, false, 0, 0);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move2.fromX + "," + move0.fromY + "--> " + move2.toX + "," + move2.toY);

        System.out.println("\n PSV + KH");
        start = System.nanoTime();
        Move move3 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, false, true, false, 0, 0);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move3.fromX + "," + move3.fromY + "--> " + move3.toX + "," + move3.toY);

        System.out.println("\n PSV + TT + KH");
        start = System.nanoTime();
        Move move4 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, true, false, 0, 0);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move4.fromX + "," + move4.fromY + "--> " + move4.toX + "," + move4.toY);

        System.out.println("\n PSV + TT + KH + LMR");
        start = System.nanoTime();
        Move move5 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, true, true, 4, 4);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move5.fromX + "," + move5.fromY + "--> " + move5.toX + "," + move5.toY);

        System.out.println("\n PSV + TT + KH + LMR(3,3)");
        start = System.nanoTime();
        Move move6 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, false, true, true, 3, 3);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");

        System.out.println("\n PSV + TT + LMR(3,3)");
        start = System.nanoTime();
        MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, false, true, 3, 3);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move6.fromX + "," + move6.fromY + "--> " + move6.toX + "," + move6.toY);
    }
}
