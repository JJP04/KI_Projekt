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

private static final double mutationRate = 0.10;  // Wahrscheinlichkeit pro Gen
private static final double mutationStrength = 0.10;  // Schrittweite
private static final double elitismus = 0.10; // besten 10% unverändert behalten

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
                10000,
            rand(5000,10000),
            rand(3000,7000),
            rand(100,1000),
            rand(1,20),
            rand(-30,-1),
                10000,
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



    public TabultKi crossover(TabultKi parent1, TabultKi parent2) {

        BewertungsfunktionEvolution e1 = parent1.getEval();
        BewertungsfunktionEvolution e2 = parent2.getEval();

        BewertungsfunktionEvolution child = new BewertungsfunktionEvolution(
                10000,
            random.nextBoolean() ? e1.getTwoOpenLinesScore()         : e2.getTwoOpenLinesScore(),
            random.nextBoolean() ? e1.getOneOpenLineWhiteTurnScore() : e2.getOneOpenLineWhiteTurnScore(),
            random.nextBoolean() ? e1.getOneOpenLineScore()          : e2.getOneOpenLineScore(),
            random.nextBoolean() ? e1.getEscapeKingWeight()          : e2.getEscapeKingWeight(),
            random.nextBoolean() ? e1.getPressureWeight()            : e2.getPressureWeight(),
                10000,
            random.nextBoolean() ? e1.getDistanceCornerWeight()      : e2.getDistanceCornerWeight(),
            random.nextBoolean() ? e1.getCornerBlockadeWeight()      : e2.getCornerBlockadeWeight(),
            random.nextBoolean() ? e1.getMaterialWhiteWeight()       : e2.getMaterialWhiteWeight(),
            random.nextBoolean() ? e1.getMaterialBlackWeight()       : e2.getMaterialBlackWeight(),
            random.nextBoolean() ? e1.getRepetitionPenalty()         : e2.getRepetitionPenalty()
        );

        return new TabultKi(child);
    }



    public void mutate(TabultKi ki) {

        BewertungsfunktionEvolution e = ki.getEval();

        e.setTwoOpenLinesScore(         mutateParam(e.getTwoOpenLinesScore(),          3000, 10000));
        e.setOneOpenLineWhiteTurnScore( mutateParam(e.getOneOpenLineWhiteTurnScore(),  1000,  9000));
        e.setOneOpenLineScore(          mutateParam(e.getOneOpenLineScore(),              0,  2000));
        e.setEscapeKingWeight(          mutateParam(e.getEscapeKingWeight(),              1,    40));
        e.setPressureWeight(            mutateParam(e.getPressureWeight(),             -60,    -1));
        e.setDistanceCornerWeight(      mutateParam(e.getDistanceCornerWeight(),       -60,    -1));
        e.setCornerBlockadeWeight(      mutateParam(e.getCornerBlockadeWeight(),      -100,    -1));
        e.setMaterialWhiteWeight(       mutateParam(e.getMaterialWhiteWeight(),         20,   400));
        e.setMaterialBlackWeight(       mutateParam(e.getMaterialBlackWeight(),         10,   200));
        e.setRepetitionPenalty(         mutateParam(e.getRepetitionPenalty(),         -400,    -1));
    }



    private int mutateParam(int value, int min, int max) {

        if (random.nextDouble() >= mutationRate) {
            return value; // keine Mutation
        }

        double factor = (random.nextDouble() * 2 - 1) * mutationStrength;
        int delta = (int) Math.round(value * factor);

        if (delta == 0) {
            delta = random.nextBoolean() ? 1 : -1;
        }

        int mutated = value + delta;
        return Math.max(min, Math.min(max, mutated));
    }


    public List<TabultKi> nextGeneration(List<TabultKi> population) {

        // nach Fitness sortieren
        population.sort(Comparator.comparingInt(TabultKi::getFitness).reversed());

        int size = population.size();
        int eliteCount = Math.max(1, (int) Math.round(size * elitismus));  // 10%, mind. 1

        List<TabultKi> next = new ArrayList<>();

        // Elitismus
        for (int i = 0; i < eliteCount; i++) {
            next.add(population.get(i));       // nicht mutieren -> bleiben erhalten
        }

        // crossover
        while (next.size() < size) {
            TabultKi p1 = selectParent(population);
            TabultKi p2 = selectParent(population);
            TabultKi child = crossover(p1, p2);
            mutate(child);
            next.add(child);
        }

        return next;
    }


    //Einer von 3 random kis wir der parent
    private TabultKi selectParent(List<TabultKi> population) {
        TabultKi best = population.get(random.nextInt(population.size()));
        for (int i = 1; i < 3; i++) {
            TabultKi c = population.get(random.nextInt(population.size()));
            if (c.getFitness() > best.getFitness()) best = c;
        }
        return best;
    }

}

