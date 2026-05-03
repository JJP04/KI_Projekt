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



        //    for (int i = 0; i < 11; i++) {
        //     for (int j = 0; j < 11; j++) {
        //         if (i == 0 || i == 10 || j == 0 || j == 10) {
        //             b.playingBoard[i][j] = Board.BORDER;
        //         } else {
        //             b.playingBoard[i][j] = Board.EMPTY;
        //         }
        //     }
        // }

        String s_1 = "2s2s3/2w6/9/w2ww3s/s2sKw1ss/s3w4/9/9/5s3 w 0 9";

        perft(s_1);
        

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
