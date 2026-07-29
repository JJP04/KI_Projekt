package tablut.Evolution;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.Tools.Perft;
import tablut.ki.MoveOrder;
import tablut.ki.TranspositionTable;
import tablut.ki.ZobristHashing;

import java.util.*;

public class SearchMovesEvolution {

    public static int maxDepth = 4;
    public static int depth = 1;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static int knotenZaehler = 0;
    public static long nodes = 0;


    // besten 5% der Züge nehmen und, sofern deren Bewertung eng beieinander liegt
    public static int closeMargin = 25;
    private static final Random rootRandom = new Random();

    // public static final TranspositionTable tt = new TranspositionTable();


    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs, BewertungsfunktionEvolution eval) {
        //TODO Muss erstzet werden duch die "Sotierten" Züge
        TranspositionTable tt = new TranspositionTable();
        List<Move> moves = MoveFactory.getAllMoves(board);
        MoveOrder.sortMoves(board, moves, 0);
        nodes = 0;
        if (moves.isEmpty()) return null;
        long deadline = System.currentTimeMillis() + timeLimitMs - buffer;


        ZobristHashing.initializeZobristTable();

        board.hash = ZobristHashing.computeHash(board);

        bestMoveFound = moves.get(0);
        bestScoreFound = 0;

        for (int currentDepth = 1; currentDepth <= maxDepth; currentDepth++) {
            if (System.currentTimeMillis() >= deadline) break;

            boolean completed = startSearchAlg(board, moves, currentDepth, deadline, eval, tt);

            if (!completed) break;

            if (bestScoreFound >= 9000 || bestScoreFound <= -9000) break;
            depth = currentDepth;
        }
        return bestMoveFound;
    }


    public static boolean startSearchAlg(Board board, List<Move> moves, int depth, long deadline, BewertungsfunktionEvolution eval, TranspositionTable tt) {
        boolean isMax = !board.playBlackTurn;


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


        Integer[] order = new Integer[moves.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        if (isMax) Arrays.sort(order, (a, b) -> Integer.compare(scores[b], scores[a]));
        else Arrays.sort(order, Comparator.comparingInt(a -> scores[a]));

        // Besten 5% der Züge nehmen
        int topCount = Math.max(1, (int) Math.ceil(moves.size() * 0.05));
        List<Move> candidates = new ArrayList<>();
        for (int r = 0; r < topCount; r++) {
            if (Math.abs(scores[order[r]] - bestScore) <= closeMargin) {
                candidates.add(moves.get(order[r]));
            }
        }


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


    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline, int ply, BewertungsfunktionEvolution eval) {
        nodes++;


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


    public static int miniMaxStanard(Board board, int depth, int alpha, int beta, long deadline, BewertungsfunktionEvolution eval) {


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


        MoveOrder.sortMoves(board, moves, ply);


        if (entry != null && entry.move != null) {
            int ttIndex = moves.indexOf(entry.move);
            if (ttIndex > 0) {
                moves.addFirst(moves.remove(ttIndex));
            }
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

                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval, tt);
                } else {

                    childScore = pvs(board, depth - 1, alpha, alpha + 1, deadline, ply + 1, eval, tt);


                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval, tt);
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
                    MoveOrder.storeKiller(move, ply);
                    MoveOrder.addHistory(move, depth);
                    break;
                }
            }

            int type;
            if (score <= alphaOrig) {
                type = -1;
            } else if (score >= betaOrig) {
                type = 1;
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

                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval, tt);
                } else {

                    childScore = pvs(board, depth - 1, beta - 1, beta, deadline, ply + 1, eval, tt);


                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1, eval, tt);
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
                    MoveOrder.storeKiller(move, ply);
                    MoveOrder.addHistory(move, depth);
                    break;
                }
            }
            int type;
            if (score <= alphaOrig) {
                type = -1;
            } else if (score >= betaOrig) {
                type = 1;
            } else {
                type = 0;
            }

            tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
        }
        return score;
    }

}
