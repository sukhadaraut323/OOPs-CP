package core;

import persons.Candidate;

public class Result {
    private String constituencyID;
    private String constituencyName;
    private Candidate winner;
    private int winningVotes;
    private int totalVotesCast;
    private int marginOfVictory;

    public Result(String constituencyID, String constituencyName, Candidate winner) {
        this.constituencyID   = constituencyID;
        this.constituencyName = constituencyName;
        this.winner           = winner;
        this.winningVotes     = winner.getVoteCount();
    }

    public void setTotalVotesCast(int total) {
        this.totalVotesCast = total;
    }

    public void setMarginOfVictory(int margin) {
        this.marginOfVictory = margin;
    }

    public void displayResult() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║        CONSTITUENCY RESULT        ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println("Constituency  : " + constituencyName);
        System.out.println("Winner        : " + winner.getName());
        System.out.println("Winning Votes : " + winningVotes);
        System.out.println("Total Votes   : " + totalVotesCast);
        System.out.println("Margin        : " + marginOfVictory + " votes");
    }

    // Getters
    public String getConstituencyID()   { return constituencyID; }
    public String getConstituencyName() { return constituencyName; }
    public Candidate getWinner()        { return winner; }
    public int getWinningVotes()        { return winningVotes; }
    public int getTotalVotesCast()      { return totalVotesCast; }
    public int getMarginOfVictory()     { return marginOfVictory; }
}