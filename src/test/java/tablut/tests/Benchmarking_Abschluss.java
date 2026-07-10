package tablut.tests;

import tablut.Tools.MainBenchmarkTest;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;

public class Benchmarking_Abschluss {

    public static void main(String[] args) {
        System.out.println("Benchmark Tests für unsere Tablut KI Abschlusspräsentation\n");

        //Tests für TubCloud:
        //System.out.println("Startstellung:");
        //String startstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        //benchmarktests_Abschlusspräsentation(startstellung);
        //Mittelsituation: 4rr3/5r3/2R1R4/r3K2r1/1rR2R2r/r4R3/3R5/5r3/3rr4 s 0 23
        System.out.println("Weiß gewinnt in 2:");
        String whiteWins = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
        benchmarktests_Abschlusspräsentation(whiteWins);
    }

    public static void benchmarktests_Abschlusspräsentation(String boardFen) {
        Board board = FenParser.parse(boardFen);
        board.printBoard();

//        System.out.println("\nSuchtiefe Startstellung in 2 Sekunden (ohne LMR):");
//        depthXInYSecAverage(board, 2000, 20, false, 0, 0);
        //NodesTillDepth(board, 20, 4, 2000, false, 0, 0);

//        System.out.println("\nSuchtiefe Startstellung in 2 Sekunden (mit LMR (4,4)):");
//        depthXInYSecAverage(board, 2000, 20, true, 4, 4);
//        //NodesTillDepth(board, 20, 4, 2000, true, 4, 4);

        System.out.println("\nSuchtiefe Startstellung in 2 Sekunden (mit LMR (3,3)):");
        depthXInYSecAverage(board, 2000, 20, true, 3, 3);
        //NodesTillDepth(board, 20, 4, 2000, true, 3, 3);

    }

    public static void depthXInYSecAverage(Board board, long time, int runs, boolean lmr, int lmrDepth, int lmrMoves) {
        double nodes = 0;
        double depth = 0;

        for (int j = 0; j < runs; j++) {
            Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, true, lmr, lmrDepth, lmrMoves);
            nodes += MainBenchmarkTest.totalNodes;
            depth += MainBenchmarkTest.depth;
        }
        System.out.println("\nAverage Depth: " + depth / runs);
        System.out.println("\nAverage Nodes: " + nodes / runs);
    }

//1: ohne LMR
// Average Depth: Average Depth: 4
//Average Nodes: 82608

//2: mit LMR(4,4)
//Average Depth: 4
//Average Nodes: 78234

//3: mit LMR(3,3)
//Average Depth: 4
//Average Nodes: 75421


    public static void NodesTillDepth(Board board, int runs, int depth, long time, boolean lmr, int lmrDepth, int lmrMoves) {
        long nodes = 0;
        double nanoTime = 0.0;
        for (int j = 0; j < runs; j++) {
            long start = System.nanoTime();
            Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, true, true, lmr, lmrDepth, lmrMoves);
            long end = System.nanoTime();
            System.out.println("Best Move:" + move.fromX + "," + move.fromY + "--> " + move.toX + "," + move.toY);
            nanoTime = (end - start) / 1_000_000.0;
            nodes += MainBenchmarkTest.totalNodes;
        }
        System.out.println("\nAverage Time: " + nanoTime / runs + " ms");
        System.out.println("\nAverage Nodes: " + nodes / runs);
    }
}


//1: ohne LMR
//Average Time: 32.98 ms
//Average Nodes: 40441

//2: mit LMR (4,4)
//Average Time: 33.33 ms
//Average Nodes: 92215

//3: mit LMR (3,3)
//Average Time: 23.317 ms
//Average Nodes: 24616

