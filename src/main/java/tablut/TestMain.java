package tablut;

import java.util.List;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
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

        String f_1 =  "9/5r3/6rR1/5RK2/6R1r/9/1R8/9/1r7 w 0 33";
        String f_2 = "4r4/9/9/4R4/3rKr3/4r4/9/9/9 s 0 23";

        perft(f_1);
        perft(f_2);
    }

    public static void perft(String fen){
        Board board = FenParser.parse(fen);
        board.printBoard();

        MoveFactory m = new MoveFactory();
        List<Move> moves = m.getAllMoves(board);
        System.out.println(moves.size());

        for (Move move : moves) {
            System.out.println(
                    "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")"
            );
        }
        System.out.println(Perft.perft(board,1));
        System.out.println(Perft.perft(board,2));
        System.out.println(Perft.perft(board,3));
        System.out.println(Perft.perft(board,4));
    }
}
