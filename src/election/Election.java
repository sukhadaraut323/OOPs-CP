package election;

import persons.Candidate;
import java.util.ArrayList;

public abstract class Election {
    private String electionID;
    private String electionName;
    private String startDate;
    private String endDate;
    private boolean isOpen;

    public Election(String electionID, String electionName, String startDate, String endDate) {
        this.electionID = electionID;
        this.electionName = electionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isOpen = false;
    }

    // Abstract methods — must be implemented by subclass
    public abstract void conductElection();
    public abstract Candidate calculateWinner(ArrayList<Candidate> candidates);
    public abstract void announceResult();

    // Open / Close election
    public void openElection() {
        isOpen = true;
        System.out.println("✅ Election '" + electionName + "' is now OPEN for voting!");
    }

    public void closeElection() {
        isOpen = false;
        System.out.println("🔒 Election '" + electionName + "' is now CLOSED.");
    }

    public void displayElectionInfo() {
        System.out.println("--- Election Info ---");
        System.out.println("Election ID  : " + electionID);
        System.out.println("Name         : " + electionName);
        System.out.println("Start Date   : " + startDate);
        System.out.println("End Date     : " + endDate);
        System.out.println("Status       : " + (isOpen ? "🟢 OPEN" : "🔴 CLOSED"));
    }

    // Getters
    public String getElectionID() { return electionID; }
    public String getElectionName() { return electionName; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isOpen() { return isOpen; }
}