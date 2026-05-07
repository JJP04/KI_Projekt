package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.List;

//Funktion zur Bewertung der Spielsituation, damit die KI den besten Zug auswählen kann
//Wird in xy Klasse aufgerufen und itteriert dann über alle Züge rüber
//Alle Züge durchlaufen, eine Kopie des Spielfelds erstellen und dann den Zug bewerten (Punkte geben) und dann den Zug mit der höchsten Bewertung auswählen

public class Bewertungsfunktion {

    /**
     * Bewertungsfunktion (weiß will max. und schwarz will min.) =
     * Gewinnstatus weiß (+/-10000)
     * + Fluchtmöglichkeiten des Königs (+200)
     * - Druck auf König durch schwarz (-150)
     * - Distanz zur Ecke (-10 * Distanz) -- je näher König an Ecke desto besser, desto kleiner der Minus Wert
     * + Material Weiß (Anzahl Figuren * 1 oder +5)
     * - Material Schwarz (Anzahl Figuren * 0.5 oder +3)
     * <p>
     * weiß:
     * König:
     * - Hoher Bonus, wenn Zug das Spiel beendet (König Ecke)
     * - Hoher Bonus, wenn König bedroht und Zug König in Sicherheit bringt
     * - Bonus, wenn König mit helfen Schlagen kann
     * Bauern:
     * - hoher Bonus wenn Zug eine eigene Figur schützt (weißer Bauer wertfoller als Schwarz Bauer)
     * - Bonus wenn gegnerische Figur geschlagen werden kann, aber eigene Figur nicht bedroht wird
     * <p>
     * schwarz:
     * Bauern:
     * - hoher Bonus wenn Zug eine gegnerische Figur bedroht (schwarzer Bauer wertvoller als weißer Bauer)
     * - Bonus wenn gegnerische Figur geschlagen werden kann, aber eigene Figur nicht bedroht wird
     */


    public static void bewerteStellung(Board board, List<Move> moves) {
        int minMaxBewertung = 0;
        for (Move move : moves) {
            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);
            //Bewertung der Spielsituation für Schwarz
            int bewertung = +winnStatus(copy);


        }
    }

    public static int winnStatus(Board board) {
        if (GameLogic.isGameOver(board)) {
            if (board.playBlackTurn) {
                return 10000; //Weiß gewinnt
            } else {
                return -10000; //Schwarz gewinnt
            }
        }
        return 0; //Spiel ist nicht vorbei
    }

    public static int fluchtMöglichkeitenKönig(Board board) {

        return 0;
    }

    public static int druckAufKönig(Board board) {

        return 0;
    }

    public static int DistanzEcke(Board board) {
        int kingX = board.kingPos[0];
        int kingY = board.kingPos[1];

        int[][] cornsers = GameLogic.corners;

        int minDistance = 0;
        for (int[] corner : cornsers) {
            int distance = Math.abs(kingX - corner[0]) + Math.abs(kingY - corner[1]);
            minDistance = Math.min(minDistance, distance);
        }
        return minDistance * -10;
    }

    public static int Materialwert(Board board) {
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

    blackCount=-(blackCount*5);
    whiteCount=(whiteCount*3);
    return blackCount + whiteCount;
    }


}

