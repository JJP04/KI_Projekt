package tablut.game;

import tablut.board.Board;
import tablut.board.Capture;
import tablut.board.Move;
import tablut.ki.ZobristHashing;

import java.util.Arrays;

public class GameLogic {

    //Schlag Methode
    public static void toCapture(Board board, Move move, int x, int y) {
        //schwarz
        if (board.playBlackTurn) {
            //Überprüft erst nach standard schlagen
            basicCapture(board, move, x, y, Board.BLACK, Board.BLACK, Board.WHITE); //Ob Bauer schlagen wird basic
            basicCapture(board, move, x, y, Board.BLACK, Board.BLACK, Board.KING); //Ob König geschlagen wird basic
            basicThroneCapture(board, move, x, y); //Ob Thron geschlagen wird Sonderfall
            toCaputureKing(board, move, x, y); //Ob König geschlagen wird Sonderfall
            //weiß
        } else {
            if (board.playingBoard[x][y] == Board.KING) {
                basicCapture(board, move, x, y, Board.KING, Board.WHITE, Board.BLACK); //Köing und andere seite Bauer
            } else {
                basicCapture(board, move, x, y, Board.WHITE, Board.WHITE, Board.BLACK); //Bauer und andere Seite Bauer
                basicCapture(board, move, x, y, Board.WHITE, Board.KING, Board.BLACK); //Bauer und andere Seite König
            }
        }
    }

    public static int countCaptures(Board board, Move move) {

        boolean blackturn = board.playBlackTurn;
        Move.makeMove(board, move);

        int captures = move.caputreCount;

        Move.unmakeMove(board, move);

        return captures;
    }

    //Klassisches schlagen
    public static void basicCapture(Board board, Move move, int x, int y, int ownFigure1, int ownFigure2, int opponentFigure) {
        for (int[] direction : Board.directions) {
            int[] field1 = moveXFields(x, y, direction, 1);
            int[] field2 = moveXFields(x, y, direction, 2);

            if (board.playingBoard[field1[0]][field1[1]] != Board.BORDER && board.playingBoard[field2[0]][field2[1]] != Board.BORDER) {

                if (board.playingBoard[x][y] == ownFigure1 && board.playingBoard[field1[0]][field1[1]] == opponentFigure) {
                    //Sonderfall König auf dem Thron oder angrenzendem Feld
                    if (opponentFigure == Board.KING && ((Board.throne[0] == board.kingPos[0] && Board.throne[1] == board.kingPos[1]) || isThroneNeighbor(board.kingPos[0], board.kingPos[1]))) {
                        continue;
                    }
                    //2 Feld ist eigene Figur oder 2 Feld ist Thron und leer oder  Feld ist Ecke dann wird geschlagen
                    if (board.playingBoard[field2[0]][field2[1]] == ownFigure2 || (Board.throne[0] == field2[0] && Board.throne[1] == field2[1] && board.playingBoard[5][5] == Board.EMPTY) || isCorner(field2[0], field2[1])) {
                        //Hash aktualisieren und Figur entfernen
                        board.hash = ZobristHashing.captureHash(board.hash, opponentFigure, field1[0], field1[1]);
                        //Geschlagene Figur speichern
                        move.capturedFigures[move.caputreCount] = new Capture(field1[0], field1[1], opponentFigure);
                        move.caputreCount++;
                        //Board aktualisieren
                        board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                    }
                }
            }
        }
    }


