package tablut.Evolution;

import tablut.board.Board;
import tablut.game.GameLogic;

import java.util.Arrays;

public class BewertungsfunktionEvolution {

    // Konfigurierbare Parameter der Bewertungsfunktion
    private int winScore = 10000;
    private int twoOpenLinesScore = 8000;
    private int oneOpenLineWhiteTurnScore = 5000;
    private int oneOpenLineScore = 300;
    private int escapeKingWeight = 5;
    private int pressureWeight = -15;
    private int cornerReachedScore = 10000;
    private int distanceCornerWeight = -10;
    private int cornerBlockadeWeight = -25;
    private int materialWhiteWeight = 100;
    private int materialBlackWeight = 50;
    private int repetitionPenalty = -50;


    //Standard Konsrutkro
    public BewertungsfunktionEvolution() {
    }

    //Konsturktor mit Parametern
    public BewertungsfunktionEvolution(int winScore, int twoOpenLinesScore, int oneOpenLineWhiteTurnScore, int oneOpenLineScore, int escapeKingWeight, int pressureWeight, int cornerReachedScore, int distanceCornerWeight, int cornerBlockadeWeight, int materialWhiteWeight, int materialBlackWeight, int repetitionPenalty) {
        this.winScore = winScore;
        this.twoOpenLinesScore = twoOpenLinesScore;
        this.oneOpenLineWhiteTurnScore = oneOpenLineWhiteTurnScore;
        this.oneOpenLineScore = oneOpenLineScore;
        this.escapeKingWeight = escapeKingWeight;
        this.pressureWeight = pressureWeight;
        this.cornerReachedScore = cornerReachedScore;
        this.distanceCornerWeight = distanceCornerWeight;
        this.cornerBlockadeWeight = cornerBlockadeWeight;
        this.materialWhiteWeight = materialWhiteWeight;
        this.materialBlackWeight = materialBlackWeight;
        this.repetitionPenalty = repetitionPenalty;
    }


    public int ratePosition(Board board) {
        return ratePosition(board, 0);
    }

    public int ratePosition(Board board, int ply) {
        int win = winStatus(board, ply);

        if (win != 0) return win;


        int openLines = countOpenEscapeLines(board);
        if (openLines >= 2) return twoOpenLinesScore - ply;
        if (openLines == 1 && !board.playBlackTurn) {
            return oneOpenLineWhiteTurnScore - ply;
        }


        int score = escapeKing(board) + pressureKing(board) + distanceCorner(board) + material(board) + cornerBlockade(board) + checkBoardRepetition(board);

        if (openLines == 1) score += oneOpenLineScore;
        return score;
    }

    public int winStatus(Board board, int ply) {
        if (GameLogic.whiteWin(board)) return winScore - ply;

        if (board.playingBoard[board.kingPos[0]][board.kingPos[1]] != Board.KING) return -(winScore - ply);
        return 0;
    }

    public int escapeKing(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];
        int moves = 0;

