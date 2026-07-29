package tablut.ki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;

public class MoveOrder {

    public static final int MAX_PLY = 64;
    public static final Move[][] killerMoves = new Move[MAX_PLY][2];

    // History-Tabelle
    public static final int[][] historyTable = new int[82][82];

    public static void storeKiller(Move move, int ply) {
        if (move.equals(killerMoves[ply][0])) return;
        killerMoves[ply][1] = killerMoves[ply][0];
        killerMoves[ply][0] = move;
    }

    // Wird bei jedem Beta-Cutoff aufgerufen,tiefe Cutoffs werden stärker gewichtet
    public static void addHistory(Move move, int depth) {
        int from = (move.fromX - 1) * 9 + (move.fromY - 1);
        int to = (move.toX - 1) * 9 + (move.toY - 1);
        historyTable[from][to] += depth * depth;
    }

    public static void sortMoves(Board board, List<Move> moves, int ply) {
        int n = moves.size();
        if (n < 2) return;

        // Score genau einmal pro Zug berechnen
        int[] scores = new int[n];
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) {
            scores[i] = moveScore(board, moves.get(i), ply);
            idx[i] = i;
        }


        Arrays.sort(idx, (a, b) -> Integer.compare(scores[b], scores[a]));

        // züge in die neue Reihenfolge bringen
        List<Move> sorted = new ArrayList<>(n);
        for (int i : idx) sorted.add(moves.get(i));
        moves.clear();
        moves.addAll(sorted);
    }

    public static int moveScore(Board board, Move move, int ply) {

        int score = 0;

        //Schlagzüge
        int captures = GameLogic.countCaptures(board, move);
        score += captures * 10_000;

        //Killer Moves
        if (move.equals(killerMoves[ply][0])) score += 100_000;
        if (move.equals(killerMoves[ply][1])) score += 90_000;

        //History
        int from = (move.fromX - 1) * 9 + (move.fromY - 1);
        int to = (move.toX - 1) * 9 + (move.toY - 1);
        score += historyTable[from][to];

        return score;
    }
}
