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

    public static int maxDepth = 4;
    public static int depth = 1;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static int knotenZaehler = 0;


    /**
     * Findet den besten Zug für die aktuelle Spielsituation auf dem Board unter Verwendung von Alpha-Beta-Suche.
     */
    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs) {
        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) return null;
        long deadline = System.currentTimeMillis() + timeLimitMs - buffer;

        bestMoveFound = moves.get(0);
        bestScoreFound = 0;

        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
            if (System.currentTimeMillis() >= deadline) break;

            boolean completed = startSearchAlg(board, moves, depth, deadline);
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

            //Alpha-Beta-Suche:
            int score = alphaBeta(copy, depth - 1, alpha, beta, deadline);

            //Minimax-Suche:
            //int score = miniMaxStanard(copy, depth - 1, alpha, beta, deadline);

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
    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline) {

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

                int childScore = alphaBeta(copy, depth - 1, alpha, beta, deadline);
                //Abbruch bei Zeit überschreitung
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                //Beta Cutoff
                if (alpha >= beta) break;
            }
            return score;
        } else {
            int score = infinity;
            for (Move move : moves) {
                Board copy = board.copy();
                GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

                int childScore = alphaBeta(copy, depth - 1, alpha, beta, deadline);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                //Alpha Cutoff
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