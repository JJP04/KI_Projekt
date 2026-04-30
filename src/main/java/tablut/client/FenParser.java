package tablut.client;

import tablut.board.Board;

public class FenParser {

    public static Board parse(String fen) {
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


        String[] parts = fen.split(" ");

        String boardPart = parts[0];
        String turnPart = parts[1];
        String moveCount = parts[2];

        int row = 1;
        int col = 1;

        for (char c : boardPart.toCharArray()) {

            if (c == '/') {
                row++;
                col = 1;
                continue;
            }

            if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
                continue;
            }

            switch (c) {
                case 'r':
                    b.playingBoard[row][col] = Board.BLACK;
                    break;
                case 'R':
                    b.playingBoard[row][col] = Board.WHITE;
                    break;
                case 'K':
                    b.playingBoard[row][col] = Board.KING;
                    break;
                default:
                    break;
            }

            col++;
        }

        // 🔥 Spieler am Zug
        b.playBlackTurn = turnPart.equals("s"); // s = schwarz, w = weiß

        // optional
        b.countMoves = Integer.parseInt(moveCount);

        return b;
    }
}