package tablut.tests;

import tablut.Tools.MainBenchmarkTest;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.ki.Bewertungsfunktion;

public class benchmarkTests_M4 {

    public static void main(String[] args) {
        System.out.println("Benchmark Tests für unsere Tablut KI Meilenstein 3\n");

        //Tests für TubCloud:
        //System.out.println("Startstellung:");
        String startstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        //benchmarktests_TubCloud(startstellung);

//        System.out.println("Weiß gewinnt in 2:");
//        String whiteWins = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
//        benchmarktests_TubCloud(whiteWins);
//
//        System.out.println("Schwarz gewinnt in 2:");
//        String blackWins = "4r4/1r2r4/2r1Kr3/3rRr3/9/2R1r2R1/9/4r4/9 w 0 1";
//        benchmarktests_TubCloud(blackWins);

        //Tests für Designentscheidungen:
        System.out.println("Tests für Designentscheidungen (Startstellung):");
        Board board = FenParser.parse(startstellung);
        board.printBoard();
        //depth4in2Minuits(board);
        depthXin1Minuits(board);
    }

    public static void benchmarktests_TubCloud(String boardFen) {
        Board board = FenParser.parse(boardFen);
        board.printBoard();

        System.out.println("\nBenchmark: Bewertungsfunktion 10000 mal aufrufen:");
        bestMovesAndTimeFor10000(board);
        System.out.println("\nBenchmark: Tiefe in 1 Sekunde:");
        depthInOneSec(board);
        System.out.println("\nBenchmark: Zeit für Tiefe 4");
        depth4in2Minuits(board);
        System.out.println("\nBenchmark: Maximale Tiefe in 2 Minuten:");
        depthXin2Minuits(board);
    }

    public static void bestMovesAndTimeFor10000(Board board) {
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            Bewertungsfunktion.ratePosition(board);
        }
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
    }

    public static void depthInOneSec(Board board) {

        Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, 10000,false, false, true, true, true, false, 0, 0);
        System.out.println("Tiefe erreicht: " + MainBenchmarkTest.depth);

        System.out.println("Bester Zug: ");
        System.out.println(move.fromX + "," + move.fromY + " --> " + move.toX + "," + move.toY);
    }

    public static void depth4in2Minuits(Board board) {
        MainBenchmarkTest.maxDepth = 4;
        long start = System.nanoTime();
        Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, 120_000,false, false, true, true, true, true, 3, 3);
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");

        System.out.println("Bester Zug: ");
        System.out.println(move.fromX + "," + move.fromY + "--> " + move.toX + "," + move.toY);
    }

    public static void depthXin2Minuits(Board board) {
        MainBenchmarkTest.maxDepth = 15;
        Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, 120_000,false, false, true, true, true, false, 0, 0);

        System.out.println("Bester Zug: ");
        System.out.println(move.fromX + "," + move.fromY + "--> " + move.toX + "," + move.toY);
    }

    public static void depthXin1Minuits(Board board) {
        MainBenchmarkTest.maxDepth = 15;
//        System.out.println("\n Alpha-Beta:");
//        Move move0 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 60_000, true, false, false, false, false,0 ,0);
//        System.out.println("Best Move:" + move0.fromX + "," + move0.fromY + "--> " + move0.toX + "," + move0.toY);
//
//        System.out.println("\n PSV:");
//        Move move1 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 60_000, false, true, false, false, false,0 ,0);
//        System.out.println("Best Move:" + move1.fromX + "," + move1.fromY + "--> " + move1.toX + "," + move1.toY);
//
//        System.out.println("\n PSV + TT");
//        Move move2 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 60_000, false, true, true, false, false,0 ,0);
//        System.out.println("Best Move:" + move2.fromX + "," + move0.fromY + "--> " + move2.toX + "," + move2.toY);
//
//        System.out.println("\n PSV + KH");
//        Move move3 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 60_000, false, true, false, true, false,0 ,0);
//        System.out.println("Best Move:" + move3.fromX + "," + move3.fromY + "--> " + move3.toX + "," + move3.toY);

//        System.out.println("\n PSV + TT + KH");
//        Move move4 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 120_000, false, true, true, true, false,0 ,0);
//        System.out.println("Best Move:" + move4.fromX + "," + move4.fromY + "--> " + move4.toX + "," + move4.toY);

        System.out.println("\n PSV + TT + KH + LMR");
        Move move5 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 120_000,false, false, true, true, true, true, 4, 4);
        System.out.println("Best Move:" + move5.fromX + "," + move5.fromY + "--> " + move5.toX + "," + move5.toY);

        System.out.println("\n PSV + TT + KH + LMR");
        Move move6 = MainBenchmarkTest.findBestMoveAlphaBeta(board, 120_000,false, false, true, true, true, true, 3, 3);
        System.out.println("Best Move:" + move6.fromX + "," + move6.fromY + "--> " + move6.toX + "," + move6.toY);
    }
}
