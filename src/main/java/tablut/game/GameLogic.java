package tablut.game;

import tablut.board.Board;
import tablut.board.Move;

public class GameLogic {

    //Führt den Zug durch
    public static void moveFigure(Board board, int fromRow, int fromCol, int toRow, int toCol) {

        if (!islegalField(board, toRow, toCol)) {
            //Print Später weg Jetzt für Debugging
            System.out.println("Illegaler Zug");
            return;
        }

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

        //Schalagen Implementieren!!!
        //Zug wechseln
        board.playBlackTurn = !board.playBlackTurn;
    }

    //Methode für das Schlagen Prinzip Bauern: Kriegt einen Position und schaut ob dadurch eine Figur geschlagen wird
    public static void toCapture(Board board, int x, int y) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        //Jede Richtung nacheinander überprüfen
        for (int[] direction : directions) {
            //Überprüft eine Richtung (benötigt dafür nächsten 3 Felder)
            int[] field1 = moveXFields(x, y, direction, 1);
            int[] field2 = moveXFields(x, y, direction, 2);
            int[] field3 = moveXFields(x, y, direction, 3);

            //schawrz am Zug und aktuelle Figur schwarz
            if (board.playBlackTurn && board.playingBoard[x][y] == 1) {
                //nächstes Feld ist weiß
                if (board.playingBoard[field1[0]][field1[1]] == -1 || board.playingBoard[field1[0]][field1[1]] == board.KING) {
                    //2 Felder weiter auch weiß
                    if (board.playingBoard[field2[0]][field2[1]] == -1 || board.playingBoard[field2[0]][field2[1]] == board.KING) {
                        //3 Felder weiter ist schwarz --> schlagen
                        if (board.playingBoard[field3[0]][field3[1]] == 1) {
                            board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                            board.playingBoard[field2[0]][field2[1]] = Board.EMPTY;
                        }
                    }
                    //2 Felder weiter ist schwarz oder BOARDER --> schlagen (Oder Ecke, muss noch implementiert werden)
                    if (board.playingBoard[field2[0]][field2[1]] == 1 || board.playingBoard[field2[0]][field2[1]] == Board.BORDER) {
                        board.playingBoard[field1[0]][field1[1]] = Board.EMPTY;
                    }
                    //2 Felder weiter ist der Thron
                    if(board.playingBoard[field2[0]][field2[1]] == board.playingBoard[5][5] && board.playingBoard[x][y] == board.KING){
                        int countBlack = 0;
                        int counterWhite = 1;
                        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for (int[] direction : directions) {
                            int[] field = moveXFields(x, y, direction, 1);
                            if (board.playingBoard[field[0]][field[1]] == board.BLACK) {
                                countBlack++;
                            }
                            if (board.playingBoard[field[0]][field[1]] == board.WHITE) {
                                counterWhite++;
                            }
                        }
                        if (countBlack == 3 && counterWhite == 1) {
                            board.playingBoard[x][y] = Board.EMPTY;
                        }
                    }
                }
            } else {
                break; //nächstes Feld ist schwarz oder leer --> passiert nichts
            }
        }
    }

    //Methode für schlagen des Königs:
    public static void toCaputreKing(Board board, int x, int y) {
        //1. Thron durch 4 schwarze besetzt
        if (board.playingBoard[5][5] == board.KING && board.playingBoard[4][5] == board.BLACK && board.playingBoard[6][5] == board.BLACK && board.playingBoard[5][4] == board.BLACK && board.playingBoard[5][6] == board.BLACK) {
            board.playingBoard[5][5] = Board.EMPTY;
        }
        //2. König auf Thron angrenzendem Feld, dann reicht 3 schwarze besetzt
        if (board.playingBoard[x][y] == board.KING && ((x == 4 && y == 5) || (x == 6 && y == 5) || (x == 5 && y == 4) || (x == 5 && y == 6))) {
            int countBlack = 0;
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] direction : directions) {
                int[] field = moveXFields(x, y, direction, 1);
                if (board.playingBoard[field[0]][field[1]] == board.BLACK) {
                    countBlack++;
                }
            }
            if (countBlack >= 3) {
                board.playingBoard[x][y] = Board.EMPTY;
            }
        }

        //3. alle andere Felder normal --> in toCaputre einbauen?;
    }

    //Sonderregel Schlagen, wenn König von 3 weiß umzingelt ist
    public static void toCaptureSpecial(Board board, int x, int y) {
        if (board.playingBoard[5][5] == board.KING) {
            int countBlack = 0;
            int counterWhite = 1;
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] t : thronFields()) {
                if (board.playingBoard[t[0]][t[1]] == board.BLACK) {
                    countBlack++;
                }
                if (board.playingBoard[t[0]][t[1]] == board.WHITE) {
                    counterWhite++;
                }
            }
            if (countBlack == 3 && counterWhite == 1) {
                board.playingBoard[x][y] = Board.EMPTY;
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

    //Wenn nirgendwo mehr ein König ist?
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

    // 1. Wenn sich eine Stellung wiederholt -->ToDo
// 2. Wenn ein Spieler keine Züge mehr ausführen kann --> ToDo
// 3. Wenn 50 Züge lang keine Figur geschlagen wurde
    public static boolean isTie(Board board) { //Berno
        if (board.countMoves >= 100) return true; //100halbezüge + 50 ganze

        return false;
    }

    //Überprüft, ob das Zielfeld legal ist
    public static boolean islegalField(Board board, int x, int y) { //Julian

        //Zielfeld = Ecke
        if (!(board.playingBoard[x][y] == board.KING) && ((x == 1 && y == 1) || (x == 1 && y == 9) || (x == 9 && y == 1) || (x == 9 && y == 9))) {
            return false;
        }
        //Zielfeld = Außerhalb des Spielfelds
        if (board.playingBoard[x][y] == board.BORDER) {
            return false;
        }
        //Feld ist besetzt
        if (!(board.playingBoard[x][y] == board.EMPTY)) {
            return false;
        }
        return true; //Wenn keine der Sonderfälle und Feld frei, dann legal
    }

    //Prüft ob das Spiel Vorbei ist
    public static boolean isGameOver(Board board) {
        return whiteWin(board) || blackWin(board) || isTie(board);
    }

    //Prüft ob das Feld der Thron ist
    public static boolean isKingTower(int x, int y) {
        return (x == 5 && y == 5);
    }

    //Gibt alle Eckfelder zurück (eventuell noch benötigt)
    public static int[][] conrnerFields() {
        return new int[][]{{1, 1}, {1, 9}, {9, 1}, {9, 9}};
    }

    public static int[][] thronFields() {
        return new int[][]{{4, 5}, {6, 5}, {5, 4}, {5, 6}};
    }

}


