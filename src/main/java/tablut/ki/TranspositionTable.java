package tablut.ki;

import tablut.board.Move;

import java.util.HashMap;

/**
 * Speichert Informationen zu einem Hash
 */
public class TranspositionTable {

    public static class Entry {
        int depth; // Tiefe der Suche, die zu diesem Eintrag geführt hat
        int score; // Bewertung des Spielzustands
        int type; // 0 = exakt, -1 = Alpha-Cutoff, 1 = Betha-Cutoff
        Move move; // Der beste Zug, der zu diesem Eintrag geführt hat

        public Entry(int depth, int score, int type, Move move) {
            this.depth = depth;
            this.score = score;
            this.type = type;
            this.move = move;
        }
    }

    private final HashMap<Long, Entry> table = new HashMap<>();


    public boolean contains(long hash) {
        return table.containsKey(hash);
    }

    public Entry get(long hash) {
        return table.get(hash);
    }

    public void put(long hash, Entry entry){
        table.put(hash, entry);
    }
}
