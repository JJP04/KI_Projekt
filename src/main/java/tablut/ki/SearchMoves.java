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

    private static final int maxDepth = 10000000;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 300;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static int knotenZaehler = 0;

    public static Move makeRandomMove(Board b) {
        //Optimieren als Array Später
        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b);
        if (moves.isEmpty()) return null;
        Random rand = new Random();
        Move randomMove = moves.get(rand.nextInt(moves.size()));

        return randomMove;
    }

    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs) {
        List<Move> moves = MoveFactory.getAllMoves(board);
        if (moves.isEmpty()) return null;

        long deadline = System.currentTimeMillis() + timeLimitMs - buffer;

        bestMoveFound = moves.get(0);
        bestScoreFound = 0;

        for (int depth = 1; depth <= maxDepth; depth++) {

            if (System.currentTimeMillis() >= deadline) break;

            boolean completed = startSearchAlg(board, moves, depth, deadline);
            //Wenn tiefe nicht beendet werden konnte
            if (!completed) break;

            if (bestScoreFound >= 9000 || bestScoreFound <= -9000) break;
        }
        return bestMoveFound;
    }


    //Berechnung der Tiefe eines Zuges/Bester Zug auf der Jeweiligen Tiefe
    public static boolean startSearchAlg(Board board, List<Move> moves, int depth, long deadline) {
        boolean isMax = !board.playBlackTurn;

        int bestScore = isMax ? -infinity : infinity;
        Move bestMove = null;
        int alpha = -infinity;
        int beta = infinity;

        for (Move move : moves) {
            if (System.currentTimeMillis() >= deadline) return false;

            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);

            //Alpha Betta
            int score = alphaBeta(copy, depth - 1, alpha, beta, deadline);

            //Minimax
//          int score = miniMaxStanard(copy, depth - 1, alpha, beta, deadline);

            if (score == Integer.MIN_VALUE) return false;

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
        }
        return true;
    }

    //Standard Alpha-Beta
    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline) {

        //Nur bei Jedem 4K noten ZeitCheck - Rechnezitsparen
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

    //Standard MiniMax - Keine Cutoff
    public static int miniMaxStanard(Board board, int depth, int alpha, int beta, long deadline) {

        //Nur bei Jedem 4K noten ZeitCheck - Rechnezitsparen
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

    public static void main(String[] args) {
        Board board = new Board();

        knotenZaehler = 0;
        miniMaxStanard(board, 2, -infinity, infinity, Long.MAX_VALUE);
        System.out.println("Minimax Tiefe 2: " + knotenZaehler);

        System.out.println("Perft   Tiefe 2: " + Perft.perft(board, 2));
    }
}





