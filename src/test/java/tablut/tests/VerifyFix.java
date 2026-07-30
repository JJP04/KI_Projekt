package tablut.tests;

import tablut.Tools.MainBenchmarkTest;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.ki.ZobristHashing;

import java.util.Arrays;


public class VerifyFix {

    public static void main(String[] args) {
        String fen = "3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";

        // kurzes Zeitlimit, Timeout tritt sicher mitten in der Suche ein
        System.out.println("=== Lauf A: 5s Limit (Timeout-Pfad) ===");
        runAndCheck(fen, 5_000);

        // 2 Minuten, maxDepth 15 das ursprüngliche Crash-Szenario
        System.out.println("\n=== Lauf B: 120s Limit (Crash-Szenario Tiefe 7/8) ===");
        runAndCheck(fen, 120_000);


    }

    static void runAndCheck(String fen, long timeMs) {
        Board board = FenParser.parse(fen);
        Board reference = FenParser.parse(fen);
        MainBenchmarkTest.maxDepth = 15;

        Move move = MainBenchmarkTest.findBestMoveAlphaBeta(board, timeMs,false, false, true, true, true, false,0,0);
        System.out.println("Tiefe erreicht: " + MainBenchmarkTest.depth);
        System.out.println("Bester Zug: " + move.fromX + "," + move.fromY + " --> " + move.toX + "," + move.toY);

        if (!Arrays.deepEquals(board.playingBoard, reference.playingBoard)) {
            board.printBoard();
            throw new AssertionError("fehler: Brett nach der Suche verändert!");
        }
        if (board.playBlackTurn != reference.playBlackTurn) {
            throw new AssertionError("fehler: Zugrecht nach der Suche verändert!");
        }
        if (!Arrays.equals(board.kingPos, reference.kingPos)) {
            throw new AssertionError("fehler: kingPos nach der Suche verändert!");
        }
        long recomputed = ZobristHashing.computeHash(board);
        if (board.hash != recomputed) {
            throw new AssertionError("fehler: Inkrementeller Hash weicht von computeHash ab!");
        }
        System.out.println("Board, Hash, Zugrecht und kingPos-Check: OK");
    }
}
