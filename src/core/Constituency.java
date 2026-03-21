package core;

import java.util.ArrayList;

public class Constituency {
    private String constituencyID;
    private String constituencyName;
    private ArrayList<String> voterIDs;
    private ArrayList<String> candidateIDs;

    public Constituency(String constituencyID, String constituencyName) {
        this.constituencyID = constituencyID;
        this.constituencyName = constituencyName;
        this.voterIDs = new ArrayList<>();
        this.candidateIDs = new ArrayList<>();
    }

    public void addVoter(String voterID) {
        voterIDs.add(voterID);
    }

    public void addCandidate(String candidateID) {
        candidateIDs.add(candidateID);
    }

    public void removeCandidate(String candidateID) {
        candidateIDs.remove(candidateID);
    }

    public int getTotalVoters() { return voterIDs.size(); }

    public void displayConstituency() {
        System.out.println("--- Constituency Details ---");
        System.out.println("ID          : " + constituencyID);
        System.out.println("Name        : " + constituencyName);
        System.out.println("Total Voters: " + voterIDs.size());
        System.out.println("Candidates  : " + candidateIDs);
    }

    // Getters
    public String getConstituencyID() { return constituencyID; }
    public String getConstituencyName() { return constituencyName; }
    public ArrayList<String> getVoterIDs() { return voterIDs; }
    public ArrayList<String> getCandidateIDs() { return candidateIDs; }
}