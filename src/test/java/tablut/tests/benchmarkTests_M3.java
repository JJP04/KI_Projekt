package tablut.tests;

import tablut.MainBenchmarkTest;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Perft;

//Alpha-Beta vs. PVS
//PVS mit copy vs. PVS mit make/unmake
//PVS - PVS + TT
//PVS - PVS + KH
//PVS - PVS + TT + KH

public class benchmarkTests_M3 {

    public static void main(String[] args) {
        System.out.println("Benchmark Tests für unsere Tablut KI... \n");
        //System.out.println("Startstellung:");
        //String startstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        //benchmarktests(startstellung);

        System.out.println("Weiß gewinnt in 2:");
        String whiteWins = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
        benchmarktests(whiteWins);

//        System.out.println("Schwarz gewinnt in 2:");
//        String blackWins = "4r4/1r2r4/2r1Kr3/3rRr3/9/2R1r2R1/9/4r4/9 w 0 1";
//        benchmarktests(blackWins);
    }

    public static void benchmarktests(String boardFen) {
        Board board = FenParser.parse(boardFen);
        board.printBoard();
        System.out.println("\nBenchmark: Tiefe in 1 Sekunde:");
        depthInOneSec(board);
//        System.out.println("\nBenchmark: Zeit für Tiefe 4");
//        depth4in2Minuits(board);
//        System.out.println("\nBenchmark: Maximale Tiefe in 2 Minuten:");
//        depthXin2Minuits(board);
    }

    public static void depthInOneSec(Board board) {

        Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, 1000, false, true, false, true, true);
        System.out.println("Tiefe erreicht: " + MainBenchmarkTest.depth);

        System.out.println("Bester Zug: ");
        System.out.println(move.fromX + "," + move.fromY + " --> " + move.toX + "," + move.toY);
    }

    public static void depth4in2Minuits(Board board) {

        MainBenchmarkTest.maxDepth = 4;
        long start = System.nanoTime();
        //Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board);
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");

        System.out.println("Zustände ohne Cutoffs: ");
        Perft.deadline = System.currentTimeMillis() + 120_000;
        System.out.println(Perft.perft(board, 4));

        System.out.println("Zustände mit Cutoffs: ");


        System.out.println("Bester Zug: ");
        //System.out.println(move.fromX + "," + move.fromY + "--> " + move.toX + "," + move.toY);
    }

    public static void depthXin2Minuits(Board board) {

        MainBenchmarkTest.maxDepth = 10;
        //Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board);
        System.out.println(MainBenchmarkTest.depth);

        System.out.println("Zustände ohne Cutoffs: ");
        Perft.deadline = System.currentTimeMillis() + 120_000;
        System.out.println(Perft.perft(board, MainBenchmarkTest.depth));

        System.out.println("Zustände mit Cutoffs: ");


        System.out.println("Bester Zug: ");
        //System.out.println(move.fromX + "," + move.fromY + "--> " + move.toX + "," + move.toY);
    }
}
