package tablut.ki;

import tablut.board.Move;

import java.util.HashMap;

/**
 * Speichert Informationen zu einem Hash
 */
public class TranspositionTable {

    public static class Entry {
        public final int depth; // Tiefe der Suche die zu diesem Eintrag geführt hat
        public final int score; // Bewertung des Spielzustands
        public final int type; // 0 = exakt -1 = Alpha-Cutoff 1 = Betha-Cutoff
        public final Move move; // Der beste Zug der zu diesem Eintrag geführt hat

        public Entry(int depth, int score, int type, Move move) {
            this.depth = depth;
            this.score = score;
            this.type = type;
            this.move = move;
        }
    }

    public final HashMap<Long, Entry> table = new HashMap<>();

    public int size() {
        return table.size();
    }

    public void clear() {
        table.clear();
    }

    public Entry get(long hash) {
        return table.get(hash);
    }

    public void put(long hash, Entry entry) {
        Entry existing = table.get(hash);
        if (existing == null || entry.depth >= existing.depth) {
            table.put(hash, entry);
        }
    }
}
