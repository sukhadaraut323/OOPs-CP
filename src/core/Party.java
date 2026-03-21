package core;

import java.util.ArrayList;

public class Party {
    private String partyID;
    private String partyName;
    private String partySymbol;
    private ArrayList<String> candidateIDs;

    public Party(String partyID, String partyName, String partySymbol) {
        this.partyID = partyID;
        this.partyName = partyName;
        this.partySymbol = partySymbol;
        this.candidateIDs = new ArrayList<>();
    }

    public void addCandidate(String candidateID) {
        candidateIDs.add(candidateID);
        System.out.println("✅ Candidate " + candidateID + " added to party " + partyName);
    }

    public void removeCandidate(String candidateID) {
        if (candidateIDs.remove(candidateID)) {
            System.out.println("✅ Candidate " + candidateID + " removed from party " + partyName);
        } else {
            System.out.println("❌ Candidate not found in party " + partyName);
        }
    }

    public void displayParty() {
        System.out.println("--- Party Details ---");
        System.out.println("Party ID    : " + partyID);
        System.out.println("Party Name  : " + partyName);
        System.out.println("Symbol      : " + partySymbol);
        System.out.println("Candidates  : " + candidateIDs);
    }

    // Getters
    public String getPartyID() { return partyID; }
    public String getPartyName() { return partyName; }
    public String getPartySymbol() { return partySymbol; }
    public ArrayList<String> getCandidateIDs() { return candidateIDs; }
}