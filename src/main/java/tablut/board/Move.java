package tablut.board;

import tablut.ki.ZobristHashing;

//Objekt aus einem Zug
public class Move {
    public int fromX, fromY;
    public int toX, toY;

    public int capturedPiece; // Für unmakeMove, um die geschlagene Figur zu speichern

    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public static void makeMove(Board board, Move move) {
        int piece = board.playingBoard[move.fromX][move.fromY];

        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        int pieceIndex = ZobristHashing.pieceToIndex(piece);

        // Hash update
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][fromIndex];
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][toIndex];

        // Board update
        board.playingBoard[move.toX][move.toY] = piece;
        board.playingBoard[move.fromX][move.fromY] = Board.EMPTY;
    }

    public static void unmakeMove(Board board, Move move, int capturedPiece) {
        int piece = board.playingBoard[move.toX][move.toY];

        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        int pieceIndex = ZobristHashing.pieceToIndex(piece);

        // Hash zurück
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][toIndex];
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][fromIndex];

        // Board zurück
        board.playingBoard[move.fromX][move.fromY] = piece;
        board.playingBoard[move.toX][move.toY] = capturedPiece;
    }


}