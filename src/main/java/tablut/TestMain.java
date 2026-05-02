package tablut;

import java.util.List;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.game.Perft;

public class TestMain {


    public static void main(String[] args) {
        Board b = new Board();

           for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (i == 0 || i == 10 || j == 0 || j == 10) {
                    b.playingBoard[i][j] = Board.BORDER;
                } else {
                    b.playingBoard[i][j] = Board.EMPTY;
                }
            }
        }


    b.playingBoard[8][2] = Board.KING;

    b.playingBoard[7][4] = Board.BLACK;

        String ac_1 =  "3rrr3/9/3R5/8r/rr7/r1R1RR3/4R3r/4rKRr1/5rr2 s 0 21"; //soll 15, wir haben 11

        Board b1 = FenParser.parse(ac_1);
        b1.printBoard();

        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(b1);
        System.out.println(moves.size());

        for (Move move : moves) {
            System.out.println(
                    "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")"
            );
        }
        System.out.println(Perft.perft(b1,3));
    }





}