    public static void basicThroneCapture(Board board, Move move, int x, int y) {
        for (int[] direction : Board.directions) {
            int[] field1 = moveXFields(x, y, direction, 1);
            int[] field2 = moveXFields(x, y, direction, 2);

            if (board.playingBoard[field1[0]][field1[1]] != Board.BORDER && board.playingBoard[field2[0]][field2[1]] != Board.BORDER) {

                if ((Board.throne[0] == field2[0] && Board.throne[1] == field2[1]) && (Board.throne[0] == board.kingPos[0] && Board.throne[1] == board.kingPos[1])) {
                    int countBlack = 0;
                    int counterWhite = 0;
                    for (int[] t : Board.throneNeighbor) {
                        if (board.playingBoard[t[0]][t[1]] == Board.BLACK) {
                            countBlack++;
                        }
                        if (board.playingBoard[t[0]][t[1]] == Board.WHITE) {
                            counterWhite++;
                        }
                    }
                    if (countBlack == 3 && counterWhite == 1) {

                        board.hash = ZobristHashing.captureHash(board.hash, Board.WHITE, field1[0], field1[1]);

                        move.capturedFigures[move.caputreCount] = new Capture(field1[0], field1[1], Board.WHITE);
                        move.caputreCount++;

                        board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                    }
                }
            }
        }
    }

    //Methode für schlagen des Königs Sonderfälle
    public static void toCaputureKing(Board board, Move move, int x, int y) {
        boolean surrounded = true;
        //König auf Thron dann muss er von 4 schwarzen besetzt sein
        if (Board.throne[0] == board.kingPos[0] && Board.throne[1] == board.kingPos[1]) {
            for (int[] t : Board.throneNeighbor) {
                if (board.playingBoard[t[0]][t[1]] != Board.BLACK) {
                    surrounded = false;
                    break;
                }
            }
            if (surrounded) {
                board.hash = ZobristHashing.captureHash(board.hash, Board.KING, board.kingPos[0], board.kingPos[1]);
                move.capturedFigures[move.caputreCount] = new Capture(board.kingPos[0], board.kingPos[1], Board.KING);
                move.caputreCount++;
                board.playingBoard[board.kingPos[0]][board.kingPos[1]] = Board.EMPTY;
            }
        }
        //König auf einem Thron angrenzendem Feld dann reicht 3 schwarze besetzt
        for (int[] t : Board.throneNeighbor) {
            if (board.kingPos[0] == t[0] && board.kingPos[1] == t[1]) {
                int countBlack = 0;
                int[][] directions = Board.directions;
                for (int[] direction : directions) {
                    int[] field = moveXFields(board.kingPos[0], board.kingPos[1], direction, 1);
                    if (board.playingBoard[field[0]][field[1]] == Board.BLACK) {
                        countBlack++;
                    }
                }
                if (countBlack == 3) {
                    board.hash = ZobristHashing.captureHash(board.hash, Board.KING, board.kingPos[0], board.kingPos[1]);
                    move.capturedFigures[move.caputreCount] = new Capture(board.kingPos[0], board.kingPos[1], Board.KING);
                    move.caputreCount++;
                    board.playingBoard[board.kingPos[0]][board.kingPos[1]] = Board.EMPTY;
                }
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
        return board.playingBoard[9][9] == Board.KING;
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

    public static boolean isGameOver(Board board) {
        return whiteWin(board) || blackWin(board) || isTie(board);
    }


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

    //Überprüft ob das Zielfeld legal ist
    public static boolean islegalField(Board board, int nx, int ny, int x, int y) { //Julian

        //Zielfeld = Ecke
        if (!(board.playingBoard[x][y] == Board.KING) && ((nx == 1 && ny == 1) || (nx == 1 && ny == 9) || (nx == 9 && ny == 1) || (nx == 9 && ny == 9))) {
            return false;
        }
        //Zielfeld = Außerhalb des Spielfelds
        if (board.playingBoard[nx][ny] == Board.BORDER) {
            return false;
        }
        //feld ist besetzt
        return board.playingBoard[nx][ny] == Board.EMPTY;//Wenn keine der Sonderfälle und Feld frei, dann legal
    }

    public static boolean isThroneNeighbor(int x, int y) {
        for (int[] neighbor : Board.throneNeighbor) {
            if (neighbor[0] == x && neighbor[1] == y) {
                return true;
            }
        }
        return false;
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


