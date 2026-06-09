package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import tablut.board.Move;
import tablut.client.FenParser;
import tablut.game.Bewertungsfunktion;
import tablut.ki.SearchMoves;

import static org.junit.jupiter.api.Assertions.*;

public class BewertungsfunktionTests {


    String whiteWins1 = "4rr3/4r4/5R3/r4r3/rr1r2Rrr/r3R3r/2R2K1R1/4r4/4r4 w 0 12";
    String blackWins1 = "9/9/4r4/9/3rKr3/4r4/9/9/9 s 0 1";
    String blackWins2 ="9/1r7/3r1r3/3rKr3/9/2R3R2/9/4r4/9 s 0 1";
    String start ="3rrr3/4r4/4R4/r3R3r/rrRRKRRrr/r3R3r/4R4/4r4/3rrr3 s 0 1";

    @Test
    void whiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(7, moveKI.fromX),
                () -> assertEquals(6, moveKI.fromY),
                () -> assertEquals(9, moveKI.toX),
                () -> assertEquals(6, moveKI.toY)
        );
    }
    @Test
    void blackWins2() {
        Board board = FenParser.parse(blackWins2);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY);
        assertAll(
                () -> assertEquals(3, moveKI.fromX),
                () -> assertEquals(4, moveKI.fromY),
                () -> assertEquals(3, moveKI.toX),
                () -> assertEquals(5, moveKI.toY)
        );
    }

    @Test
    void ratePositionWhiteWins() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.ratePosition(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );

        assertEquals(new Move(7, 6, 9, 6), moveKI);
    }

    @Test
    void ratePositionBlackWins() {
        Board board = FenParser.parse(blackWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.ratePosition(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-1102, a);
    }
    @Test
    void ratePositionBlackWins2() {
        Board board = FenParser.parse(blackWins2);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.ratePosition(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertAll(
                () -> assertEquals(3, moveKI.fromX),
                () -> assertEquals(4, moveKI.fromY),
                () -> assertEquals(3, moveKI.toX),
                () -> assertEquals(5, moveKI.toY)
        );
    }
    @Test
    void ratePositionStart() {
        Board board = FenParser.parse(start);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.ratePosition(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-808, a);
    }
    @Test
    void materialStart() {
        Board board = FenParser.parse(start);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.material(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-8, a);
    }
    @Test
    void materialWhiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.material(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-17, a);
    }
    @Test
    void distanceCornerStart() {
        Board board = FenParser.parse(start);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.distanceCorner(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-800, a);
    }
    @Test
    void distanceCornerWhiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.distanceCorner(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-500, a);
    }
    @Test
    void pressureKingStart() {
        Board board = FenParser.parse(start);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.pressureKing(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(0, a);
    }
    @Test
    void pressureKingBlackWins1() {
        Board board = FenParser.parse(blackWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.pressureKing(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(-300, a);
    }
    @Test
    void escapeKingStart() {
        Board board = FenParser.parse(start);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.escapeKing(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(0, a);
    }
    @Test
    void escapeKingBlackWins1() {
        Board board = FenParser.parse(blackWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.escapeKing(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(10, a);
    }
    @Test
    void escapeKingWhiteWins1() {
        Board board = FenParser.parse(whiteWins1);
        board.printBoard();
        Move moveKI = SearchMoves.findBestMoveAlphaBeta(board, 120000);
        int a = Bewertungsfunktion.escapeKing(board);
        System.out.println(a);
        System.out.println("KI Zug: " + moveKI.fromX + "," + moveKI.fromY + "--> " + moveKI.toX + "," + moveKI.toY );
        assertEquals(70, a);
    }

}
