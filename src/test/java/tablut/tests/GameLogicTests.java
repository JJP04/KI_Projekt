package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import tablut.game.GameLogic;

import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTests {

    private Board createBoard() {
        Board b = new Board();
        b.setUpBoard();
        return b;
    }

    private Board emptyBoard() {
        return new Board();
    }

    @Test
    void testMoveFigureValidMove() {
        Board b = createBoard();

        int fromX = 4;
        int fromY = 1;
        int toX = 4;
        int toY = 2;

        GameLogic.moveFigure(b, fromX, fromY, toX, toY);

        assertEquals(Board.EMPTY, b.playingBoard[fromX][fromY]);
        assertEquals(Board.BLACK, b.playingBoard[toX][toY]);
    }

//   @Test
//   void testIllegalCornerMove() {
//      Board b = createBoard();

//      GameLogic.moveFigure(b, 4, 1, 1, 1); // Ecke

//     assertEquals(Board.BLACK, b.playingBoard[4][1]);
// }

    @Test
    void testMoveFromEmptyField() {
        Board b = createBoard();

        GameLogic.moveFigure(b, 1, 2, 3, 2);

        assertEquals(Board.EMPTY, b.playingBoard[1][2]);
        assertEquals(Board.EMPTY, b.playingBoard[3][2]);
    }

    @Test
    void testMoveFromBorderField() {
        Board b = createBoard();

        GameLogic.moveFigure(b, 0, 1, 3, 1);

        assertEquals(Board.BORDER, b.playingBoard[0][1]);
        assertEquals(Board.EMPTY, b.playingBoard[3][1]);
    }

    @Test
    void testMoveBlackPiece() {
        Board b = createBoard();

        GameLogic.moveFigure(b, 1, 4, 4, 4);

        assertEquals(Board.EMPTY, b.playingBoard[1][4]);
        assertEquals(Board.BLACK, b.playingBoard[4][4]);
    }

    @Test
    void testMoveWhitePiece() {
        Board b = createBoard();

        GameLogic.moveFigure(b, 3, 5, 3, 7);

        assertEquals(Board.EMPTY, b.playingBoard[3][5]);
        assertEquals(Board.WHITE, b.playingBoard[3][7]);
    }

    @Test
    void testMoveKing() {
        Board b = createBoard();

        GameLogic.moveFigure(b, 5, 7, 3, 7);
        GameLogic.moveFigure(b, 5, 5, 5, 7);

        assertEquals(Board.EMPTY, b.playingBoard[5][5]);
        assertEquals(Board.KING, b.playingBoard[5][7]);
    }

    @Test
    void testWhiteWinCorner1() {
        Board b = createBoard();

        b.playingBoard[1][1] = Board.KING;

        assertTrue(GameLogic.whiteWin(b));
    }

    @Test
    void testWhiteWinCorner2() {
        Board b = createBoard();

        b.playingBoard[1][9] = Board.KING;

        assertTrue(GameLogic.whiteWin(b));
    }

    @Test
    void testWhiteWinCorner3() {
        Board b = createBoard();

        b.playingBoard[9][1] = Board.KING;

        assertTrue(GameLogic.whiteWin(b));
    }

    @Test
    void testWhiteWinCorner4() {
        Board b = createBoard();

        b.playingBoard[9][9] = Board.KING;

        assertTrue(GameLogic.whiteWin(b));
    }

    @Test
    void testWhiteWinCorner5() {
        Board b = createBoard();
        assertTrue(!GameLogic.whiteWin(b));
    }

    @Test
    void testBlackWinNoKing() {
        Board b = createBoard();
        b.playingBoard[5][5] = Board.EMPTY;
        assertTrue(GameLogic.blackWin(b));
    }

    @Test
    void testBlackWin() {
        Board b = createBoard();
        assertTrue(!GameLogic.blackWin(b));
    }

    @Test
    void testTieByMoveCount() {
        Board b = createBoard();

        b.countMoves = 100;

        assertTrue(GameLogic.isTie(b));
    }

    @Test
    void testTieB() {
        Board b = createBoard();

        b.countMoves = 67;

        assertTrue(!GameLogic.isTie(b));
    }

    @Test
    void testKingTower() {
        assertTrue(GameLogic.isKingTower(5, 5));
        assertFalse(GameLogic.isKingTower(4, 5));
    }

    @Test
    void testIllegalFieldCorner() {
        Board b = createBoard();

        boolean result = GameLogic.islegalField(b, 1, 1, 1,2);

        assertFalse(result);
    }

    @Test
    void testKingCapturedOnThrone() {
        Board b = emptyBoard();

        b.playingBoard[5][5] = Board.KING;
        b.kingPos[0] = 5;
        b.kingPos[1] = 5;

        b.playingBoard[4][5] = Board.BLACK;
        b.playingBoard[5][4] = Board.BLACK;
        b.playingBoard[6][5] = Board.BLACK;
        b.playingBoard[5][6] = Board.WHITE;
        b.playingBoard[5][7] = Board.BLACK;

        int x = 5;
        int y = 7;

        GameLogic.basicThroneCapture(b, x, y);

        assertEquals(Board.EMPTY, b.playingBoard[5][6]);
    }

    @Test
    void testKingNotCapturedTooFewBlack() {
        Board b = emptyBoard();

        b.playingBoard[5][5] = Board.KING;
        b.playingBoard[4][5] = Board.BLACK;
        b.playingBoard[6][5] = Board.BLACK;
        b.playingBoard[5][4] = Board.WHITE;
        b.playingBoard[5][6] = Board.WHITE;

        int x = 4;
        int y = 5;

        GameLogic.basicThroneCapture(b, x, y);

        assertEquals(Board.BLACK, b.playingBoard[x][y]);
    }

    @Test
    void testNoCaptureIfKingNotOnThrone() {
        Board b = emptyBoard();

        b.playingBoard[4][5] = Board.KING;

        b.playingBoard[3][5] = Board.BLACK;
        b.playingBoard[5][5] = Board.BLACK;
        b.playingBoard[4][4] = Board.BLACK;
        b.playingBoard[4][6] = Board.WHITE;

        int x = 3;
        int y = 5;

        GameLogic.basicThroneCapture(b, x, y);

        assertEquals(Board.BLACK, b.playingBoard[x][y]);
    }

    @Test
    void testBasicCaptureNormal() {
        Board b = emptyBoard();

        b.playingBoard[5][8] = Board.BLACK;

        b.playingBoard[5][7] = Board.WHITE;
        b.playingBoard[5][6] = Board.BLACK;

        GameLogic.basicCapture(b, 5, 8, Board.BLACK, Board.BLACK, Board.WHITE);

        assertEquals(Board.EMPTY, b.playingBoard[5][7]);
    }

    @Test
    void testNoCaptureWithoutSecondPiece() {
        Board b = emptyBoard();

        b.playingBoard[5][5] = Board.BLACK;
        b.playingBoard[5][6] = Board.WHITE;
        // kein zweiter BLACK dahinter

        GameLogic.basicCapture(b, 5, 5, Board.BLACK, Board.BLACK, Board.WHITE);

        assertEquals(Board.WHITE, b.playingBoard[5][6]);
    }

    @Test
    void testCaptureAgainstBorder() {
        Board b = emptyBoard();

        b.playingBoard[1][8] = Board.BLACK;
        b.playingBoard[1][9] = Board.WHITE;
        // dahinter ist BORDER (Rand)
        GameLogic.basicCapture(b, 1, 5, Board.BLACK, Board.BLACK, Board.WHITE);

        assertEquals(Board.WHITE, b.playingBoard[1][9]);
    }

    @Test
    void testMultipleDirectionCapture() {
        Board b = emptyBoard();

        // Zentrum
        b.playingBoard[5][5] = Board.BLACK;

        // Gegner oben/unten
        b.playingBoard[4][5] = Board.WHITE;
        b.playingBoard[6][5] = Board.WHITE;

        // eigene Figuren
        b.playingBoard[3][5] = Board.BLACK;
        b.playingBoard[7][5] = Board.BLACK;

        GameLogic.basicCapture(b, 5, 5, Board.BLACK, Board.BLACK, Board.WHITE);

        assertEquals(Board.EMPTY, b.playingBoard[4][5]);
        assertEquals(Board.EMPTY, b.playingBoard[6][5]);
    }

    @Test
    void testCaptureAgainstThrone() {
        Board b = emptyBoard();

        b.playingBoard[5][7] = Board.BLACK;
        b.playingBoard[5][5] = Board.EMPTY; // Thron bleibt „leer“
        b.playingBoard[5][6] = Board.WHITE;

        GameLogic.basicCapture(b, 5, 7, Board.BLACK, Board.BLACK, Board.WHITE);

        assertEquals(Board.EMPTY, b.playingBoard[5][6]);
    }

    @Test
    void testKingCapturedOnThroneWithFourBlack() {
        Board b = emptyBoard();

        b.playingBoard[5][5] = Board.KING;

        b.playingBoard[4][5] = Board.BLACK;
        b.playingBoard[6][5] = Board.BLACK;
        b.playingBoard[5][4] = Board.BLACK;
        b.playingBoard[5][6] = Board.BLACK;

        GameLogic.toCaputureKing(b, 5, 5);

        assertEquals(Board.EMPTY, b.playingBoard[5][5]);
    }


    @Test
    void testKingNotCapturedOnThroneWithThreeBlack() {
        Board b = emptyBoard();

        b.playingBoard[5][5] = Board.KING;

        b.playingBoard[4][5] = Board.BLACK;
        b.playingBoard[6][5] = Board.BLACK;
        b.playingBoard[5][4] = Board.BLACK;
        // rechts fehlt

        GameLogic.toCaputureKing(b, 5, 5);

        assertEquals(Board.KING, b.playingBoard[5][5]);
    }

    @Test
    void testKingCapturedAdjacentToThrone() {
        Board b = emptyBoard();

        // König neben Thron
        b.playingBoard[4][5] = Board.KING;
        b.kingPos[0] = 4;
        b.kingPos[1] = 5;

        // 3 schwarze um ihn herum
        b.playingBoard[3][5] = Board.BLACK;
        b.playingBoard[4][4] = Board.BLACK;
        b.playingBoard[4][6] = Board.BLACK;

        GameLogic.toCaputureKing(b, 3, 5);

        assertEquals(Board.EMPTY, b.playingBoard[4][5]);
    }

    @Test
    void testKingNotCapturedAdjacentTooFewBlack() {
        Board b = emptyBoard();

        b.playingBoard[4][5] = Board.KING;

        b.playingBoard[3][5] = Board.BLACK;
        b.playingBoard[4][4] = Board.BLACK;
        // einer fehlt

        GameLogic.toCaputureKing(b, 4, 5);

        assertEquals(Board.KING, b.playingBoard[4][5]);
    }

// @Test
// void testKingNotNearThroneNoSpecialCapture() {
//     Board b = emptyBoard();

//     b.playingBoard[7][7] = Board.KING;

//     b.playingBoard[6][7] = Board.BLACK;
//     b.playingBoard[8][7] = Board.BLACK;
//     b.playingBoard[7][6] = Board.BLACK;
//     b.playingBoard[7][8] = Board.BLACK;

//     GameLogic.toCaputureKing(b, 7, 7);

//     assertEquals(Board.KING, b.playingBoard[7][7]);
// }

    @Test
    void testCornerIsIllegal() {
        Board b = emptyBoard();
        boolean result = GameLogic.islegalField(b, 1, 1,1,2);
        assertFalse(result);
    }

// @Test
// void testCornerAllowedForKing() {
//     Board b = emptyBoard();
//     b.playingBoard[1][1] = Board.KING;
//     boolean result = GameLogic.islegalField(b, 1, 1);
//     assertTrue(result);
// }

    @Test
    void testBorderIsIllegal() {
        Board b = emptyBoard();
        boolean result = GameLogic.islegalField(b, 0, 5,0,0); // Border
        assertFalse(result);
    }

    @Test
    void testOccupiedFieldIsIllegal() {
        Board b = emptyBoard();

        b.playingBoard[4][4] = Board.BLACK;

        boolean result = GameLogic.islegalField(b, 4, 4,0,0);

        assertFalse(result);
    }

    @Test
    void testEmptyFieldIsLegal() {
        Board b = emptyBoard();

        boolean result = GameLogic.islegalField(b, 4, 4,0,0);

        assertTrue(result);
    }

}
