package tablut.Evolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import tablut.board.Board;
import tablut.board.Move;
import tablut.game.GameLogic;

public class EvolutionTrainer {

    public static final int POPULATION_SIZE = 8;
    public static final int GENERATIONS = 15;
    private static final long MOVE_TIME = 500;

    private static final double mutationRate = 0.20;  // Wahrscheinlichkeit pro Gen
    private static final double mutationStrength = 0.10;  // Schrittweite
    private static final double elitismus = 0.10;

    private Random random = new Random();

    private int rand(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }


    public static void main(String[] args) {

        EvolutionTrainer trainer = new EvolutionTrainer();

        List<TabultKi> population = trainer.createInitialPopulation(POPULATION_SIZE);

        BewertungsfunktionEvolution spezial = new BewertungsfunktionEvolution(10000, 7907, 3999, 864, 5, -12, 10000, -20, -26, 57, 90, -44);

        trainer.testSpezialGegenStandard(spezial, 10);

        //  trainer.balanceTest(50);

//    System.out.println("Anzahl an KIs:"+population.size());
//
//    for (int generation = 1; generation <= GENERATIONS; generation++) {
//
//        System.out.print("\n===== Generation " + generation + " =====\n");
//        trainer.playTournament(population);
//        population.sort(Comparator.comparingInt(TabultKi::getFitness).reversed());
//
//        StringBuilder ranking = new StringBuilder();
//        for (int i = 0; i < POPULATION_SIZE; i++) {
//            TabultKi ki = population.get(i);
//            ranking.append(String.format(
//                "%2d: Fitness=%3d  W=%2d D=%2d L=%2d%n",
//                i, ki.getFitness(), ki.getWins(), ki.getDraws(), ki.getLosses()));
//        }
//        System.out.print(ranking.toString());
//
//        trainer.printBestKi(population.getFirst(), generation);
//        trainer.printBestKi(population.getLast(), generation);
//        population = trainer.nextGeneration(population);


//  }


    }


    public List<TabultKi> createInitialPopulation(int size) {

        List<TabultKi> population = new ArrayList<>();

        for (int i = 0; i < size; i++) {

            BewertungsfunktionEvolution eval = new BewertungsfunktionEvolution(10000, rand(5000, 10000), rand(3000, 7000), rand(100, 1000), rand(1, 20), rand(-30, -1), 10000, rand(-30, -1), rand(-50, -5), rand(50, 200), rand(20, 100), rand(-200, -10));

            population.add(new TabultKi(eval));
        }

        return population;
    }


    public void playGame(TabultKi ki1, TabultKi ki2) {

        Board board = new Board();

        int maxMoves = 100;

        for (int i = 0; i < maxMoves; i++) {

            Move move;

            if (!board.playBlackTurn) {
                move = SearchMovesEvolution.findBestMoveAlphaBeta(board, MOVE_TIME, ki1.getEval());
            } else {
                move = SearchMovesEvolution.findBestMoveAlphaBeta(board, MOVE_TIME, ki2.getEval());
            }

            if (move == null) {
                break;
            }

            Move.makeMove(board, move);

            if (GameLogic.whiteWin(board)) {
                System.out.println("Weiß gewinnt");
                ki1.addWin();
                ki2.addLoss();
                return;
            }

            if (GameLogic.blackWin(board)) {
                System.out.println("Schwarz gewinnt");

                ki1.addLoss();
                ki2.addWin();
                return;
            }
        }

        // Unentschieden
        System.out.println("Remis");
        ki1.addDraw();
        ki2.addDraw();


    }


    //Zwei KIs spielen nmal gegeneinander
    public void balanceTest(int games) {
        TabultKi weiss = new TabultKi(new BewertungsfunktionEvolution());  // Default-Werte
        TabultKi schwarz = new TabultKi(new BewertungsfunktionEvolution());  // Default-Werte

        for (int g = 0; g < games; g++) {
            playGame(weiss, schwarz);
        }


        System.out.println("\n===== Balance-Test (" + games + " Partien) =====");
        System.out.println("Weiß:    " + weiss.getWins());
        System.out.println("Schwarz: " + weiss.getLosses());
        System.out.println("Remis:   " + weiss.getDraws());
    }