        for (int[] dir : Board.directions) {
            int nx = kx + dir[0];
            int ny = ky + dir[1];
            while (board.playingBoard[nx][ny] == Board.EMPTY) {

                if (!(nx == Board.throne[0] && ny == Board.throne[1])) {
                    moves++;
                }
                nx += dir[0];
                ny += dir[1];
            }
        }
        return moves * escapeKingWeight;
    }

    public int pressureKing(Board board) {
        int pressure = 0;
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        for (int[] dir : Board.directions) {
            int nx = kx + dir[0];
            int ny = ky + dir[1];
            int distance = 1;

            while (board.playingBoard[nx][ny] != Board.BORDER) {
                if (board.playingBoard[nx][ny] == Board.BLACK) {
                    if (distance <= 3) {
                        pressure += (4 - distance);
                    }
                    break;
                }
                if (board.playingBoard[nx][ny] == Board.WHITE) {
                    break;
                }
                nx += dir[0];
                ny += dir[1];
                distance++;
            }
        }
        return pressure * pressureWeight;
    }

    public int distanceCorner(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];

        int bestDist = Integer.MAX_VALUE;

        for (int[] corner : Board.corners) {
            int dist = Math.abs(kx - corner[0]) + Math.abs(ky - corner[1]);
            bestDist = Math.min(bestDist, dist);
        }
        if (bestDist == 0) return cornerReachedScore;

        return bestDist * distanceCornerWeight;
    }

    public int countOpenEscapeLines(Board board) {
        int kx = board.kingPos[0];
        int ky = board.kingPos[1];
        int open = 0;

        for (int[] corner : Board.corners) {
            int cx = corner[0];
            int cy = corner[1];

            if (kx == cx && ky == cy) continue;

            if (kx == cx) {

                if (isLineFree(board, kx, ky, cy, false)) open++;
            } else if (ky == cy) {

                if (isLineFree(board, kx, ky, cx, true)) open++;
            }
        }
        return open;
    }


    private boolean isLineFree(Board board, int kx, int ky, int target, boolean scanColumn) {
        if (scanColumn) {

            int step = (target > kx) ? 1 : -1;
            for (int x = kx + step; x != target; x += step) {
                if (board.playingBoard[x][ky] != Board.EMPTY) return false;
            }
        } else {

            int step = (target > ky) ? 1 : -1;
            for (int y = ky + step; y != target; y += step) {
                if (board.playingBoard[kx][y] != Board.EMPTY) return false;
            }
        }
        return true;
    }

    private static final int[][] blockadeFields = {{1, 3}, {3, 1}, {2, 2},   // Ecke (1,1)
            {1, 7}, {3, 9}, {2, 8},   // Ecke (1,9)
            {9, 3}, {7, 1}, {8, 2},   // Ecke (9,1)
            {9, 7}, {7, 9}, {8, 8}    // Ecke (9,9)
    };

    public int cornerBlockade(Board board) {
        int count = 0;
        for (int[] f : blockadeFields) {
            if (board.playingBoard[f[0]][f[1]] == Board.BLACK) count++;
        }
        return count * cornerBlockadeWeight;
    }

    public int material(Board board) {
        int blackCount = 0;
        int whiteCount = 0;

        for (int r = 1; r <= 9; r++) {
            for (int c = 1; c <= 9; c++) {
                if (board.playingBoard[r][c] == Board.BLACK) {
                    blackCount++;
                }
                if (board.playingBoard[r][c] == Board.WHITE) {
                    whiteCount++;
                }
            }
        }
        return (whiteCount * materialWhiteWeight) - (blackCount * materialBlackWeight);
    }

    public int checkBoardRepetition(Board board) {
        for (int[][] past : board.boardHistory) {
            if (Arrays.deepEquals(past, board.playingBoard)) {
                return repetitionPenalty;
            }
        }
        return 0;
    }

    public int getWinScore() {
        return winScore;
    }

    public void setWinScore(int value) {
        winScore = value;
    }

    public int getTwoOpenLinesScore() {
        return twoOpenLinesScore;
    }

    public void setTwoOpenLinesScore(int value) {
        twoOpenLinesScore = value;
    }

    public int getOneOpenLineWhiteTurnScore() {
        return oneOpenLineWhiteTurnScore;
    }

    public void setOneOpenLineWhiteTurnScore(int value) {
        oneOpenLineWhiteTurnScore = value;
    }

    public int getOneOpenLineScore() {
        return oneOpenLineScore;
    }

    public void setOneOpenLineScore(int value) {
        oneOpenLineScore = value;
    }

    public int getEscapeKingWeight() {
        return escapeKingWeight;
    }

    public void setEscapeKingWeight(int value) {
        escapeKingWeight = value;
    }

    public int getPressureWeight() {
        return pressureWeight;
    }

    public void setPressureWeight(int value) {
        pressureWeight = value;
    }

    public int getCornerReachedScore() {
        return cornerReachedScore;
    }

    public void setCornerReachedScore(int value) {
        cornerReachedScore = value;
    }

    public int getDistanceCornerWeight() {
        return distanceCornerWeight;
    }

    public void setDistanceCornerWeight(int value) {
        distanceCornerWeight = value;
    }

    public int getCornerBlockadeWeight() {
        return cornerBlockadeWeight;
    }

    public void setCornerBlockadeWeight(int value) {
        cornerBlockadeWeight = value;
    }

    public int getMaterialWhiteWeight() {
        return materialWhiteWeight;
    }

    public void setMaterialWhiteWeight(int value) {
        materialWhiteWeight = value;
    }

    public int getMaterialBlackWeight() {
        return materialBlackWeight;
    }

    public void setMaterialBlackWeight(int value) {
        materialBlackWeight = value;
    }

    public int getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(int value) {
        repetitionPenalty = value;
    }
}
