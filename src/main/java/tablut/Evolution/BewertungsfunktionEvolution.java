package tablut.Evolution;

import tablut.board.Board;
import tablut.game.GameLogic;

import java.util.Arrays;

public class BewertungsfunktionEvolution {

    // Konfigurierbare Parameter der Bewertungsfunktion (über Getter/Setter anpassbar)
    private int winScore = 10000;                    // Basiswert für Gewinn/Verlust
    private int twoOpenLinesScore = 8000;            // zwei offene Fluchtlinien
    private int oneOpenLineWhiteTurnScore = 5000;    // eine offene Fluchtlinie + Weiß am Zug
    private int oneOpenLineScore = 300;              // eine offene Fluchtlinie (Schwarz am Zug)
    private int escapeKingWeight = 5;                // Gewicht pro Fluchtfeld des Königs
    private int pressureWeight = -15;                // Gewicht für Druck auf den König
    private int cornerReachedScore = 10000;          // König steht bereits auf einer Ecke
    private int distanceCornerWeight = -10;          // Gewicht pro Distanzfeld zur Ecke
    private int cornerBlockadeWeight = -25;          // Gewicht pro besetztem Blockadefeld
    private int materialWhiteWeight = 100;           // Materialwert je weißer Figur
    private int materialBlackWeight = 50;            // Materialwert je schwarzer Figur
    private int repetitionPenalty = -50;             // Strafe für Stellungswiederholung

    /**
     * Standard-Konstruktor: verwendet die oben festgelegten Default-Parameter.
     */
    public BewertungsfunktionEvolution() {
    }

    /**
     * Konstruktor, mit dem alle Parameter der Bewertungsfunktion direkt beim
     * Erstellen der Instanz festgelegt werden können.
     */
    public BewertungsfunktionEvolution(int winScore, int twoOpenLinesScore, int oneOpenLineWhiteTurnScore,
                                       int oneOpenLineScore, int escapeKingWeight, int pressureWeight,
                                       int cornerReachedScore, int distanceCornerWeight, int cornerBlockadeWeight,
                                       int materialWhiteWeight, int materialBlackWeight, int repetitionPenalty) {
        this.winScore = winScore;
        this.twoOpenLinesScore = twoOpenLinesScore;
        this.oneOpenLineWhiteTurnScore = oneOpenLineWhiteTurnScore;
        this.oneOpenLineScore = oneOpenLineScore;
        this.escapeKingWeight = escapeKingWeight;
        this.pressureWeight = pressureWeight;
        this.cornerReachedScore = cornerReachedScore;
        this.distanceCornerWeight = distanceCornerWeight;
        this.cornerBlockadeWeight = cornerBlockadeWeight;
        this.materialWhiteWeight = materialWhiteWeight;
        this.materialBlackWeight = materialBlackWeight;
        this.repetitionPenalty = repetitionPenalty;
    }

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
    public int ratePosition(Board board) {
        return ratePosition(board, 0);
    }

    public int ratePosition(Board board, int ply) {
        int win = winStatus(board, ply);
        //Spielende
        if (win != 0) return win;

        //Flucht möglichkeiten
        int openLines = countOpenEscapeLines(board);
        if (openLines >= 2) return twoOpenLinesScore - ply;          //Schwarz kann nur eine Linie blocken,dominant
        if (openLines == 1 && !board.playBlackTurn) {
            return oneOpenLineWhiteTurnScore - ply;                          //nur eine ecke frei und weiß am zug
        }



        int score = escapeKing(board) + pressureKing(board) + distanceCorner(board) + material(board) +  cornerBlockade(board) + checkBoardRepetition(board);

        if (openLines == 1) score += oneOpenLineScore;               //Schwarz, linie wird evetl geblocjkt
        return score;
    }

    public int winStatus(Board board, int ply) {
        if (GameLogic.whiteWin(board)) return winScore - ply;
        //König geschlagen: das Feld an kingPos ist dann nicht mehr der König
        if (board.playingBoard[board.kingPos[0]][board.kingPos[1]] != Board.KING) return -(winScore - ply);
        return 0;
    }