    /**
     * Ursprüngliche KI (Stanard) spielt gegen KI mit neuer Bewertungsfunktion(Spezial)
     */
    public void testSpezialGegenStandard(BewertungsfunktionEvolution spezialEval, int spieleProSeite) {

        TabultKi spezial = new TabultKi(spezialEval);
        TabultKi standard = new TabultKi(new BewertungsfunktionEvolution()); // unveränderte Default-BF

        // Spezial-KI spielt als weiss
        for (int i = 0; i < spieleProSeite; i++) {
            playGame(spezial, standard);
        }
        // Zwischenstand nach Phase 1
        int weissWins = spezial.getWins();
        int weissDraws = spezial.getDraws();
        int weissLosses = spezial.getLosses();

        // Spezial-KI spielt als schwarz
        for (int i = 0; i < spieleProSeite; i++) {
            playGame(standard, spezial);
        }
        //  Zwischenstand
        int schwarzWins = spezial.getWins() - weissWins;
        int schwarzDraws = spezial.getDraws() - weissDraws;
        int schwarzLosses = spezial.getLosses() - weissLosses;


        int gesamt = spieleProSeite * 2;
        StringBuilder sb = new StringBuilder();
        sb.append("\n===== Spezial-BF vs. Standard-BF (").append(gesamt).append(" Partien) =====\n");
        sb.append("Ergebnisse aus Sicht der SPEZIAL-KI:\n");
        sb.append(String.format("  Als Weiß    (%d Partien): %d Siege, %d Remis, %d Niederlagen%n", spieleProSeite, weissWins, weissDraws, weissLosses));
        sb.append(String.format("  Als Schwarz (%d Partien): %d Siege, %d Remis, %d Niederlagen%n", spieleProSeite, schwarzWins, schwarzDraws, schwarzLosses));
        sb.append(String.format("  GESAMT: %d Siege, %d Remis, %d Niederlagen%n", spezial.getWins(), spezial.getDraws(), spezial.getLosses()));
        sb.append(String.format("  Punkte (Sieg=3, Remis=1): Spezial=%d  |  Standard=%d%n", spezial.getFitness(), standard.getFitness()));
        sb.append("================================================\n");

        System.out.print(sb);
    }


    //Tunier modus
    public void playTournament(List<TabultKi> population) {

        // Alte Ergebnisse löschen
        for (TabultKi ki : population) {
            ki.resetStats();
        }

        for (int i = 0; i < population.size(); i++) {

            for (int j = i + 1; j < population.size(); j++) {

                //System.out.println("Spiel: " + i + " gegen " + j);
                playGame(population.get(i), population.get(j));
                // System.out.println("Spiel: " + j + " gegen " + i);
                playGame(population.get(j), population.get(i));
            }
        }


    }


    public TabultKi crossover(TabultKi parent1, TabultKi parent2) {

        BewertungsfunktionEvolution e1 = parent1.getEval();
        BewertungsfunktionEvolution e2 = parent2.getEval();

        BewertungsfunktionEvolution child = new BewertungsfunktionEvolution(10000, random.nextBoolean() ? e1.getTwoOpenLinesScore() : e2.getTwoOpenLinesScore(), random.nextBoolean() ? e1.getOneOpenLineWhiteTurnScore() : e2.getOneOpenLineWhiteTurnScore(), random.nextBoolean() ? e1.getOneOpenLineScore() : e2.getOneOpenLineScore(), random.nextBoolean() ? e1.getEscapeKingWeight() : e2.getEscapeKingWeight(), random.nextBoolean() ? e1.getPressureWeight() : e2.getPressureWeight(), 10000, random.nextBoolean() ? e1.getDistanceCornerWeight() : e2.getDistanceCornerWeight(), random.nextBoolean() ? e1.getCornerBlockadeWeight() : e2.getCornerBlockadeWeight(), random.nextBoolean() ? e1.getMaterialWhiteWeight() : e2.getMaterialWhiteWeight(), random.nextBoolean() ? e1.getMaterialBlackWeight() : e2.getMaterialBlackWeight(), random.nextBoolean() ? e1.getRepetitionPenalty() : e2.getRepetitionPenalty());

        return new TabultKi(child);
    }


