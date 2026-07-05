package tablut.Evolution;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.Tools.Perft;
import tablut.ki.KillerHeuristik;
import tablut.ki.TranspositionTable;
import tablut.ki.ZobristHashing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SearchMovesEvolution {

    public static int maxDepth = 10;
    public static int depth = 1;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static int knotenZaehler = 0;
    public static long nodes = 0;


    // besten 5% der Züge nehmen und, sofern deren Bewertung eng beieinander liegt,
    public static int closeMargin = 25;
    private static final Random rootRandom = new Random();

   // public static final TranspositionTable tt = new TranspositionTable();


    

    /**
     * Findet den besten Zug für die aktuelle Spielsituation auf dem Board unter Verwendung von Alpha-Beta-Suche.
     */
    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs, BewertungsfunktionEvolution eval) {
        //TODO Muss erstzet werden duch die "Sotierten" Züge
        TranspositionTable tt = new TranspositionTable();
        List<Move> moves = MoveFactory.getAllMoves(board);
        KillerHeuristik.sortMoves(moves, 0);
        nodes = 0;
        if (moves.isEmpty()) return null;
        long deadline = System.currentTimeMillis() + timeLimitMs - buffer;

        //Zobrist initialisieren (Jede Figur auf jedem Feld bekommt einen zufälligen Wert zugeordnet)
        ZobristHashing.initializeZobristTable();
        //Hash für die Startstellung berechnen
        board.hash = ZobristHashing.computeHash(board);

        bestMoveFound = moves.get(0);
        bestScoreFound = 0;

        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
            if (System.currentTimeMillis() >= deadline) break;

            boolean completed = startSearchAlg(board, moves, currentDepth, deadline, eval,tt);
            //Wenn Zeitlimit erreicht
            if (!completed) break;
            //Abbruch bei Gewinn oder Verlust
            if (bestScoreFound >= 9000 || bestScoreFound <= -9000) break;
            depth = currentDepth;
        }
        return bestMoveFound;
    }

    /**
     * Bewertet ALLE Züge auf einer bestimmten Tiefe
     */
    public static boolean startSearchAlg(Board board, List<Move> moves, int depth, long deadline, BewertungsfunktionEvolution eval, TranspositionTable tt) {
        boolean isMax = !board.playBlackTurn;

        //  alle Wurzelzüge mit vollem Fenster bewerten
        int[] scores = new int[moves.size()];
        int bestScore = isMax ? -infinity : infinity;

        for (int i = 0; i < moves.size(); i++) {
            if (System.currentTimeMillis() >= deadline) return false;
            Move move = moves.get(i);
            Move.makeMove(board, move);
            int score = pvs(board, depth - 1, -infinity, infinity, deadline, 1, eval, tt);
            Move.unmakeMove(board, move);
            if (score == Integer.MIN_VALUE) return false;   // Zeitlimit erreicht
            scores[i] = score;
            if (isMax ? score > bestScore : score < bestScore) bestScore = score;
        }

        // zug sortieren (bester zuerst)
        Integer[] order = new Integer[moves.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        if (isMax) Arrays.sort(order, (a, b) -> Integer.compare(scores[b], scores[a]));
        else       Arrays.sort(order, (a, b) -> Integer.compare(scores[a], scores[b]));

        // Besten 5% der Züge nehmen
        int topCount = Math.max(1, (int) Math.ceil(moves.size() * 0.05));
        List<Move> candidates = new ArrayList<>();
        for (int r = 0; r < topCount; r++) {
            if (Math.abs(scores[order[r]] - bestScore) <= closeMargin) {
                candidates.add(moves.get(order[r]));
            }
        }

        // Auswahl: klaren Sieg/Verlust nie ignoerien
        boolean decisive = bestScore >= 9000 || bestScore <= -9000;
        Move chosen;
        if (!decisive && candidates.size() > 1) {
            chosen = candidates.get(rootRandom.nextInt(candidates.size()));
        } else {
            // bester Zug
            chosen = moves.get(order[0]);
        }

        bestMoveFound = chosen;
        bestScoreFound = bestScore;
        return true;
    }

    /**
     * Führt die Alpha-Beta-Suche durch und bewertet die Positionen auf der angegebenen Tiefe
     * Gibt den besten Score zurück
     */
    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline, int ply, BewertungsfunktionEvolution eval) {
        nodes++;

        //Nur bei jedem 4. Knoten ZeitCheck => Rechnerzeitsparen
        if ((depth & 0x3) == 0 && System.currentTimeMillis() >= deadline) {
            return Integer.MIN_VALUE;
        }
        if (depth == 0 || GameLogic.isGameOver(board)) {
            return eval.ratePosition(board, ply);
        }
        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) {
            return 0;
        }
        boolean maxScore = !board.playBlackTurn;

        //Kindknoten rekursiv bewerten
        int score;
        if (maxScore) {
            score = -infinity;
            for (Move move : moves) {

                Move.makeMove(board, move);

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1, eval);

                Move.unmakeMove(board, move);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }

        } else {
            score = infinity;
            for (Move move : moves) {

                Move.makeMove(board, move);

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1, eval);

                Move.unmakeMove(board, move);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
        }
        return score;
    }

    /**
     * Standard Minimax-Suche ohne Cutoffs
     */
    public static int miniMaxStanard(Board board, int depth, int alpha, int beta, long deadline, BewertungsfunktionEvolution eval) {

        //Nur bei jedem 4. Knoten ZeitCheck => Rechnerzeitsparen
        if ((depth & 0x3) == 0 && System.currentTimeMillis() >= deadline) {
            return Integer.MIN_VALUE;
        }

        if (depth == 0 || GameLogic.isGameOver(board)) {
            knotenZaehler++;
            return eval.ratePosition(board);
        }

        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) {
            return 0;
        }

        boolean maxScore = !board.playBlackTurn;

        int score;
        if (maxScore) {
            score = -infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                MoveFactory.moveFigure(copy, move);

                int childScore = miniMaxStanard(copy, depth - 1, alpha, beta, deadline, eval);
                //Abbruch bei Zeit überschreitung
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
            }
        } else {
            score = infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                MoveFactory.moveFigure(copy, move);

                int childScore = miniMaxStanard(copy, depth - 1, alpha, beta, deadline, eval);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
            }
        }
        return score;
    }

    public static int pvs(Board board, int depth, int alpha, int beta, long deadline, int ply, BewertungsfunktionEvolution eval, TranspositionTable tt) {
        nodes++;

        //Überprüfung, ob Spielzustand bereits in Transposition Table gespeichert ist
        TranspositionTable.Entry entry = tt.get(board.hash);
        if (entry != null && entry.depth >= depth) {
            if (entry.type == 0) return entry.score;
            if (entry.type == -1 && entry.score <= alpha) return entry.score;
            if (entry.type == 1 && entry.score >= beta) return entry.score;
        }

        if ((depth & 0x3) == 0 && System.currentTimeMillis() >= deadline) {
            return Integer.MIN_VALUE;
        }

        if (depth == 0 || GameLogic.isGameOver(board)) {
            return eval.ratePosition(board, ply);
        }

        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) {
            return 0;
        }

        // Killer-Heuristik sortiert Züge nach Ply
        KillerHeuristik.sortMoves(moves, ply);

        // TT-Zug hat höchste Priorität → nach Sortierung an erste Stelle setzen
        if (entry != null && entry.move != null) {
            moves.remove(entry.move);
            moves.addFirst(entry.move);
        }

        int alphaOrig = alpha;
        int betaOrig = beta;

        Move bestMove = null;

        boolean maxScore = !board.playBlackTurn;

        int score;
        if (maxScore) {

            score = -infinity;

            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Move.makeMove(board, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval,tt);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(board, depth - 1, alpha, alpha + 1, deadline, ply + 1, eval,tt);

                    // Fail-high: Zug ist besser als alpha, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval,tt);
                    }
                }

                Move.unmakeMove(board, move);

                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                if (childScore > score) {
                    bestMove = move;
                }

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) {
                    KillerHeuristik.storeKiller(move, ply);
                    KillerHeuristik.addHistory(move, depth);
                    break;
                }
            }

            int type;
            if (score <= alphaOrig) {
                type = -1; // UPPERBOUND
            } else if (score >= betaOrig) {
                type = 1; // LOWERBOUND
            } else {
                type = 0; // EXACT
            }

            tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));

        } else {
            score = infinity;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Move.makeMove(board, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval,tt);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(board, depth - 1, beta - 1, beta, deadline, ply + 1, eval,tt);

                    // Fail-low: Zug ist schlechter als beta, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval,tt);
                    }
                }

                Move.unmakeMove(board, move);

                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                if (childScore < score) {
                    bestMove = move;
                }

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (alpha >= beta) {
                    KillerHeuristik.storeKiller(move, ply);
                    KillerHeuristik.addHistory(move, depth);
                    break;
                }
            }
            int type;
            if (score <= alphaOrig) {
                type = -1; // UPPERBOUND
            } else if (score >= betaOrig) {
                type = 1; // LOWERBOUND
            } else {
                type = 0; // EXACT
            }

            tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
        }
        return score;
    }

    /**
     * Testet Anzahl der Knoten, die bei bestimmter Alpha-Beta-Suche bzw. Minimax-Suche auf einer bestimmten Tiefe besucht werden
     */
    public static void main(String[] args) {
        Board board = new Board();
        BewertungsfunktionEvolution eval = new BewertungsfunktionEvolution();

        knotenZaehler = 0;
        miniMaxStanard(board, 2, -infinity, infinity, Long.MAX_VALUE, eval);
        System.out.println("Minimax Tiefe 2: " + knotenZaehler);

        System.out.println("Perft   Tiefe 2: " + Perft.perft(board, 2));
    }
}
