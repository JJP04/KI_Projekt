package tablut.Tools;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.ki.Bewertungsfunktion;
import tablut.ki.MoveOrder;
import tablut.ki.TranspositionTable;
import tablut.ki.ZobristHashing;

import java.util.List;

public class MainBenchmarkTestCopy {

    public static int maxDepth = 10;
    public static int depth = 0;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static long totalNodes;
    public static long completedDepth;
    public static long nodesAtCompletedDepth;
    public static long depthNodes;
    public static long timeLimitMs;

    public static boolean alphaBeta = false;
    public static boolean pvs = false;
    public static boolean transpositionTable = false;
    public static boolean killerHeuristik = false;
    public static boolean lateMoveReductions = false;
    public static int lmrDepth;
    public static int lmrMoves;


    public static final TranspositionTable tt = new TranspositionTable();

    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs, boolean alphaBeta, boolean pvs, boolean transpositionTable, boolean killerHeuristik, boolean LateMoveReductions, int lmrDepth, int lmrMoves) {

        MainBenchmarkTestCopy.timeLimitMs = timeLimitMs;
        MainBenchmarkTestCopy.alphaBeta = alphaBeta;
        MainBenchmarkTestCopy.pvs = pvs;
        MainBenchmarkTestCopy.transpositionTable = transpositionTable;
        MainBenchmarkTestCopy.killerHeuristik = killerHeuristik;
        MainBenchmarkTestCopy.lateMoveReductions = LateMoveReductions;
        MainBenchmarkTestCopy.lmrDepth = lmrDepth;
        MainBenchmarkTestCopy.lmrMoves = lmrMoves;

        List<Move> moves = MoveFactory.getAllMoves(board);
        MoveOrder.sortMoves(board, moves, 0);
        totalNodes = 0;
        //TT leeren, damit jeder Benchmark unabhängig misst
        tt.clear();
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

            depthNodes = 0;

            boolean completed = startSearchAlg(board, moves, currentDepth, deadline);

            //Wenn Zeitlimit erreicht;
            if (!completed) {
                break;
            }
            // Nur vollständig abgeschlossene Tiefe speichern
            completedDepth = currentDepth;

            totalNodes += depthNodes;
            nodesAtCompletedDepth = totalNodes;


            depth = currentDepth;
            //Abbruch bei Gewinn oder Verlust
            if (bestScoreFound >= 9000 || bestScoreFound <= -9000) break;

            depth = currentDepth;
        }
        System.out.println("Total Nodes (mit Cutoffs): " + totalNodes);

        return bestMoveFound;
    }

    /**
     * Bewertet alle Züge auf einer bestimmten Tiefe
     */
    public static boolean startSearchAlg(Board board, List<Move> moves, int depth, long deadline) {
        depthNodes++;

        boolean isMax = !board.playBlackTurn;
        int score = 0;

        int bestScore = isMax ? -infinity : infinity;
        Move bestMove = null;
        int alpha = -infinity;
        int beta = infinity;

        for (Move move : moves) {
            if (System.currentTimeMillis() >= deadline) return false;

            Board copy = board.copy();
            MoveFactory.moveFigure(copy, move);

            if (alphaBeta) {
                score = alphaBeta(copy, depth - 1, alpha, beta, deadline, 1);
            }
            //PVS:
            if (pvs) {
                score = pvs(copy, depth - 1, alpha, beta, deadline, 1);
            }

            if (score == Integer.MIN_VALUE) return false;

            //Zug + Score ausgeben:
            //System.out.printf("Zug: %d,%d --> %d,%d  Score: %d%n",
            //move.fromX, move.fromY, move.toX, move.toY, score);

            //max -> höchster Score, min -> niedrigster Score
            if (isMax) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                alpha = bestScore;
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                beta = bestScore;
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
     * Führt die Alpha-Beta durch
     */
    public static int alphaBeta(Board board, int depth, int alpha, int beta, long deadline, int ply) {
        depthNodes++;

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
        int score;
        if (maxScore) {
            score = -infinity;
            for (Move move : moves) {

                Move.makeMove(board, move);

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1);

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

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1);

                Move.unmakeMove(board, move);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
        }
        return score;
    }

    public static int pvs(Board board, int depth, int alpha, int beta, long deadline, int ply) {
        depthNodes++;

        //Überprüfung, ob Spielzustand bereits in Transposition Table gespeichert ist

        TranspositionTable.Entry entry = new TranspositionTable.Entry(depth, 0, 0, null);

        if (transpositionTable) {
            entry = tt.get(board.hash);
            if (entry != null && entry.depth >= depth) {
                if (entry.type == 0) return entry.score;
                if (entry.type == -1 && entry.score <= alpha) return entry.score;
                if (entry.type == 1 && entry.score >= beta) return entry.score;
            }
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

        if (killerHeuristik) {
            // Killer-Heuristik sortiert Züge nach Ply
            MoveOrder.sortMoves(board, moves, ply);
        }

        if (transpositionTable) {

            if (entry != null && entry.move != null) {
                int ttIndex = moves.indexOf(entry.move);
                if (ttIndex > 0) {
                    moves.addFirst(moves.remove(ttIndex));
                }
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

                int searchDepth = depth - 1;

                boolean reduce = false;

                if (lateMoveReductions && depth >= lmrDepth && i > lmrMoves) {
                    reduce = true; // Reduziere Tiefe für tiefe Knoten und viele Züge
                }
                int reducedDepth = reduce ? searchDepth - 1 : searchDepth;

                Board copy = board.copy();
                MoveFactory.moveFigure(copy, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster (ohne LMR) für ersten i Züge, danach mit LMR
                    childScore = pvs(copy, reducedDepth, alpha, alpha + 1, deadline, ply + 1);

                    //LMR-Re-Search (Falls Zug besser als alpha, dann volle Tiefe suchen)
                    if (reduce && childScore > alpha) {
                        childScore = pvs(copy, depth - 1, alpha, alpha + 1, deadline, ply + 1);
                    }

                    //PVS-Re-Search (Falls Zug besser als alpha, dann volle Tiefe suchen)
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
                    if (killerHeuristik) {
                        MoveOrder.storeKiller(move, ply);
                        MoveOrder.addHistory(move, depth);
                    }
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
            if (transpositionTable) {
                tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
            }
        } else {
            score = infinity;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);

                int searchDepth = depth - 1;

                boolean reduce = false;

                if (lateMoveReductions && depth >= lmrDepth && i > lmrMoves) {
                    reduce = true; // Reduziere Tiefe für tiefe Knoten und viele Züge
                }
                int reducedDepth = reduce ? searchDepth - 1 : searchDepth;

                Board copy = board.copy();
                MoveFactory.moveFigure(copy, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(copy, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster (ohne LMR) für ersten i Züge, danach mit LMR
                    childScore = pvs(copy, reducedDepth, beta - 1, beta, deadline, ply + 1);

                    //LMR-Re-Search (Falls Zug besser als alpha, dann volle Tiefe suchen)
                    if (reduce && childScore < beta) {
                        childScore = pvs(copy, depth - 1, beta - 1, beta, deadline, ply + 1);
                    }
                    //PVS-Re-Search (Falls Zug besser als alpha, dann volle Tiefe suchen)
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
                    if (killerHeuristik) {
                        MoveOrder.storeKiller(move, ply);
                        MoveOrder.addHistory(move, depth);
                    }
                    break;
                }
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
        if (transpositionTable) {
            tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
        }
        return score;
    }
}
