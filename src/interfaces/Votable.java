package interfaces;

public interface Votable {
    boolean castVote(String candidateID);
    boolean hasVoted();
}