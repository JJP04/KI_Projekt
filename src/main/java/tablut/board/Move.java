package tablut.board;

import tablut.game.GameLogic;
import tablut.ki.ZobristHashing;

public class Move {
    public int fromX, fromY;
    public int toX, toY;

    //Undo Data
    public Capture[] capturedFigures = new Capture[2]; //Values: capturedX, capturedY, capturedFigure
    public int caputreCount = 0;


    public Move(int fromX, int fromY, int toX, int toY) {
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public static void makeMove(Board board, Move move) {

        move.caputreCount = 0;
        move.capturedFigures[0] = null;
        move.capturedFigures[1] = null;
        int x = move.fromX;
        int y = move.fromY;

        // Board update
        int figure = board.playingBoard[move.fromX][move.fromY];

        board.playingBoard[move.toX][move.toY] = figure;
        board.playingBoard[move.fromX][move.fromY] = Board.EMPTY;

        if (figure == Board.KING) {
            board.kingPos[0] = move.toX;
            board.kingPos[1] = move.toY;
        }

        GameLogic.toCapture(board, move, x, y);

        //Hash update
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        int figureIndex = ZobristHashing.pieceToIndex(figure);

        board.hash ^= ZobristHashing.zobristTable[figureIndex][fromIndex];
        board.hash ^= ZobristHashing.zobristTable[figureIndex][toIndex];

        board.playBlackTurn = !board.playBlackTurn;
    }

    public static void unmakeMove(Board board, Move move) {
        //Board Update
        int figure = board.playingBoard[move.toX][move.toY];

        board.playingBoard[move.fromX][move.fromY] = figure;
        board.playingBoard[move.toX][move.toY] = Board.EMPTY;

        if (figure == Board.KING) {
            board.kingPos[0] = move.fromX;
            board.kingPos[1] = move.fromY;
        }

        if (move.capturedFigures[0] != null) {
            board.playingBoard[move.capturedFigures[0].x][move.capturedFigures[0].y] = move.capturedFigures[0].figure;

            //Hash geschlagene Figur zurück
            hashCapturedFigurBack(board, move, 0);
        }
        if (move.capturedFigures[1] != null) {
            board.playingBoard[move.capturedFigures[1].x][move.capturedFigures[1].y] = move.capturedFigures[1].figure;

            //Hash geschlagene Figur zurück
            hashCapturedFigurBack(board, move, 1);
        }

        //Hash Update
        int pieceIndex = ZobristHashing.pieceToIndex(figure);
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        // Hash zurück
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][toIndex];
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][fromIndex];

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
        if (!(o instanceof Move)) return false;
        Move m = (Move) o;
        return fromX == m.fromX && fromY == m.fromY && toX == m.toX && toY == m.toY;
    }

    @Override
    public int hashCode() {
        return fromX * 1000 + fromY * 100 + toX * 10 + toY;
    }
}