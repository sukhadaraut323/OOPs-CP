package core;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ballot {
    private String ballotID;
    private String voterID;
    private String candidateID;
    private String constituencyID;
    private String timestamp;
    private boolean isValid;

    public Ballot(String ballotID, String voterID, String candidateID, String constituencyID) {
        this.ballotID       = ballotID;
        this.voterID        = voterID;
        this.candidateID    = candidateID;
        this.constituencyID = constituencyID;
        this.isValid        = true;
        this.timestamp      = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }

    public void printBallot() {
        System.out.println("--- Ballot Receipt ---");
        System.out.println("Ballot ID    : " + ballotID);
        System.out.println("Voter ID     : " + voterID);
        System.out.println("Candidate ID : " + candidateID);
        System.out.println("Constituency : " + constituencyID);
        System.out.println("Timestamp    : " + timestamp);
        System.out.println("Status       : " + (isValid ? "✅ Valid" : "❌ Invalid"));
    }

    public void invalidateBallot() {
        isValid = false;
        System.out.println("❌ Ballot " + ballotID + " has been invalidated.");
    }

    // Getters
    public String getBallotID()       { return ballotID; }
    public String getVoterID()        { return voterID; }
    public String getCandidateID()    { return candidateID; }
    public String getConstituencyID() { return constituencyID; }
    public String getTimestamp()      { return timestamp; }
    public boolean isValid()          { return isValid; }
}