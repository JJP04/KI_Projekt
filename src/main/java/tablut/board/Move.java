package tablut.board;

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
        // Board update
        int figure = board.playingBoard[move.fromX][move.fromY];

        board.playingBoard[move.toX][move.toY] = figure;
        board.playingBoard[move.fromX][move.fromY] = Board.EMPTY;

        //Hash update
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        int figureIndex = ZobristHashing.pieceToIndex(figure);

        board.hash ^= ZobristHashing.zobristTable[figureIndex][fromIndex];
        board.hash ^= ZobristHashing.zobristTable[figureIndex][toIndex];
    }


    public static void unmakeMove(Board board, Move move) {
        //Board Update
        int figure = board.playingBoard[move.toX][move.toY];

        board.playingBoard[move.fromX][move.fromY] = figure;
        board.playingBoard[move.toX][move.toY] = Board.EMPTY;

        if (move.capturedFigures[0] != null) {
            board.playingBoard[move.capturedFigures[0].x][move.capturedFigures[0].y] = move.capturedFigures[0].figure;
        }
        if (move.capturedFigures[1] != null) {
            board.playingBoard[move.capturedFigures[1].x][move.capturedFigures[1].y] = move.capturedFigures[1].figure;
        }

        //Hash Update
        int fromIndex = (move.fromX - 1) * 9 + (move.fromY - 1);
        int toIndex = (move.toX - 1) * 9 + (move.toY - 1);

        int pieceIndex = ZobristHashing.pieceToIndex(figure);

        // Hash zurück
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][toIndex];
        board.hash ^= ZobristHashing.zobristTable[pieceIndex][fromIndex];
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