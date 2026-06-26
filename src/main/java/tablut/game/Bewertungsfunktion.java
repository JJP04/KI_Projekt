package tablut.game;

import tablut.board.Board;

import java.util.Arrays;

public class Bewertungsfunktion {
    /**
     * Bewertungsfunktion (weiß will max. und schwarz will min.)
     * Gewinnstatus weiß (+/-(10000 - ply)) -- schnellere Siege werden bevorzugt
     * + Offene Fluchtlinien des Königs zur Ecke (2 Linien: +8000, 1 Linie + Weiß am Zug: +5000, sonst +300)
     * + Fluchtmöglichkeiten des Königs (mögliche Züge vom König * 5)
     * - Druck auf König  (gestaffelt nach Distanz der schwarzen Figuren zum König)
     * - Distanz zur Ecke (-10 * Distanz) -- je näher König an Ecke desto besser, desto kleiner der Minus Wert
     * + Material Weiß (Anzahl Figuren *100)
     * - Material Schwarz (Anzahl Figuren *50)
     * - Eck-Blockaden durch Schwarz (-25 pro besetztem Blockadefeld)
     */
    public static int ratePosition(Board board) {
        return ratePosition(board, 0);
    }

    public static int ratePosition(Board board, int ply) {
        int win = winStatus(board, ply);
        //Spielende: keine weiteren Terme mehr nötig
        if (win != 0) return win;

        //Offene Fluchtlinien dominieren alles außer dem Spielende
        int openLines = countOpenEscapeLines(board);
        if (openLines >= 2) return 8000 - ply;          //Schwarz kann nur eine Linie blocken --> praktisch gewonnen
        if (openLines == 1 && !board.playBlackTurn) {
            return 5000 - ply;                          //Weiß am Zug läuft direkt in die Ecke
        }

        int escape      = escapeKing(board);
        int pressure    = pressureKing(board);
        int distance    = distanceCorner(board);
        int mat         = material(board);
        int blockade    = cornerBlockade(board);
        int rep         = checkBoardRepetition(board);

        int score = escape + pressure + distance + mat + blockade + rep;
        if (openLines == 1) score += 300;               //Schwarz am Zug muss die Linie erst blocken
        return score;
    }

    public static int winStatus(Board board) {
        return winStatus(board, 0);
    }

    public static int winStatus(Board board, int ply) {
        if (GameLogic.whiteWin(board)) return 10000 - ply;
        //König geschlagen: das Feld an kingPos ist dann nicht mehr der König
        if (board.playingBoard[board.kingPos[0]][board.kingPos[1]] != Board.KING) return -(10000 - ply);
        return 0;
    }

    /**
     * Fluchtmöglichkeiten des Königs (Anzahl * 5):
     * Wenn König durch Zug mehr felder hat, die er betreten kann, dann besser.
     * Vergleich der Anzahl an möglichen Felder des Königs vorher und nachher
     */
    public static int escapeKing(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];
        int moves = 0;

        for (int[] dir : Board.directions) {
            int nx = kx + dir[0];
            int ny = ky + dir[1];
            while (board.playingBoard[nx][ny] == Board.EMPTY) {
                //leerer Thron darf übersprungen, aber nicht betreten werden
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
     * Druck auf König:
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
        return pressure * -15;
    }

    /**
     * Abstand zur Ecke (-10 * Distanz):
     * Misst Entfernung zur nächsten Ecke, je näher König an Ecke desto besser, desto kleiner der Minus Wert
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

        return bestDist * -10;
    }

    /**
     * Zählt die Ecken, zu denen der König eine komplett freie Turm-Linie hat.
     * Frei heißt: alle Felder zwischen König und Ecke sind leer (weiße UND schwarze Figuren blockieren).
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

    //Prüft ob alle Felder zwischen König und Ecke auf einer Linie leer sind
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

    //Bewertun Blockaden durch Schearfz:
    //Schwarze Steine auf den Zugangsfeldern der Ecken riegeln die Fluchtwege dauerhaft ab (gut für Schwarz --> negativ)
    private static final int[][] blockadeFields = {
            {1, 3}, {3, 1}, {2, 2},   // Ecke (1,1)
            {1, 7}, {3, 9}, {2, 8},   // Ecke (1,9)
            {9, 3}, {7, 1}, {8, 2},   // Ecke (9,1)
            {9, 7}, {7, 9}, {8, 8}    // Ecke (9,9)
    };

    public static int cornerBlockade(Board board) {
        int count = 0;
        for (int[] f : blockadeFields) {
            if (board.playingBoard[f[0]][f[1]] == Board.BLACK) count++;
        }
        return count * -25;
    }

    /**
     * Berechnet Anzahl der Figuren (aktuell gleichwertigkeit von weiß und schwarzer Figur:
     * schwarz = 16x50 = 800 Punkte
     * weiß = 8x100 = 800 Punkte
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
        return (whiteCount * 100) - (blackCount * 50);
    }

    /**
     * Stellung Wiederholung:
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
