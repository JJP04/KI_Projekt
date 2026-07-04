package tablut.Evolution;

public class TabultKi {


 private BewertungsfunktionEvolution eval;

    private int wins;
    private int losses;
    private int draws;

   
 public TabultKi(BewertungsfunktionEvolution eval) {
        this.eval = eval;
    }


    public BewertungsfunktionEvolution getEval() {
        return eval;
    }

    public int getFitness() {
        return wins * 3 + draws;
    }

    public void addWin() {
        wins++;
    }

    public void addLoss() {
        losses++;
    }

    public void addDraw() {
        draws++;
    }

    public void resetStats() {
        wins = losses = draws = 0;
    }

    public int getWins(){
      return wins;
    }
    public int getLosses(){
      return losses;
    }
    public int getDraws(){
      return draws;
    }
}
