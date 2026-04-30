package tablut;

import tablut.board.Board;
import tablut.game.GameLogic;
import tablut.game.Perft;

public class TestMain {


    public static void main(String[] args) {
        Board b = new Board();

    b.playingBoard[5][5] = Board.KING;

    b.playingBoard[4][5] = Board.BLACK;
    b.playingBoard[5][4] = Board.BLACK;
    b.playingBoard[6][5] = Board.BLACK;
    b.playingBoard[5][6] = Board.WHITE;
    b.playingBoard[5][7] = Board.BLACK;

    int x = 5;
    int y = 7;

    GameLogic.basicThroneCapture(b, x, y);
    }





}
