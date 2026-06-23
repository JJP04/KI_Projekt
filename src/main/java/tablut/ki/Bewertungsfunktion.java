package tablut.ki;

import tablut.board.Board;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;

import java.util.Arrays;

public class Bewertungsfunktion {
    /**
     * Bewertungsfunktion (weiß will max. und schwarz will min.)
     * Gewinnstatus weiß (+/-10000)
     * + Fluchtmöglichkeiten des Königs (mögliche Züge vom König * 10)
     * - Druck auf König  (100* Anzahl besetzer Felder um den König)
     * - Distanz zur Ecke (-10 * Distanz) -- je näher König an Ecke desto besser, desto kleiner der Minus Wert
     * + Material Weiß (Anzahl Figuren *5)
     * - Material Schwarz (Anzahl Figuren *3)
     */
    public static int ratePosition(Board board) {
        int win         = winStatus(board);
        int escape      = escapeKing(board);
        int pressure    = pressureKing(board);
        int distance    = distanceCorner(board);
        int mat         = material(board);
        int rep         = checkBoardRepetition(board);
        return win + escape + pressure + distance + mat + rep;
    }

    public static int winStatus(Board board) {
        if (GameLogic.whiteWin(board)) return 10000;
        if (GameLogic.blackWin(board)) return -10000;
        return 0;
    }

    /**
     * Fluchtmöglichkeiten des Königs (Anzahl * 20):
     * Wenn König durch Zug mehr felder hat, die er betreten kann, dann besser.
     * Vergleich der Anzahl an möglichen Felder des Königs vorher und nachher
     */
    public static int escapeKing(Board board) {
        int moves = MoveFactory.getFigurMoves(board, board.kingPos[0], board.kingPos[1]).size();
        return moves * 10;
    }

    /**
     * Druck auf König (-100):
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter für weiß.
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
                        pressure += (4 - distance);  // Dist 1→3, Dist 2→2, Dist 3→1
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
        return pressure * -10;
    }
    /**
     * Abstand zur Ecke (-80 * Distanz):
     * Misst Entfernung zur nächsten Ecke, je näher König an Ecke desto besser, desto kleiner der Minus Wert
     */
    public static int distanceCorner(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        int bestScore = Integer.MIN_VALUE;

        for (int[] corner : Board.corners) {
            int cx = corner[0];
            int cy = corner[1];

            if (kx == cx && ky == cy) {
                return 10000; // König steht bereits auf Ecke — Sieg
            }

            if (kx == cx) {
                // gleiche Reihe
                int blockers = countBlockers(board, kx, ky, cx, cy, false);
                bestScore = Math.max(bestScore, evalEscapeLine(blockers));

            } else if (ky == cy) {
                // gleiche Spalte
                int blockers = countBlockers(board, kx, ky, cx, cy, true);
                bestScore = Math.max(bestScore, evalEscapeLine(blockers));

            } else {
                // nicht auf gleicher Linie — Manhattan als Fallback
                int dist = Math.abs(kx - cx) + Math.abs(ky - cy);
                bestScore = Math.max(bestScore, dist * -30);
            }
        }
        return bestScore;
    }

    //Zählt schwarze figuren zwischen König und Ecke auf einer Linie
    private static int countBlockers(Board board, int kx, int ky,
                                     int cx, int cy, boolean scanRow) {
        int blockers = 0;
        if (scanRow) {
            // gleiche Spalte (ky == cy)
            int step = (cx > kx) ? 1 : -1;
            for (int x = kx + step; x != cx; x += step) {
                if (board.playingBoard[x][ky] == Board.BLACK) blockers++;
            }
        } else {
            // gleiche Reihe (kx == cx)
            int step = (cy > ky) ? 1 : -1;
            for (int y = ky + step; y != cy; y += step) {
                if (board.playingBoard[kx][y] == Board.BLACK) blockers++;
            }
        }
        return blockers;
    }

    //Bewertun Blockaden durch Schearfz
    private static int evalEscapeLine(int blockers) {
        if (blockers == 0) return 150;
        return blockers * -40;
    }


    /**
     * Berechnet Anzahl der Figuren (aktuell gleichwertigkeit von weiß und schwarzer Figur:
     * schwarz = 16x3 = 48 Punkte
     * weiß = 8x5 = 40 Punkte
     * Wenn eine Figur geschlagen werden würde, wäre eben + oder -
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
        return (whiteCount * 30) - (blackCount * 15);
    }

    /**
     * Stellung Wiederholung (+5000 / -5000):
     */
    public static int checkBoardRepetition(Board board) {
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                return -50;
            }
        }
        return 0;
    }
}