    public void mutate(TabultKi ki) {

        BewertungsfunktionEvolution e = ki.getEval();
        int before = e.getEscapeKingWeight();


        e.setTwoOpenLinesScore(mutateParam(e.getTwoOpenLinesScore(), 3000, 10000));
        e.setOneOpenLineWhiteTurnScore(mutateParam(e.getOneOpenLineWhiteTurnScore(), 1000, 9000));
        e.setOneOpenLineScore(mutateParam(e.getOneOpenLineScore(), 0, 2000));
        e.setEscapeKingWeight(mutateParam(e.getEscapeKingWeight(), 1, 40));
        e.setPressureWeight(mutateParam(e.getPressureWeight(), -60, -1));
        e.setDistanceCornerWeight(mutateParam(e.getDistanceCornerWeight(), -60, -1));
        e.setCornerBlockadeWeight(mutateParam(e.getCornerBlockadeWeight(), -100, -1));
        e.setMaterialWhiteWeight(mutateParam(e.getMaterialWhiteWeight(), 20, 400));
        e.setMaterialBlackWeight(mutateParam(e.getMaterialBlackWeight(), 10, 200));
        e.setRepetitionPenalty(mutateParam(e.getRepetitionPenalty(), -400, -1));

        System.out.println(before + " -> " + e.getEscapeKingWeight());
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
            BewertungsfunktionEvolution eval = population.get(i).getEval();
            // neue Bewertungsfunktion mit denselben Parametern erzeugen
            BewertungsfunktionEvolution copy = new BewertungsfunktionEvolution(10000, eval.getTwoOpenLinesScore(), eval.getOneOpenLineWhiteTurnScore(), eval.getOneOpenLineScore(), eval.getEscapeKingWeight(), eval.getPressureWeight(), 10000, eval.getDistanceCornerWeight(), eval.getCornerBlockadeWeight(), eval.getMaterialWhiteWeight(), eval.getMaterialBlackWeight(), eval.getRepetitionPenalty());

            next.add(new TabultKi(copy));      // nicht mutieren , bleiben erhalten
        }

        // crossover
        while (next.size() < size) {
            TabultKi p1 = selectParent(population);
            TabultKi p2 = selectParent(population);

            System.out.println(p1.getEval().getEscapeKingWeight() + " | " + p2.getEval().getEscapeKingWeight());


            TabultKi child = crossover(p1, p2);
            mutate(child);
            next.add(child);
        }


        return next;
    }


    //Auwahl der Parents
    private TabultKi selectParent(List<TabultKi> population) {
        TabultKi best = population.get(random.nextInt(population.size()));
        for (int i = 1; i < 3; i++) {
            TabultKi c = population.get(random.nextInt(population.size()));
            if (c.getFitness() > best.getFitness()) best = c;
        }
        return best;
    }


    public void printBestKi(TabultKi ki, int generation) {

        BewertungsfunktionEvolution e = ki.getEval();

        StringBuilder sb = new StringBuilder();
        sb.append("Generation ").append(generation).append('\n');
        sb.append("Fitness: ").append(ki.getFitness()).append('\n');
        sb.append("twoOpenLinesScore = ").append(e.getTwoOpenLinesScore()).append('\n');
        sb.append("oneOpenLineWhiteTurnScore = ").append(e.getOneOpenLineWhiteTurnScore()).append('\n');
        sb.append("oneOpenLineScore = ").append(e.getOneOpenLineScore()).append('\n');
        sb.append("escapeKingWeight = ").append(e.getEscapeKingWeight()).append('\n');
        sb.append("pressureWeight = ").append(e.getPressureWeight()).append('\n');
        sb.append("distanceCornerWeight = ").append(e.getDistanceCornerWeight()).append('\n');
        sb.append("cornerBlockadeWeight = ").append(e.getCornerBlockadeWeight()).append('\n');
        sb.append("materialWhiteWeight = ").append(e.getMaterialWhiteWeight()).append('\n');
        sb.append("materialBlackWeight = ").append(e.getMaterialBlackWeight()).append('\n');
        sb.append("repetitionPenalty = ").append(e.getRepetitionPenalty()).append('\n');
        sb.append("--------------------------------\n");

        System.out.print(sb);
    }
}

