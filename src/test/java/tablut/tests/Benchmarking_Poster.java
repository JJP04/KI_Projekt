package tablut.tests;

import tablut.Tools.MainBenchmarkTest;
import tablut.Tools.MainBenchmarkTestCopy;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.ki.Bewertungsfunktion;

public class Benchmarking_Poster {
    public static void main(String[] args) {
        System.out.println("Benchmark Tests für unsere Tablut KI Meilenstein 3\n");

        String startstellung = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";
        Board board = FenParser.parse(startstellung);
        board.printBoard();
        depth5inTimeSA(board, 3,2000000000);
        //copyVsMakeUnmakeTime(board, 5, 20000);
    }

    public static void depth5inTime(Board board, int depth, int time) {
        System.out.println("\n Alpha-Beta:");
        Move move0 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time,true, true, false, false, false, false, 0, 0);
        System.out.println("Best Move:" + move0.fromX + "," + move0.fromY + "--> " + move0.toX + "," + move0.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);

        System.out.println("\n PSV:");
        Move move1 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time,false, false, true, false, false, false, 0, 0);
        //System.out.println("Best Move:" + move1.fromX + "," + move1.fromY + "--> " + move1.toX + "," + move1.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);

        System.out.println("\nPSV + TT + KH");
        Move move4 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time,false, false, true, true, true, false, 0, 0);
        //System.out.println("Best Move:" + move4.fromX + "," + move4.fromY + "--> " + move4.toX + "," + move4.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("\n Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);

        System.out.println("\n PSV + TT + KH + LMR(4,4)");
        Move move5 = MainBenchmarkTest.findBestMoveAlphaBeta(board, time,false, false, true, true, true, true, 4, 4);
        //System.out.println("Best Move:" + move5.fromX + "," + move5.fromY + "--> " + move5.toX + "," + move5.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);

        System.out.println("\n PSV + TT + KH + LMR(3,3)");
        MainBenchmarkTest.findBestMoveAlphaBeta(board, time,false, false, true, true, true, true, 3, 3);
        //System.out.println("Best Move:" + move6.fromX + "," + move6.fromY + "--> " + move6.toX + "," + move6.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);
    }

    public static void depth5inTimeSA(Board board, int depth, int time) {

        int runs = 10;
        MainBenchmarkTest.maxDepth = depth;

        long totalNodesMinimax = 0;
        long totalNodesAlphaBeta = 0;
        long totalNodesPVS = 0;

        long totalTimeMinimax = 0;
        long totalTimeAlphaBeta = 0;
        long totalTimePVS = 0;

        for (int i = 0; i < runs; i++) {
            System.out.println("Durchlauf " + (i + 1) + "/" + runs);

            System.out.println("\nMinimax:");
            long start = System.nanoTime();
            MainBenchmarkTest.findBestMoveAlphaBeta(board, time, true, false, false, false, false, false, 0, 0);
            long end = System.nanoTime();
            totalTimeMinimax += end - start;
            totalNodesMinimax += MainBenchmarkTest.nodesAtCompletedDepth;

            System.out.println("\nAlpha-Beta:");
            start = System.nanoTime();
            MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, true, false, false, false, false, 0, 0);
            end = System.nanoTime();
            totalTimePVS += end - start;
            totalTimeAlphaBeta += end - start;
            totalNodesAlphaBeta += MainBenchmarkTest.nodesAtCompletedDepth;

            System.out.println("\nPVS:");
            start = System.nanoTime();
            MainBenchmarkTest.findBestMoveAlphaBeta(board, time, false, false, true, false, false, false, 0, 0);
            end = System.nanoTime();
            totalTimePVS += end - start;
            totalNodesPVS += MainBenchmarkTest.nodesAtCompletedDepth;
        }

        System.out.println("\nMinimax:");
        System.out.println("Average Time: " + (totalTimeMinimax / runs) / 1_000_000.0 + " ms");
        System.out.println("Average Nodes: " + (totalNodesMinimax / (double) runs));


        System.out.println("\nAlpha-Beta:");
        System.out.println("Average Time: " + (totalTimeAlphaBeta / runs) / 1_000_000.0 + " ms");
        System.out.println("Average Nodes: " + (totalNodesAlphaBeta / (double) runs));


        System.out.println("\nPVS:");
        System.out.println("Average Time: " + (totalTimePVS / runs) / 1_000_000.0 + " ms");
        System.out.println("Average Nodes: " + (totalNodesPVS / (double) runs));
    }

    public static void copyVsMakeUnmakeTime(Board board, int depth, int time) {
        MainBenchmarkTestCopy.maxDepth = depth;
        MainBenchmarkTest.maxDepth = depth;
        System.out.println("\n Copy");
        long start = System.nanoTime();
        MainBenchmarkTestCopy.findBestMoveAlphaBeta(board, time, false, true, true, true, true, 3, 3);
        long end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move0.fromX + "," + move0.fromY + "--> " + move0.toX + "," + move0.toY);
        System.out.println("Depth: " + MainBenchmarkTestCopy.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTestCopy.nodesAtCompletedDepth);

        System.out.println("\n MakeUnmake");
        start = System.nanoTime();
        MainBenchmarkTest.findBestMoveAlphaBeta(board, time,false, false, true, true, true, true, 3, 3);
        end = System.nanoTime();
        System.out.println("Zeit: " + (end - start) / 1_000_000.0 + " ms");
        //System.out.println("Best Move:" + move0.fromX + "," + move0.fromY + "--> " + move0.toX + "," + move0.toY);
        System.out.println("Depth: " + MainBenchmarkTest.completedDepth);
        System.out.println("Nodes: " + MainBenchmarkTest.nodesAtCompletedDepth);
    }
}
