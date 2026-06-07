package tablut.game;

import tablut.board.Board;
import tablut.board.Move;
import tablut.ki.ZobristHashing;

import java.util.Arrays;

public class GameLogic {


    //Führt den Zug durch
    public static void moveFigure(Board board, int fromRow, int fromCol, int toRow, int toCol) {

        int figure = board.playingBoard[fromRow][fromCol];

        if (figure == Board.EMPTY || figure == Board.BORDER) {
            System.out.println("Keine Figur auf Feld");
            return;
        }
        board.playingBoard[fromRow][fromCol] = Board.EMPTY;
        board.playingBoard[toRow][toCol] = figure;

        if (figure == Board.BLACK) {
            board.blackSoldersPos[fromRow][fromCol] = false;
            board.blackSoldersPos[toRow][toCol] = true;
        } else if (figure == Board.WHITE) {
            board.whiteSoldersPos[fromRow][fromCol] = false;
            board.whiteSoldersPos[toRow][toCol] = true;
        } else if (figure == Board.KING) {
            board.kingPos[0] = toRow;
            board.kingPos[1] = toCol;
        }

        board.countMoves++;
        if (!board.isSearchCopy) {  // ← nur speichern wenn echtes Spiel
            savePosition(board);
        }

        //Schlagen Methode aufrufen
        toCapture(board, toRow, toCol);

        //Hash aktualisieren
        board.hash = ZobristHashing.updateHash(board.hash, board.playingBoard[toRow][toCol], fromRow, fromCol, toRow, toCol);

        //Zug wechseln
        board.playBlackTurn = !board.playBlackTurn;
    }


    //Methode für das Schlagen Prinzip Bauern: Kriegt einen Position und schaut ob dadurch eine Figur geschlagen wird
    public static void toCapture(Board board, int x, int y) {
        //SCHWARZ:
        if (board.playBlackTurn) {
            //Überprüft erst nach standard schlagen
            basicCapture(board, x, y, 1, 1, -1); //Ob Bauer schlagen wird basic
            basicCapture(board, x, y, 1, 1, -2); //Ob König geschlagen wird basic
            basicThroneCapture(board, x, y); //Ob Thron geschlagen wird Sonderfall
            toCaputureKing(board, x, y); //Ob König geschlagen wird Sonderfall
            //WEIß:
        } else {
            if (board.playingBoard[x][y] == board.KING) {
                basicCapture(board, x, y, -2, -1, 1); //Köing und andere seite Bauer
            } else {
                basicCapture(board, x, y, -1, -1, 1); //Bauer und andere Seite Bauer
                basicCapture(board, x, y, -1, -2, 1); //Bauer und andere Seite König
            }
        }
    }

    //Klassisches schlagen
    public static void basicCapture(Board board, int x, int y, int ownFigure1, int ownFigure2, int opponentFigure) {
        for (int[] direction : Board.directions) {
            int[] field1 = moveXFields(x, y, direction, 1);
            int[] field2 = moveXFields(x, y, direction, 2);
            if (board.playingBoard[field1[0]][field1[1]] != Board.BORDER
                    && board.playingBoard[field2[0]][field2[1]] != Board.BORDER) {
                if (board.playingBoard[x][y] == ownFigure1) {
                    if (board.playingBoard[field1[0]][field1[1]] == opponentFigure) {

                        // König auf Thron oder Thron-Nachbarfeld → nicht normal schlagbar
                        if (opponentFigure == Board.KING) {
                            int kx = board.kingPos[0];
                            int ky = board.kingPos[1];
                            if ((kx == 5 && ky == 5) ||
                                    (kx == 4 && ky == 5) ||
                                    (kx == 6 && ky == 5) ||
                                    (kx == 5 && ky == 4) ||
                                    (kx == 5 && ky == 6)) {
                                continue;
                            }
                        }

                        if (board.playingBoard[field2[0]][field2[1]] == ownFigure2
                                || Arrays.equals(Board.throne, field2) && board.playingBoard[5][5] == Board.EMPTY
                                || isCorner(field2[0], field2[1])) {
                            board.hash = ZobristHashing.captureHash(board.hash, opponentFigure, field1[0], field1[1]);
                            board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                        }
                    }
                }
            }
        }
    }

    //Schwarz: Sonderfall 18
    public static void basicThroneCapture(Board board, int x, int y) {
        //2 Felder weiter ist der Thron
        for (int[] direction : Board.directions) {
            int[] field1 = moveXFields(x, y, direction, 1);
            int[] field2 = moveXFields(x, y, direction, 2);
            //2 Felder weiter Thron und König auch drauf
            if (board.playingBoard[field1[0]][field1[1]] != Board.BORDER && board.playingBoard[field2[0]][field2[1]] != Board.BORDER) {
                if (board.playingBoard[field2[0]][field2[1]] == board.playingBoard[5][5] && board.playingBoard[5][5] == board.KING) {
                    int countBlack = 0;
                    int counterWhite = 0;
                    for (int[] t : Board.throneNeighbor) {
                        if (board.playingBoard[t[0]][t[1]] == board.BLACK) {
                            countBlack++;
                        }
                        if (board.playingBoard[t[0]][t[1]] == board.WHITE) {
                            counterWhite++;
                        }
                    }
                    if (countBlack == 3 && counterWhite == 1) {
                        board.hash = ZobristHashing.captureHash(board.hash, board.WHITE, field1[0], field1[1]);
                        board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                    }
                }
            }
        }
    }

