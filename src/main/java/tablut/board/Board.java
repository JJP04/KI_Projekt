package tablut.board;

public class Board {

    //Constant
    public static final int EMPTY = 0;
    public static final int BORDER = 99;
    public static final int BLACK = 1;
    public static final int WHITE = -1;
    public static final int KING = -2;

    public Board() {
        createBorder();
        setUpBoard();

    }

    //Board inklusive "Rand"
    public final int[][] playingBoard = new int[11][11];

    public int[] kingPos = {5, 5};

    public final boolean[][] blackSoldersPos = new boolean[11][11];
    public final boolean[][] whiteSoldersPos = new boolean[11][11];

    //Schwarz Beginnt
    public boolean playBlackTurn = true;

    //Zähler Max 50 Züge?
    public int countMoves = 0;

    public void createBorder() {

        for (int i = 0; i < 11; i++) {
            playingBoard[0][i] = BORDER;
            playingBoard[10][i] = BORDER;
            playingBoard[i][0] = BORDER;
            playingBoard[i][10] = BORDER;


        }
    }

    public void setUpBoard() {
        int[][] blackStart = {{1, 4}, {1, 5}, {1, 6}, {2, 5}, {9, 4}, {9, 5}, {9, 6}, {8, 5}, {4, 1}, {5, 1}, {6, 1}, {5, 2}, {4, 9}, {5, 9}, {6, 9}, {5, 8}};
        int[][] whiteStart = {{3, 5}, {5, 3}, {5, 7}, {7, 5}, {4, 5}, {5, 4}, {5, 6}, {6, 5}, {4, 4}, {4, 6}, {6, 4}, {6, 6}};

        //Black
        for (int[] pos : blackStart) {
            playingBoard[pos[0]][pos[1]] = BLACK;
            blackSoldersPos[pos[0]][pos[1]] = true;
        }

        //White
        for (int[] pos : whiteStart) {
            playingBoard[pos[0]][pos[1]] = WHITE;
            whiteSoldersPos[pos[0]][pos[1]] = true;
        }

        // König auf Thron
        playingBoard[5][5] = KING;
    }

    //Prüft auf Corner für Win
    public static boolean isCorner(int r, int c) {
        return (r == 1 || r == 9) && (c == 1 || c == 9);
    }

    //Prüft auf Corner oder Thron für Züge
    public static boolean isRestricted(int r, int c) {
        return isCorner(r, c) || (r == 5 && c == 5);
    }


    //Für Print
    private String getFigur(int value) {
        return switch (value) {
            case EMPTY -> ".";
            case BORDER -> "#";
            case BLACK -> "B";
            case WHITE -> "W";
            case KING -> "K";
            default -> "?";
        };
    }

    //Print Board
    public void printBoard() {
        System.out.print("  ");
        for (int j = 0; j < 11; j++) {
            System.out.print(j + " ");
        }
        System.out.println();

        for (int i = 0; i < 11; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 11; j++) {
                System.out.print(getFigur(playingBoard[i][j]) + " ");
            }
            System.out.println();
        }
    }


    public Board copy() {
        //Optimiere Unnötige neu Belgen des Bordes
        Board b = new Board();

        for (int r = 0; r < playingBoard.length; r++) {
            //System.arraycopy(quelle, startQuelle, ziel, startZiel, anzahl);
            System.arraycopy(this.playingBoard[r], 0, b.playingBoard[r], 0, 11);
            System.arraycopy(this.blackSoldersPos[r], 0, b.blackSoldersPos[r], 0, 11);
            System.arraycopy(this.whiteSoldersPos[r], 0, b.whiteSoldersPos[r], 0, 11);
        }

        b.playBlackTurn = this.playBlackTurn;
        b.countMoves = this.countMoves;
        b.kingPos = this.kingPos.clone();

        return b;
    }


}
