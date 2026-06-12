package tablut;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.Bewertungsfunktion;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.ki.KillerHeuristik;
import tablut.ki.SearchMoves;
import tablut.ki.TranspositionTable;
import tablut.ki.ZobristHashing;

import java.util.List;

public class MainBenchmarkTest {

    public static int maxDepth = 10;
    public static int depth = 1;
    private static final int infinity = Integer.MAX_VALUE / 2;
    private static final long buffer = 50;
    private static Move bestMoveFound = null;
    private static int bestScoreFound = 0;
    public static long totalNodes;
    public static long depthNodes;
    public static long timeLimitMs;

    public static boolean alphaBeta = false;
    public static boolean pvs = false;
    public static boolean makeUnmake = false;
    public static boolean transpositionTable = false;
    public static boolean killerHeuristik = false;

    public static TranspositionTable tt = new TranspositionTable();
    
    public static Move findBestMoveAlphaBeta(Board board, long timeLimitMs, boolean alphaBeta, boolean pvs, boolean makeUnmake, boolean transpositionTable, boolean killerHeuristik) {

        MainBenchmarkTest.timeLimitMs = timeLimitMs;
        MainBenchmarkTest.alphaBeta = alphaBeta;
        MainBenchmarkTest.pvs = pvs;
        MainBenchmarkTest.makeUnmake = makeUnmake;
        MainBenchmarkTest.transpositionTable = transpositionTable;
        MainBenchmarkTest.killerHeuristik = killerHeuristik;

        List<Move> moves = MoveFactory.getAllMoves(board);
        KillerHeuristik.sortMoves(moves, 0);
        totalNodes = 0;
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
            //Wenn Zeitlimit erreicht
            if (!completed) break;
            //Abbruch bei Gewinn oder Verlust
            if (bestScoreFound >= 9000 || bestScoreFound <= -9000) break;

            System.out.println("Nodes at depth " + currentDepth + ": " + depthNodes);
            totalNodes += depthNodes;

            depth = currentDepth;
        }
        System.out.println("Total Nodes: " + totalNodes);

        return bestMoveFound;
    }

    /**
     * Bewertet ALLE Züge auf einer bestimmten Tiefe
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
            //MakeMove
            Move.makeMove(board, move);

            //Alpha Beta:
            if (alphaBeta) {
                score = alphaBeta(board, depth - 1, alpha, beta, deadline, 1);
            }
            //PVS:
            if (pvs) {
                score = pvs(board, depth - 1, alpha, beta, deadline, 1);
            }

            Move.unmakeMove(board, move);

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
        if (maxScore) {
            int score = -infinity;
            for (Move move : moves) {

                Move.makeMove(board, move);

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1);

                Move.unmakeMove(board, move);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                if (alpha >= beta) break;
            }

            return score;
        } else {
            int score = infinity;
            for (Move move : moves) {

                Move.makeMove(board, move);

                int childScore = alphaBeta(board, depth - 1, alpha, beta, deadline, ply + 1);

                Move.unmakeMove(board, move);
                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (alpha >= beta) break;
            }
            return score;
        }
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
            KillerHeuristik.sortMoves(moves, ply);
        }

        if (transpositionTable) {
            // TT-Zug hat höchste Priorität → nach Sortierung an erste Stelle setzen
            if (entry != null && entry.move != null) {
                moves.remove(entry.move);
                moves.addFirst(entry.move);
            }
        }

        int alphaOrig = alpha;
        int betaOrig = beta;

        Move bestMove = null;

        boolean maxScore = !board.playBlackTurn;

        if (maxScore) {

            int score = -infinity;

            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Move.makeMove(board, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(board, depth - 1, alpha, alpha + 1, deadline, ply + 1);
                    if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;
                    // Fail-high: Zug ist besser als alpha, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1);
                    }
                }

                Move.unmakeMove(board, move);

                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                if (childScore > score) {
                    bestMove = move;
                }

                score = Math.max(score, childScore);
                alpha = Math.max(alpha, score);
                if (killerHeuristik) {
                    if (alpha >= beta) {
                        KillerHeuristik.storeKiller(move, ply);
                        KillerHeuristik.addHistory(move, depth);
                        break;
                    }
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

            if (transpositionTable) {
                tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
            }
            return score;

        } else {
            int score = infinity;
            for (int i = 0; i < moves.size(); i++) {
                Move move = moves.get(i);
                Move.makeMove(board, move);

                int childScore;
                if (i == 0) {
                    // Erster Zug: volles Fenster
                    childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1);
                } else {
                    // Alle anderen: Nullfenster
                    childScore = pvs(board, depth - 1, beta - 1, beta, deadline, ply + 1);
                    if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;
                    // Fail-low: Zug ist schlechter als beta, genauen Wert holen
                    if (childScore > alpha && childScore < beta) {
                        childScore = pvs(board, depth - 1, alpha, beta, deadline, ply + 1);
                    }
                }

                Move.unmakeMove(board, move);

                if (childScore == Integer.MIN_VALUE) return Integer.MIN_VALUE;

                if (childScore < score) {
                    bestMove = move;
                }

                score = Math.min(score, childScore);
                beta = Math.min(beta, score);
                if (killerHeuristik) {
                    if (alpha >= beta) {
                        KillerHeuristik.storeKiller(move, ply);
                        KillerHeuristik.addHistory(move, depth);
                        break;
                    }
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

            if (transpositionTable) {
                tt.put(board.hash, new TranspositionTable.Entry(depth, score, type, bestMove));
            }
            return score;
        }
    }
}