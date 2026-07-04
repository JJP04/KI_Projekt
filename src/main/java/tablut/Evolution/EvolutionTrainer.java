package tablut.Evolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;

public class EvolutionTrainer {
  
public static final int POPULATION_SIZE =4;


private Random random = new Random();

private int rand(int min, int max) {
    return random.nextInt(max - min + 1) + min;
    }


public static void main(String[] args) {

    EvolutionTrainer trainer = new EvolutionTrainer();

    List<TabultKi> population = trainer.createInitialPopulation(POPULATION_SIZE);

    System.out.println("Anzahl an KIs:"+population.size());
    trainer.playTournament(population);

    population.sort(Comparator.comparingInt(TabultKi::getFitness).reversed());


    for (int i = 0; i < 5; i++) {

    TabultKi ki = population.get(i);

    System.out.println(
        "Platz " + (i + 1)
        + " Fitness: " + ki.getFitness()
        + " Siege: " + ki.getWins()
        + " Niederlagen: " + ki.getLosses()
        + " Unentschieden: " + ki.getDraws()
    );
  }


}

  
public List<TabultKi> createInitialPopulation(int size) {

    List<TabultKi> population = new ArrayList<>();

    for(int i = 0; i < size; i++) {

    BewertungsfunktionEvolution eval =
        new BewertungsfunktionEvolution(
            1000,
            rand(5000,10000),
            rand(3000,7000),
            rand(100,1000),
            rand(1,20),
            rand(-30,-1),
            1000,
            rand(-30,-1),
            rand(-50,-5),
            rand(50,200),
            rand(20,100),
            rand(-200,-10)
        );

    population.add(new TabultKi(eval));
}

        return population;
    }



    public void playGame(TabultKi ki1, TabultKi ki2) {

    Board board = new Board();

    int maxMoves = 200;

    for (int i = 0; i < maxMoves; i++) {

        Move move;

        if (!board.playBlackTurn) {
            move = SearchMovesEvolution.findBestMoveAlphaBeta(
                    board,
                    1000,
                    ki1.getEval()
            );
        } else {
            move = SearchMovesEvolution.findBestMoveAlphaBeta(
                    board,
                    1000,
                    ki2.getEval()
            );
        }

        if (move == null) {
            break;
        }

        Move.makeMove(board, move);

        if (GameLogic.whiteWin(board)) {
            ki1.addWin();
            ki2.addLoss();
            return;
        }

        if (GameLogic.blackWin(board)) {
            ki1.addLoss();
            ki2.addWin();
            return;
        }
    }

    // Unentschieden
    ki1.addDraw();
    ki2.addDraw();
}


public void playTournament(List<TabultKi> population) {

    // Alte Ergebnisse löschen
    for (TabultKi ki : population) {
        ki.resetStats();
    }

    for (int i = 0; i < population.size(); i++) {

        for (int j = i + 1; j < population.size(); j++) {

            System.out.println("Spiel: " + i + " gegen " + j);
            playGame(population.get(i),population.get(j));
            //System.out.println("Spiel: " + j + " gegen " + i);
            //playGame(population.get(j),population.get(i));
        }
    }

    
}

}

