package tablut.ki;

import java.util.List;

import tablut.board.Move;

public class KillerHeuristik {

  public static final int MAX_PLY = 64;

  public static Move[][] killerMoves = new Move[MAX_PLY][2];

  public static void storeKiller(Move move, int ply) {

    if (move.equals(killerMoves[ply][0]))
        return;

    killerMoves[ply][1] = killerMoves[ply][0];
    killerMoves[ply][0] = move;
}
  
  public static void sortMoves(List<Move> moves, int ply) {

    moves.sort((a, b) ->
            Integer.compare(
                    moveScore(b, ply),
                    moveScore(a, ply)
            )
    );
  }

  public static int moveScore(Move move, int ply) {

    if (move.equals(killerMoves[ply][0]))
        return 100000;

    if (move.equals(killerMoves[ply][1]))
        return 90000;

    return 0;
}
}