    //Methode für schlagen des Königs:
    // Wird der König in allen anderen Fällen auch normal geschalen 
    public static void toCaputureKing(Board board, int x, int y) {
        //1. Thron durch 4 schwarze besetzt
        if (board.playingBoard[5][5] == board.KING && board.playingBoard[4][5] == board.BLACK && board.playingBoard[6][5] == board.BLACK && board.playingBoard[5][4] == board.BLACK && board.playingBoard[5][6] == board.BLACK) {
            board.hash = ZobristHashing.captureHash(board.hash, board.KING, x, y);
            board.playingBoard[5][5] = Board.EMPTY;
        }
        //2. König auf Thron angrenzendem Feld, dann reicht 3 schwarze besetzt
        if (board.playingBoard[4][5] == board.KING || board.playingBoard[6][5] == board.KING || board.playingBoard[5][4] == board.KING || board.playingBoard[5][6] == board.KING) {
            int countBlack = 0;
            int[][] directions = Board.directions;
            for (int[] direction : directions) {
                int[] field = moveXFields(board.kingPos[0], board.kingPos[1], direction, 1);
                if (board.playingBoard[field[0]][field[1]] == board.BLACK) {
                    countBlack++;
                }
            }
            if (countBlack == 3) {
                board.hash = ZobristHashing.captureHash(board.hash, board.KING,x,y);
                board.playingBoard[board.kingPos[0]][board.kingPos[1]] = Board.EMPTY;
            }
        }
    }

    public static int[] moveXFields(int x, int y, int[] direction, int steps) {
        int nx = x + direction[0] * steps;
        int ny = y + direction[1] * steps;
        return new int[]{nx, ny};
    }

    public static boolean whiteWin(Board board) {
        if (board.playingBoard[1][1] == Board.KING) return true;
        if (board.playingBoard[1][9] == Board.KING) return true;
        if (board.playingBoard[9][1] == Board.KING) return true;
        if (board.playingBoard[9][9] == Board.KING) return true;
        return false;
    }

    public static boolean blackWin(Board board) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (board.playingBoard[i][j] == Board.KING) {
                    return false;
                }
            }
        }
        return true;
    }

    //Prüft ob das Spiel Vorbei ist
    public static boolean isGameOver(Board board) {
        return whiteWin(board) || blackWin(board) || isTie(board);
    }

    // 1. Wenn sich eine Stellung wiederholt -->ToDo
// 2. Wenn ein Spieler keine Züge mehr ausführen kann --> ToDo
// 3. Wenn 50 Züge lang keine Figur geschlagen wurde
    public static boolean isTie(Board board) {
        if (board.countMoves >= 100) {
            return true;
        }
        int count = 0;
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                count++;
                if (count >= 2) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void gameEnd(Board board) {
        System.out.println("Spielende nach " + board.countMoves + " Zügen");
        if (whiteWin(board)) System.out.println("Weiß gewinnt!");
        else if (blackWin(board)) System.out.println("Schwarz gewinnt!");
        else System.out.println("Unentschieden!");
    }

    public static void savePosition(Board board) {
        board.boardHistory.add(board.copy().playingBoard);
    }

    //Überprüft, ob das Zielfeld legal ist
    public static boolean islegalField(Board board, int nx, int ny, int x, int y) { //Julian

        //Zielfeld = Ecke
        if (!(board.playingBoard[x][y] == board.KING) && ((nx == 1 && ny == 1) || (nx == 1 && ny == 9) || (nx == 9 && ny == 1) || (nx == 9 && ny == 9))) {
            return false;
        }
        //Zielfeld = Außerhalb des Spielfelds
        if (board.playingBoard[nx][ny] == board.BORDER) {
            return false;
        }
        //Feld ist besetzt
        if (!(board.playingBoard[nx][ny] == board.EMPTY)) {
            return false;
        }
        return true; //Wenn keine der Sonderfälle und Feld frei, dann legal
    }

    //Prüft ob das Feld der Thron ist
    public static boolean isKingTower(int x, int y) {
        return (x == 5 && y == 5);
    }

    public static boolean isCorner(int x, int y) {
        for (int[] corner : Board.corners) {
            if (corner[0] == x && corner[1] == y) {
                return true;
            }
        }
        return false;
    }
}


