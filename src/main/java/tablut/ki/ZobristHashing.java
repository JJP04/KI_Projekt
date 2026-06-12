package tablut.ki;

import tablut.board.Board;

import java.util.Random;

/**
 * Erzeugt einen Hash für eine Stellung
 */
public class ZobristHashing {
    public static int Anzahl_Figuren = 3;
    public static int Anzahl_Felder = 81;
    public static final long[][] zobristTable = new long[Anzahl_Figuren][Anzahl_Felder];
    public static long blackToMoveKey;

    /**
     * Initialisiert die Zobrist-Tabellen mit zufälligen Werten.
     */
    public static void initializeZobristTable() {
        Random rand = new Random(12345);
        for (int figur = 0; figur < Anzahl_Figuren; figur++) {
            for (int feld = 0; feld < Anzahl_Felder; feld++) {
                zobristTable[figur][feld] = rand.nextLong();
            }
        }
        blackToMoveKey = rand.nextLong();
    }

    public static int pieceToIndex(int piece) {
        if (piece == Board.BLACK) return 0;
        if (piece == Board.WHITE) return 1;
        if (piece == Board.KING)  return 2;
        return 99;
    }

    /**
     * Berechnet den Zobrist-Hash für ein gegebenes Board.
     */
    public static long computeHash(Board board) {
        long hash = 0;
        for (int row = 1; row <= 9; row++) {
            for (int col = 1; col <= 9; col++) {
                int figur = board.playingBoard[row][col];
                if (figur != Board.EMPTY && figur != Board.BORDER) {
                    int pieceIndex = pieceToIndex(figur);
                    int fieldIndex = (row - 1) * 9 + (col - 1);
                    hash ^= zobristTable[pieceIndex][fieldIndex];
                }
            }
        }
        if (board.playBlackTurn) {
            hash ^= blackToMoveKey;
        }
        return hash;
    }

    /**
     * Aktualisiert den Zobrist-Hash, wenn eine Figur von einer Position zu einer anderen bewegt wird.
     * Effizienter als jedes mal gesamte Board zu berechnen
     */
    public static long updateHash(long hash, int piece, int fromRow, int fromCol, int toRow, int toCol) {
        int pieceIndex = pieceToIndex(piece);
        int fromFieldIndex = (fromRow - 1) * 9 + (fromCol - 1);
        int toFieldIndex = (toRow - 1) * 9 + (toCol - 1);

        // Entferne die Figur von der alten Position
        hash ^= zobristTable[pieceIndex][fromFieldIndex];
        // Füge die Figur an der neuen Position hinzu
        hash ^= zobristTable[pieceIndex][toFieldIndex];
        return hash;
    }

    public static long captureHash(long hash, int piece, int row, int col){
        int pieceIndex = pieceToIndex(piece);
        int fieldIndex = (row - 1) * 9 + (col - 1);
        hash ^= zobristTable[pieceIndex][fieldIndex];

        return hash;
    }
}

