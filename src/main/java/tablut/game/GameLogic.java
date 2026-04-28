package tablut.game;

import tablut.board.Board;

public class GameLogic {

    //Macht ein nur den Zug
    //Prüfung ob Legal soll in MoveFactory passieren
    public static void moveFigure(Board board, int fromRow, int fromCol, int toRow, int toCol) {

        int figure = board.playingBoard[fromRow][fromCol];

        board.playingBoard[fromRow][fromRow] = Board.EMPTY;
        board.playingBoard[toRow][toCol] = figure;

        if (figure == Board.BLACK) {
            board.blackSoldersPos[fromRow][fromRow] = false;
            board.blackSoldersPos[toRow][toCol] = true;

        } else if (figure == Board.WHITE) {
            board.whiteSoldersPos[fromRow][fromRow] = false;
            board.whiteSoldersPos[toRow][toCol] = true;
        } else if (figure == Board.KING) {
            board.kingPos[0] = toRow;
            board.kingPos[1] = toCol;


        }


        //Schalagen Implementieren?

        //Zug wechseln
        board.playBlackTurn = !board.playBlackTurn;


    }


    public static void toCapture(Board board,int toRow, int toCol){}

    public static void whiteWin(Board board){}

    public static void blackWin(Board board){}


}
