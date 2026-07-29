package tablut.ki;

import tablut.board.Board;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;

import java.util.Arrays;

public class Bewertungsfunktion {

    public static int ratePosition(Board board) {
        return ratePosition(board, 0);
    }

    public static int ratePosition(Board board, int ply) {
        int win = winStatus(board, ply);
        //Spielende
        if (win != 0) return win;

        //Flucht möglichkeiten
        int openLines = countOpenEscapeLines(board);
        if (openLines >= 2) return 7907 - ply;          //Schwarz kann nur eine Linie blocken,dominant
        if (openLines == 1 && !board.playBlackTurn) {
            return 3999 - ply;                          //nur eine ecke frei und weiß am zug
        }


        int score = escapeKing(board) + pressureKing(board) + distanceCorner(board) + material(board) + cornerBlockade(board) + checkBoardRepetition(board);

        if (openLines == 1) score += 864;               //Schwarz, linie wird evtl geblockt
        return score;
    }

    public static int winStatus(Board board, int ply) {
        if (GameLogic.whiteWin(board)) return 10000 - ply;
        //König geschlagen: das Feld an kingPos ist dann nicht mehr der König
        if (board.playingBoard[board.kingPos[0]][board.kingPos[1]] != Board.KING) return -(10000 - ply);
        return 0;
    }

    /**
     * Fluchtmöglichkeiten des Königs (Anzahl * 5)
     * Wenn König durch Zug mehr felder hat, die er betreten kann, dann besser
     */
    public static int escapeKing(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];
        int moves = 0;

        for (int[] dir : Board.directions) {
            int nx = kx + dir[0];
            int ny = ky + dir[1];
            while (board.playingBoard[nx][ny] == Board.EMPTY) {
                //leerer Thron darf übersprungen  nicht betreten
                if (!(nx == Board.throne[0] && ny == Board.throne[1])) {
                    moves++;
                }
                nx += dir[0];
                ny += dir[1];
            }
        }
        return moves * 5;
    }

    /**
     * Druck auf König
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter für weiß
     */
    public static int pressureKing(Board board) {
        int pressure = 0;
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        for (int[] dir : Board.directions) {
            int nx = kx + dir[0];
            int ny = ky + dir[1];
            int distance = 1;

            while (board.playingBoard[nx][ny] != Board.BORDER) {
                if (board.playingBoard[nx][ny] == Board.BLACK) {
                    if (distance <= 3) {
                        pressure += (4 - distance);
                    }
                    break; // dahinter abgeschirmt
                }
                if (board.playingBoard[nx][ny] == Board.WHITE) {
                    break; // eigene Figur schirmt ab
                }
                nx += dir[0];
                ny += dir[1];
                distance++;
            }
        }
        return pressure * -12;
    }

    /**
     * Abstand zur Ecke (-10 * Distanz)
     * Misst Entfernung zur nächsten Ecke je näher König an Ecke desto besser, desto kleiner der Minus Wert
     */
    public static int distanceCorner(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        int bestDist = Integer.MAX_VALUE;

        for (int[] corner : Board.corners) {
            int dist = Math.abs(kx - corner[0]) + Math.abs(ky - corner[1]);
            bestDist = Math.min(bestDist, dist);
        }
        if (bestDist == 0) return 10000; // König steht bereits auf Ecke — Sieg

        return bestDist * -20;
    }

    /**
     * Zählt die Ecken, zu denen der König eine komplett freie Turm Linie hat
     */
    public static int countOpenEscapeLines(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];
        int open = 0;

        for (int[] corner : Board.corners) {
            int cx = corner[0];
            int cy = corner[1];

            if (kx == cx && ky == cy) continue; // steht schon drauf — winStatus deckt das ab

            if (kx == cx) {
                // gleiche Reihe
                if (isLineFree(board, kx, ky, cy, false)) open++;
            } else if (ky == cy) {
                // gleiche Spalte
                if (isLineFree(board, kx, ky, cx, true)) open++;
            }
        }
        return open;
    }

    /**
     *
     * Prüft ob alle Felder zwischen König und Ecke auf einer Linie leer sind
     */
    private static boolean isLineFree(Board board, int kx, int ky, int target, boolean scanColumn) {
        if (scanColumn) {
            // gleiche Spalte (ky == cy)
            int step = (target > kx) ? 1 : -1;
            for (int x = kx + step; x != target; x += step) {
                if (board.playingBoard[x][ky] != Board.EMPTY) return false;
            }
        } else {
            // gleiche Reihe (kx == cx)
            int step = (target > ky) ? 1 : -1;
            for (int y = ky + step; y != target; y += step) {
                if (board.playingBoard[kx][y] != Board.EMPTY) return false;
            }
        }
        return true;
    }

    private static final int[][] blockadeFields = {{1, 3}, {3, 1}, {2, 2},   // Ecke (1,1)
            {1, 7}, {3, 9}, {2, 8},   // Ecke (1,9)
            {9, 3}, {7, 1}, {8, 2},   // Ecke (9,1)
            {9, 7}, {7, 9}, {8, 8}    // Ecke (9,9)
    };

    public static int cornerBlockade(Board board) {
        int count = 0;
        for (int[] f : blockadeFields) {
            if (board.playingBoard[f[0]][f[1]] == Board.BLACK) count++;
        }
        return count * -26;
    }


    /**
     * Berechnet Anzahl der Figuren
     *
     */
    public static int material(Board board) {
        int blackCount = 0;
        int whiteCount = 0;

        for (int r = 1; r <= 9; r++) {
            for (int c = 1; c <= 9; c++) {
                if (board.playingBoard[r][c] == Board.BLACK) {
                    blackCount++;
                }
                if (board.playingBoard[r][c] == Board.WHITE) {
                    whiteCount++;
                }
            }
        }
        return (whiteCount * 57) - (blackCount * 90);
    }

    /**
     * Stellung Wiederholung:
     */
    public static int checkBoardRepetition(Board board) {
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                return -44;
            }
        }
        return 0;
    }
}
