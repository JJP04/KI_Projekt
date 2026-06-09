package tablut;

import java.util.List;

import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Bewertungsfunktion;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.game.Perft;
import tablut.ki.SearchMoves;

public class TestMain {

    //Board übergeben und besten Zug ermitteln, danach alle möglichen Züge durchgehen und bewerten

    /**
     * Ermittelt besten Zug für eine gegebene Stellung inklusive Bewertung
     * Anschließend werden alle möglichen Züge durchgegangen inklusive Bewertungen
     */
    public static void main(String[] args) {

        //Bordstellung manuell angeben:
        String al_1 = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
        Board board = FenParser.parse(al_1);
        Board bestBoard = board.copy();
        board.printBoard();
        //Suchtiefe manuell angeben:
        SearchMoves.maxDepth = 1;

        Move move = SearchMoves.findBestMoveAlphaBeta(board, 10000000);
        MoveFactory.moveFigure(bestBoard, move);
        int scoreBestmove = Bewertungsfunktion.ratePosition(bestBoard);
        System.out.println(
                "Entscheidung AlphaBeta:" +
                        "(" + move.fromX + "," + move.fromY + ") -> (" + move.toX + "," + move.toY + ")" +
                        " Score: " + scoreBestmove
        );

        for (Move m : MoveFactory.getAllMoves(board)) {
            Board copy = board.copy();
            MoveFactory.moveFigure(copy, move);
            int score = Bewertungsfunktion.ratePosition(copy);
            System.out.println(
                    "(" + m.fromX + "," + m.fromY + ") -> (" +
                            m.toX + "," + m.toY + ")"
            );
            System.out.println("Score: " + score);
        }
    }

    /**
     * Perft Test: Berechnet die Anzahl der möglichen Stellungen in einer bestimmten Tiefe.
     */
    public static void perft(String fen) {
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
        System.out.println(Perft.perft(board, 1));
        System.out.println(Perft.perft(board, 2));
        System.out.println(Perft.perft(board, 3));
        System.out.println(Perft.perft(board, 4));
    }
}
