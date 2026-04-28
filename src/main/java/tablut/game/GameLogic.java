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

    public static boolean whiteWin(Board board){
        if(board.playingBoard[1][1]==Board.KING)return true;
        if(board.playingBoard[1][9]==Board.KING)return true;
        if(board.playingBoard[9][1]==Board.KING)return true;
        if(board.playingBoard[9][9]==Board.KING)return true;
        return false;
    }

    public static boolean blackWin(Board board){
        for(int i=0;i<11;i++){
            for(int j=0; j<11;i++){
             if(board.playingBoard[i][j]==Board.KING){
               return false;
                }   
            } 
        }
        return true;
    }
        
    // 1. Wenn sich eine Stellung wiederholt -->ToDo
    // 2. Wenn ein Spieler keine Züge mehr ausführen kann --> ToDo
    // 3. Wenn 50 Züge lang keine Figur geschlagen wurde
    public static boolean isTie(Board board){
        if(board.countMoves==50)return true;
    
        return false;
    }
}