    /**
     * Fluchtmöglichkeiten des Königs (Anzahl * 5):
     * Wenn König durch Zug mehr felder hat, die er betreten kann, dann besser.
     * Vergleich der Anzahl an möglichen Felder des Königs vorher und nachher
     */
    public int escapeKing(Board board) {
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
        return moves * escapeKingWeight;
    }

    /**
     * Druck auf König:
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter für weiß.
     */
    public int pressureKing(Board board) {
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
        return pressure * pressureWeight;
    }

    /**
     * Abstand zur Ecke (-10 * Distanz):
     * Misst Entfernung zur nächsten Ecke, je näher König an Ecke desto besser, desto kleiner der Minus Wert
     */
    public int distanceCorner(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        int bestDist = Integer.MAX_VALUE;

        for (int[] corner : Board.corners) {
            int dist = Math.abs(kx - corner[0]) + Math.abs(ky - corner[1]);
            bestDist = Math.min(bestDist, dist);
        }
        if (bestDist == 0) return cornerReachedScore; // König steht bereits auf Ecke — Sieg

        return bestDist * distanceCornerWeight;
    }

    /**
     * Zählt die Ecken, zu denen der König eine komplett freie Turm-Linie hat.
     * Frei heißt: alle Felder zwischen König und Ecke sind leer (weiße UND schwarze Figuren blockieren).
     */
    public int countOpenEscapeLines(Board board) {
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
    private boolean isLineFree(Board board, int kx, int ky, int target, boolean scanColumn) {
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

    public int cornerBlockade(Board board) {
        int count = 0;
        for (int[] f : blockadeFields) {
            if (board.playingBoard[f[0]][f[1]] == Board.BLACK) count++;
        }
        return count * cornerBlockadeWeight;
    }


    /**
     * Berechnet Anzahl der Figuren  gleichwertigkeit von weiß und schwarzer Figur:
     * schwarz = 16x50 = 800 Punkte
     * weiß = 8x100 = 800 Punkte
     * Wenn eine Figur geschlagen werden würde, wäre eben + oder -
     */
    public int material(Board board) {
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
        return (whiteCount * materialWhiteWeight) - (blackCount * materialBlackWeight);
    }

    /**
     * Stellung Wiederholung:
     */
    public int checkBoardRepetition(Board board) {
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                return repetitionPenalty;
            }
        }
        return 0;
    }

    // Getter und Setter für die konfigurierbaren Parameter der Bewertungsfunktion
    public int getWinScore() {
        return winScore;
    }

    public void setWinScore(int value) {
        winScore = value;
    }

    public int getTwoOpenLinesScore() {
        return twoOpenLinesScore;
    }

    public void setTwoOpenLinesScore(int value) {
        twoOpenLinesScore = value;
    }

    public int getOneOpenLineWhiteTurnScore() {
        return oneOpenLineWhiteTurnScore;
    }

    public void setOneOpenLineWhiteTurnScore(int value) {
        oneOpenLineWhiteTurnScore = value;
    }

    public int getOneOpenLineScore() {
        return oneOpenLineScore;
    }

    public void setOneOpenLineScore(int value) {
        oneOpenLineScore = value;
    }

    public int getEscapeKingWeight() {
        return escapeKingWeight;
    }

    public void setEscapeKingWeight(int value) {
        escapeKingWeight = value;
    }

    public int getPressureWeight() {
        return pressureWeight;
    }

    public void setPressureWeight(int value) {
        pressureWeight = value;
    }

    public int getCornerReachedScore() {
        return cornerReachedScore;
    }

    public void setCornerReachedScore(int value) {
        cornerReachedScore = value;
    }

    public int getDistanceCornerWeight() {
        return distanceCornerWeight;
    }

    public void setDistanceCornerWeight(int value) {
        distanceCornerWeight = value;
    }

    public int getCornerBlockadeWeight() {
        return cornerBlockadeWeight;
    }

    public void setCornerBlockadeWeight(int value) {
        cornerBlockadeWeight = value;
    }

    public int getMaterialWhiteWeight() {
        return materialWhiteWeight;
    }

    public void setMaterialWhiteWeight(int value) {
        materialWhiteWeight = value;
    }

    public int getMaterialBlackWeight() {
        return materialBlackWeight;
    }

    public void setMaterialBlackWeight(int value) {
        materialBlackWeight = value;
    }

    public int getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(int value) {
        repetitionPenalty = value;
    }
}
