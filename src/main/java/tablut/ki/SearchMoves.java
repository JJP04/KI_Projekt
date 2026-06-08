package tablut.ki;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.Bewertungsfunktion;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.game.Perft;

import java.util.List;
import java.util.Random;

public class SearchMoves {

    public static int maxDepth = 10;
    public static int depth = 1;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static int knotenZaehler = 0;
    public static long nodes = 0;

    public static TranspositionTable tt = new TranspositionTable();


    public static Move makeRandomMove(Board b) {
        //Optimieren als Array Später
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        if (moves.isEmpty()) return null;
        Random rand = new Random();
        Move randomMove = moves.get(rand.nextInt(moves.size()));

        return randomMove;
    }

    /**
     * Findet den besten Zug für die aktuelle Spielsituation auf dem Board unter Verwendung von Alpha-Beta-Suche.
     */
    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs) {
        //TODO Muss erstzet werden duch die "Sotierten" Züge
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

            boolean completed = startSearchAlg(board, moves, currentDepth, deadline);
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
    public static boolean startSearchAlg(Board board, List<Move> moves, int depth, long deadline) {
        boolean isMax = !board.playBlackTurn;

        int bestScore = isMax ? -infinity : infinity;
        Move bestMove = null;
        int alpha = -infinity;
        int beta = infinity;

        for (Move move : moves) {
            if (System.currentTimeMillis() >= deadline) return false;
            //Führt den Zug auf einer Kopie des Boards aus
            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

            //Minimax:
            //int score = miniMaxStanard(copy, depth - 1, alpha, beta, deadline);

            //Alpha Beta:
         //  int score = alphaBeta(copy, depth - 1, alpha, beta, deadline, 1);

            //PVS:
          int score = pvs(copy, depth - 1, alpha, beta, deadline, 1);

            if (score == Integer.MIN_VALUE) return false;

            //Zug + Score ausgeben:
            //System.out.printf("Zug: %d,%d --> %d,%d  Score: %d%n",
            //move.fromX, move.fromY, move.toX, move.toY, score);

            //MAX --> höchster Score, MIN --> niedrigster Score
            if (isMax) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                alpha = Math.max(alpha, bestScore);
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                beta = Math.min(beta, bestScore);
            }
        }
        if (bestMove != null) {
            bestMoveFound = bestMove;
            bestScoreFound = bestScore;
            //System.out.println("→ Bester Zug: " + bestMove.fromX + "," + bestMove.fromY
            //        + " --> " + bestMove.toX + "," + bestMove.toY
            //        + "  Score: " + bestScoreFound);
        }
        return true;
    }

    /**
     * Führt die Alpha-Beta-Suche durch und bewertet die Positionen auf der angegebenen Tiefe
     * Gibt den besten Score zurück
     */
    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline, int ply ) {
        nodes++;

        //Nur bei jedem 4. Knoten ZeitCheck => Rechnerzeitsparen
        if ((depth & 0x3) == 0 && System.currentTimeMillis() >= deadline) {
            return Integer.MIN_VALUE;
        }
        if (depth == 0 || GameLogic.isGameOver(board)) {
            return Bewertungsfunktion.ratePosition(board);
        }
        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) {
            return 0;
        }
        boolean maxScore = !board.playBlackTurn;

        //Kindknoten rekursiv bewerten
        if (maxScore) {
            int score = -infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore = alphaBeta(copy, depth - 1, alpha, beta, deadline, ply+1);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }
            return score;
        } else {
            int score = infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore = alphaBeta(copy, depth - 1, alpha, beta, deadline, ply+1);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
            return score;
        }
    }

    /**
     * Standard Minimax-Suche ohne Cutoffs
     */
    public static int miniMaxStanard(Board board, int depth, int alpha, int beta, long deadline) {

        //Nur bei jedem 4. Knoten ZeitCheck => Rechnerzeitsparen
        if ((depth & 0x3) == 0 && System.currentTimeMillis() >= deadline) {
            return Integer.MIN_VALUE;
        }

        if (depth == 0 || GameLogic.isGameOver(board)) {
            knotenZaehler++;
            return Bewertungsfunktion.ratePosition(board);
        }

        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) {
            return 0;
        }

        boolean maxScore = !board.playBlackTurn;

        if (maxScore) {
            int score = -infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore = miniMaxStanard(copy, depth - 1, alpha, beta, deadline);
                //Abbruch bei Zeit überschreitung
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
            }
            return score;
        } else {
            int score = infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore = miniMaxStanard(copy, depth - 1, alpha, beta, deadline);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
            }
            return score;
        }
    }

    public static int pvs(Board board, int depth, int alpha, int beta, long deadline, int ply) {
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
            return Bewertungsfunktion.ratePosition(board);
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

        if (maxScore) {

            int score = -infinity;

            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(copy, depth - 1, alpha, alpha + 1, deadline, ply + 1);
                    if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;
                    // Fail-high: Zug ist besser als alpha, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                    }
                }

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
            return score;

        } else {
            int score = infinity;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(copy, depth - 1, beta - 1, beta, deadline, ply + 1);
                    if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;
                    // Fail-low: Zug ist schlechter als beta, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                    }
                }

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
            return score;
        }
    }

    /**
     * Testet Anzahl der Knoten, die bei bestimmter Alpha-Beta-Suche bzw. Minimax-Suche auf einer bestimmten Tiefe besucht werden
     */
    public static void main(String[] args) {
        Board board = new Board();

        knotenZaehler = 0;
        miniMaxStanard(board, 2, -infinity, infinity, Long.MAX_VALUE);
        System.out.println("Minimax Tiefe 2: " + knotenZaehler);

        System.out.println("Perft   Tiefe 2: " + Perft.perft(board, 2));
    }
}
