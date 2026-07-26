package tablut.board;

import tablut.game.GameLogic;
import tablut.ki.ZobristHashing;

import java.util.Arrays;

public class Move {
    public final int fromX;
    public final int fromY;
    public final int toX;
    public final int toY;

    //speichert geschlagene Figuren
    public final Capture[] capturedFigures = new Capture[4]; //Values: capturedX, capturedY, capturedFigure
    public int caputreCount = 0;

    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    /**
     * Führt den Zug auf dem Board aus und aktualisiert den Hashwert
     */
    public static void makeMove(Board board, Move move) {
        move.caputreCount = 0;
        Arrays.fill(move.capturedFigures, null);

        //Figurentyp bestimmen
        int figure = board.playingBoard[move.fromX][move.fromY];

        //Board Update mit dem Zug
        board.playingBoard[move.toX][move.toY] = figure;
        board.playingBoard[move.fromX][move.fromY] = Board.EMPTY;

        if (figure == Board.KING) {
            board.kingPos[0] = move.toX;
            board.kingPos[1] = move.toY;
        }

        GameLogic.toCapture(board, move, move.toX, move.toY);

        //Hash-Feld bestimmen
        int figureIndex = ZobristHashing.pieceToIndex(figure);
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        //Hash Wert updaten
        board.hash ^= ZobristHashing.zobristTable[figureIndex][fromIndex];
        board.hash ^= ZobristHashing.zobristTable[figureIndex][toIndex];
        board.hash ^= ZobristHashing.blackToMoveKey;

        board.playBlackTurn = !board.playBlackTurn;
    }

    public static void unmakeMove(Board board, Move move) {
        int figure = board.playingBoard[move.toX][move.toY];

        board.playingBoard[move.fromX][move.fromY] = figure;
        board.playingBoard[move.toX][move.toY] = Board.EMPTY;

        if (figure == Board.KING) {
            board.kingPos[0] = move.fromX;
            board.kingPos[1] = move.fromY;
        }

        if (move.capturedFigures[0] != null) {
            board.playingBoard[move.capturedFigures[0].x][move.capturedFigures[0].y] = move.capturedFigures[0].figure;

            hashCapturedFigurBack(board, move, 0);
        }
        if (move.capturedFigures[1] != null) {
            board.playingBoard[move.capturedFigures[1].x][move.capturedFigures[1].y] = move.capturedFigures[1].figure;

            hashCapturedFigurBack(board, move, 1);
        }

        if (move.capturedFigures[2] != null) {
            board.playingBoard[move.capturedFigures[2].x][move.capturedFigures[2].y] = move.capturedFigures[2].figure;

            hashCapturedFigurBack(board, move, 2);
        }

        if (move.capturedFigures[3] != null) {
            board.playingBoard[move.capturedFigures[3].x][move.capturedFigures[3].y] = move.capturedFigures[3].figure;

            hashCapturedFigurBack(board, move, 3);
        }

        //Hash-Feld bestimmen
        int pieceIndex = ZobristHashing.pieceToIndex(figure);
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        //Hash zurücksetzen
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][toIndex];
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][fromIndex];
        board.hash ^= ZobristHashing.blackToMoveKey;

        board.playBlackTurn = !board.playBlackTurn;
    }

    public static void hashCapturedFigurBack(Board board, Move move, int index) {
        //Hash geschlagene Figur zurück
        int capturedFigure = ZobristHashing.pieceToIndex(move.capturedFigures[index].figure);
        int field = (move.capturedFigures[index].x - 1) * 9 + (move.capturedFigures[index].y - 1);
        board.hash ^= ZobristHashing.zobristTable[capturedFigure][field];
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Move m)) return false;
        return fromX == m.fromX && fromY == m.fromY && toX == m.toX && toY == m.toY;
    }

    @Override
    public int hashCode() {
        return fromX * 1000 + fromY * 100 + toX * 10 + toY;
    }
}