package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

import java.util.Arrays;
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
     */


    //Nutze FindestbemoveAlphaBeta aus SearchMove anstatt das hier
    public static Move bewerteStellung(Board board, List<Move> moves) {
        int bestMinMaxBewertung = board.playBlackTurn ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        Move bestMove = null;
        for (Move move : moves) {
            int minMaxBewertung = 0;
            Board copy = board.copy();
            GameLogic.moveFigure(copy, move.fromX, move.fromY, move.toX, move.toY);
            // GameLogic.toCapture(copy, move.toX, move.toY);
            //Bewertung der Spielsituation für Schwarz
            minMaxBewertung += winStatus(copy);
            minMaxBewertung += escapeKing(board, copy);
            minMaxBewertung += pressureKing(board, copy);
            minMaxBewertung += distanceCorner(copy);
            minMaxBewertung += material(copy);

            if (board.playBlackTurn && minMaxBewertung < bestMinMaxBewertung) {
                bestMinMaxBewertung = minMaxBewertung;
                bestMove = move;
            } else if (!board.playBlackTurn && minMaxBewertung > bestMinMaxBewertung) {
                bestMinMaxBewertung = minMaxBewertung;
                bestMove = move;
            }
        }
        return bestMove;
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
     *
     * @param board
     * @return int
     */
    public static int escapeKing(Board board, Board boardCopy) {
        int beforCounter = MoveFactory.getFigurMoves(board, board.kingPos[0], board.kingPos[1]).size();
        int afterCounter = MoveFactory.getFigurMoves(boardCopy, boardCopy.kingPos[0], boardCopy.kingPos[1]).size();
        return (afterCounter - beforCounter) * 200;
    }

    /**
     * Druck auf König (-150):
     * Wenn durch Zug mehr schwarze Figuren um den König sind, dann schlechter für weiß.
     *
     * @param board
     * @return int
     */
    public static int pressureKing(Board board, Board boardCopy) {
        int beforPressure = 0;
        int afterPressure = 0;
        int[][] directions = Board.directions;
        for (int[] direction : directions) {
            int[] fieldB = GameLogic.moveXFields(board.kingPos[0], board.kingPos[1], direction, 1);
            if (board.playingBoard[fieldB[0]][fieldB[1]] == board.BLACK) {
                beforPressure++;
                int[] fieldA = GameLogic.moveXFields(boardCopy.kingPos[0], boardCopy.kingPos[1], direction, 1);
                if (boardCopy.playingBoard[fieldA[0]][fieldA[1]] == boardCopy.BLACK) {
                    afterPressure++;
                }
            }
        }
        int pressure = afterPressure - beforPressure;
        return pressure * -150;
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
     *
     * @param board
     * @return int
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
     * Neue Methoden, mehr auf Alpha Beta algo angepasst
     *
     */

    public static int escapeKingAlphaBeta(Board board) {
        int moves = MoveFactory.getFigurMoves(board, board.kingPos[0], board.kingPos[1]).size();
        return moves * 50;
    }

    public static int pressureKingAlphaBeta(Board board) {
        int pressure = 0;
        for (int[] dir : Board.directions) {
            int[] field = GameLogic.moveXFields(board.kingPos[0], board.kingPos[1], dir, 1);
            if (board.playingBoard[field[0]][field[1]] == Board.BLACK) {
                pressure++;
            }
        }
        return pressure * -150;
    }


    //Bestrafft Stellung wiederHohlung
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

