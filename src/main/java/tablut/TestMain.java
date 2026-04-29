package tablut;

import tablut.board.Board;
import tablut.game.Perft;

public class TestMain {


    public static void main(String[] args) {
        Board board = new Board();
        board.printBoard(); // zur Kontrolle
        long perf = Perft.perft(board, 1);
        System.out.println(perf);
    }





}
