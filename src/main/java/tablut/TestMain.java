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
    
         

       MoveFactory m = new MoveFactory();
       List<Move> moves = m.getAllMoves(b);
       System.out.println(moves.size());

       for (Move move : moves) {
           System.out.println(
                   "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")"
           );
        }
        String f_2 = "4r4/9/9/4R4/3rKr3/4r4/9/9/9 s 20 1";
        Board ba = FenParser.parse(f_2);

        System.out.println(Perft.perft(new Board(),1));
    }





}
