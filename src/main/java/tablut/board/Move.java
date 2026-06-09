package tablut.board;

import tablut.game.GameLogic;
import tablut.ki.ZobristHashing;

//Objekt aus einem Zug
public class Move {
    public int fromX, fromY;
    public int toX, toY;

    //Undo Data
    public Capture[] capturedFigures = new Capture[2]; //Values: capturedX, capturedY, capturedFigure
    public int caputreCount = 0;
    public long oldHash;
    public boolean oldTurn;

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

        GameLogic.toCapture(board, move, move.toX, move.toY);

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Move)) return false;
        Move m = (Move) o;
        return fromX == m.fromX && fromY == m.fromY && toX == m.toX && toY == m.toY;
    }

    @Override
    public int hashCode() {
        return fromX * 1000 + fromY * 100 + toX * 10 + toY;
    }
}