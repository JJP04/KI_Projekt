package tablut.tests;

import org.junit.jupiter.api.Test;
import tablut.board.Board;
import static org.junit.jupiter.api.Assertions.*;


class BoardTest {

    @Test
void kingIsOnThrone() {
    Board board = new Board();
    board.setUpBoard();

    assertEquals(Board.KING, board.playingBoard[5][5]);
}
    @Test
    void testBoardSize() {
        Board board = new Board();
        board.setUpBoard();
        // mit Border
        assertEquals(11, board.playingBoard.length);
        assertEquals(11, board.playingBoard[0].length);
    }
    @Test
    void testCornersAreEmptyOrBlocked() {
        Board board = new Board();
        board.setUpBoard();

        // Ecken dürfen nicht besetzt sein
        assertEquals(Board.EMPTY, board.playingBoard[1][1]);
        assertEquals(Board.EMPTY, board.playingBoard[1][9]);
        assertEquals(Board.EMPTY, board.playingBoard[9][1]);
        assertEquals(Board.EMPTY, board.playingBoard[9][9]);
    }
  
    @Test
    void testBlackPiecesAre16() {
        Board board = new Board();
        board.setUpBoard();

        int foundBlack = 0;

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (board.playingBoard[i][j] == Board.BLACK) {
                    foundBlack++;
                }
            }
        }

        assertEquals(16, foundBlack);
    }
    @Test
    void testWhitePiecesAre8() {
        Board board = new Board();
        board.setUpBoard();

        int foundWhite = 0;

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (board.playingBoard[i][j] == Board.WHITE) {
                    foundWhite++;
                }
            }
        }

        assertEquals(8, foundWhite);
    }
    @Test
    void testThroneNotOccupiedByOtherPiece() {
        Board board = new Board();
        board.setUpBoard();

        // Thron muss exakt König sein
        assertNotEquals(Board.BLACK, board.playingBoard[5][5]);
        assertNotEquals(Board.WHITE, board.playingBoard[5][5]);
    }

     // echte Ecken
    @Test
    void testIsCorner_TrueCases() {
        Board b = new Board();
        b.setUpBoard();
        assertTrue(b.isCorner(1, 1));
        assertTrue(b.isCorner(1, 9));
        assertTrue(b.isCorner(9, 1));
        assertTrue(b.isCorner(9, 9));
    }

    // keine Ecken
    @Test
    void testIsCorner_FalseCases() {
        Board b = new Board();
        b.setUpBoard();
        assertFalse(b.isCorner(5, 5));
        assertFalse(b.isCorner(1, 5)); 
        assertFalse(b.isCorner(5, 1)); 
        assertFalse(b.isCorner(4, 4)); 
    }
        // ✅ Corner + Thron
    @Test
    void testIsRestricted_TrueCases() {
        Board b = new Board();
        b.setUpBoard();
        assertTrue(b.isRestricted(1, 1));
        assertTrue(b.isRestricted(1, 9));
        assertTrue(b.isRestricted(9, 1));
        assertTrue(b.isRestricted(9, 9));

        // Thron
        assertTrue(b.isRestricted(5, 5));
    }

    // ❌ normale Felder
    @Test
    void testIsRestricted_FalseCases() {
        Board b = new Board();
        b.setUpBoard();
        assertFalse(b.isRestricted(5, 4));
        assertFalse(b.isRestricted(4, 5));
        assertFalse(b.isRestricted(6, 6));
        assertFalse(b.isRestricted(2, 2));
    }

}
