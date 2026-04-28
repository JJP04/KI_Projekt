package tablut;

import java.util.List;

import tablut.board.Board;
import tablut.board.Move;

public class Main {
    public static void main(String[] args) {
        Board b = new Board();
       // b.printBoard();
    
        List<Move> moves = b.getAllMoves(b);
        System.out.println(moves.size());

        for (Move m : moves) {
            System.out.println(
                "(" + m.fromX + "," + m.fromY + ") -> (" + m.toX + "," + m.toY + ")"
            );
        }
    }
}