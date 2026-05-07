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
        int maxBewertung = 0;
        for (Move move : moves) {
            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);
            //Bewertung der Spielsituation für Schwarz
        }
    }
}

