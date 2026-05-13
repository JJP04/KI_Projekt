package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Bewertungsfunktion;
import tablut.game.GameLogic;
import tablut.game.MoveFactory;
import tablut.game.Perft;
import tablut.ki.SearchMoves;

import static org.junit.jupiter.api.Assertions.*;

public class BewertungsfunktionTests {


    String whiteWins1 = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
    @Test
    void whiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 10000000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(7, moveKI.fromX),
                () -> assertEquals(6, moveKI.fromY),
                () -> assertEquals(9, moveKI.toX),
                () -> assertEquals(6, moveKI.toY)
        );
    }

    String einKlemmungAnderSeite = "9/5r3/6rR1/5RK2/6R1r/9/1R8/9/1r7 w 0 33";
    @Test
    void einKlemmungAnderSeiteTest() {
        Board board = FenParser.parse(einKlemmungAnderSeite);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 10000000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(5, moveKI.fromX),
                () -> assertEquals(4, moveKI.fromY),
                () -> assertEquals(4, moveKI.toX),
                () -> assertEquals(4, moveKI.toY)
        );
    }


    String schlagÜberThron = "4r4/9/9/4R4/3rKr3/4r4/9/9/9 s 0 23";
    @Test
    void schlagÜberThronTest() {
        Board board = FenParser.parse(schlagÜberThron);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 10000000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);

        GameLogic.moveFigure(board, moveKI.fromX, moveKI.fromY, moveKI.toX, moveKI.toY);

        board.printBoard();

        assertAll(
                () -> assertEquals(1, moveKI.fromX),
                () -> assertEquals(5, moveKI.fromY),
                () -> assertEquals(3, moveKI.toX),
                () -> assertEquals(5, moveKI.toY)
        );


    }


    @Test
    void schlagÜberThronTes2t() {
        Board board = FenParser.parse(schlagÜberThron);
        board.printBoard();

        // Manuell den Zug ausführen und schauen was passiert:
        Board testBoard = board.copy();
        GameLogic.moveFigure(testBoard, 1, 5, 3, 5);
        testBoard.printBoard();
        System.out.println("blackWin: " + GameLogic.blackWin(testBoard));
        System.out.println("König pos: " + testBoard.kingPos[0] + "," + testBoard.kingPos[1]);
        System.out.println("(5,5) = " + testBoard.playingBoard[5][5]);
        System.out.println("(4,5) = " + testBoard.playingBoard[4][5]);

        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 1000000000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(1, moveKI.fromX),
                () -> assertEquals(5, moveKI.fromY),
                () -> assertEquals(3, moveKI.toX),
                () -> assertEquals(5, moveKI.toY)
        );
    }

    @Test
    void schlagÜberThronTes3t() {
        Board board = FenParser.parse(schlagÜberThron);
        board.printBoard();

        // Manuell den Zug ausführen und schauen was passiert:
        Board testBoard = board.copy();
        GameLogic.moveFigure(testBoard, 1, 5, 3, 5);
        testBoard.printBoard();
        System.out.println("blackWin: " + GameLogic.blackWin(testBoard));
        System.out.println("König pos: " + testBoard.kingPos[0] + "," + testBoard.kingPos[1]);
        System.out.println("(5,5) = " + testBoard.playingBoard[5][5]);
        System.out.println("(4,5) = " + testBoard.playingBoard[4][5]);


    }





}
