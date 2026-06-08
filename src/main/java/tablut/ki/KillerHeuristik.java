package tablut.ki;

import java.util.List;
import tablut.board.Move;

public class KillerHeuristik {

    public static final int MAX_PLY = 64;
    public static Move[][] killerMoves = new Move[MAX_PLY][2];

    // History-Tabelle: historyTable[from][to] = Anzahl gewichteter Cutoffs
    public static final int[][] historyTable = new int[82][82];

    public static void storeKiller(Move move, int ply) {
        if (move.equals(killerMoves[ply][0])) return;
        killerMoves[ply][1] = killerMoves[ply][0];
        killerMoves[ply][0] = move;
    }

    // Wird bei jedem Beta-Cutoff aufgerufen; tiefe Cutoffs werden stärker gewichtet
    public static void addHistory(Move move, int depth) {
        int from = (move.fromX - 1) * 9 + (move.fromY - 1);
        int to   = (move.toX   - 1) * 9 + (move.toY   - 1);
        historyTable[from][to] += depth * depth;
    }

    public static void sortMoves(List<Move> moves, int ply) {
        moves.sort((a, b) -> Integer.compare(moveScore(b, ply), moveScore(a, ply)));
    }

    public static int moveScore(Move move, int ply) {
        if (move.equals(killerMoves[ply][0])) return 100_000;
        if (move.equals(killerMoves[ply][1])) return  90_000;

        int from = (move.fromX - 1) * 9 + (move.fromY - 1);
        int to   = (move.toX   - 1) * 9 + (move.toY   - 1);
        return historyTable[from][to];
    }
}
