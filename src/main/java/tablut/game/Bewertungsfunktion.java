package tablut.game;

import tablut.board.Board;

import java.util.Arrays;

//Funktion zur Bewertung der Spielsituation, damit die KI den besten Zug auswählen kann
//Wird in xy Klasse aufgerufen und itteriert dann über alle Züge rüber
//Alle Züge durchlaufen, eine Kopie des Spielfelds erstellen und dann den Zug bewerten (Punkte geben) und dann den Zug mit der höchsten Bewertung auswählen
public class Bewertungsfunktion {

    /**
     * Bewertungsfunktion (weiß will max. und schwarz will min.)
     * Gewinnstatus weiß (+/-10000)
     * + Fluchtmöglichkeiten des Königs (+200)
     * - Druck auf König  (-150)
     * - Distanz zur Ecke (-10 * Distanz) -- je näher König an Ecke desto besser, desto kleiner der Minus Wert
     * + Material Weiß (Anzahl Figuren * 1 oder +5)
     * - Material Schwarz (Anzahl Figuren * 0.5 oder +3)
     */

    public static int ratePosition(Board board) {
        return winStatus(board)          // Gewonnen? +10000 / -10000
                + escapeKing(board)      // Fluchtmöglichkeiten
                + pressureKing(board)    // Druck auf König
                + distanceCorner(board)  // König Distanz Ecke
                + material(board);       // Materialwert
    }

    public static int winStatus(Board board) {
        if (GameLogic.isGameOver(board)) {
            if (board.playBlackTurn) {
                return 10000; //Weiß gewinnt
            } else {
                return -10000; //Schwarz gewinnt
            }
        }
        return 0; //Spiel ist nicht vorbei
    }

    /**
     * Fluchtmöglichkeiten des Königs (+200):
     * Wenn König durch Zug mehr felder hat, die er betreten kann, dann besser.
     * Vergleich der Anzahl an möglichen Felder des Königs vorher und nachher
     */
    public static int escapeKing(Board board) {
        int moves = MoveFactory.getFigurMoves(board, board.kingPos[0], board.kingPos[1]).size();
        return moves * 50;
    }

    /**
     * Druck auf König (-150):
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter für weiß.
     */
    public static int pressureKing(Board board) {
        int pressure = 0;
        for (int[] dir : Board.directions) {
            int[] field = GameLogic.moveXFields(board.kingPos[0], board.kingPos[1], dir, 1);
            if (board.playingBoard[field[0]][field[1]] == Board.BLACK) {
                pressure++;
            }
        }
        return pressure * -150;
    }

    /**
     * Abstand zur Ecke (-80 * Distanz):
     * Misst Entfernung zur nächsten Ecke, je näher König an Ecke desto besser, desto kleiner der Minus Wert
     */
    public static int distanceCorner(Board board) {
        int kingX = board.kingPos[0];
        int kingY = board.kingPos[1];

        int[][] cornsers = Board.corners;

        int minDistance = Integer.MAX_VALUE;
        for (int[] corner : cornsers) {
            int distance = Math.abs(kingX - corner[0]) + Math.abs(kingY - corner[1]);
            minDistance = Math.min(minDistance, distance);
        }
        return minDistance * -80;
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
        return (whiteCount * 5) - (blackCount * 3);
    }

    /**
     * Stellung Wiederholung (+5000 / -5000):
     */
    public static int checkBoardRepetition(Board board) {
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                // Stellung schon mal vorgekommen?
                return board.playBlackTurn ? 5000 : -5000;
            }
        }
        return 0; // noch nie vorgekommen
    }
}

