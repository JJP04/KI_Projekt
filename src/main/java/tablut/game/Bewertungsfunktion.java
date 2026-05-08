package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.List;

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
     * <p>
     */


    public static void bewerteStellung(Board board, List<Move> moves) {
        int minMaxBewertung = 0;
        for (Move move : moves) {
            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);
            GameLogic.toCapture(copy, move.toX, move.toY);
            //Bewertung der Spielsituation für Schwarz
            minMaxBewertung = +winStatus(copy);
            minMaxBewertung = +escapeKing(copy);
            minMaxBewertung = +pressureKing(copy);
            minMaxBewertung = +distanceCorner(copy);
            minMaxBewertung = +material(copy);
        }
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
     * Wenn König durch zug mehr felder hat, die er betreten kann, dann besser.
     *
     * @param board
     * @return int
     */
    public static int escapeKing(Board board) {

        return 0;
    }

    /**
     * Druck auf König (-150):
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter.
     *
     * @param board
     * @return int
     */
    public static int pressureKing(Board board) {

        return 0;
    }

    /**
     * Abstand zur Ecke (-10 * Distanz):
     * Misst Entfernung zur nächsten Ecke, je näher König an Ecke desto besser, desto kleiner der Minus Wert
     *
     * @param board
     * @return int
     */
    public static int distanceCorner(Board board) {
        int kingX = board.kingPos[0];
        int kingY = board.kingPos[1];

        int[][] cornsers = Board.corners;

        int minDistance = 0;
        for (int[] corner : cornsers) {
            int distance = Math.abs(kingX - corner[0]) + Math.abs(kingY - corner[1]);
            minDistance = Math.min(minDistance, distance);
        }
        return minDistance * -10;
    }

    /**
     * Berechnet Anzahl der Figuren (aktuell gleichwertigkeit von weiß und schwarzer Figur:
     * schwarz = 16x1 = 16 Punkte
     * weiß = 8x2 = 16 Punkte
     * Wenn eine Figur geschlagen werden würde, wäre eben + oder -
     *
     * @param board
     * @return int
     */
    public static int material(Board board) {
        int counterBlack = 0;
        int counterWhite = 0;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (board.playingBoard[i][j] == Board.BLACK) {
                    counterBlack++;
                }
                if (board.playingBoard[i][j] == Board.WHITE) {
                    counterWhite++;
                }
            }
        }
        return (counterWhite * 2) - (counterBlack * 1);
    }
}
