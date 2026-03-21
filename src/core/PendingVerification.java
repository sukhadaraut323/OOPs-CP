package core;

import persons.Voter;

public class PendingVerification {

    private Voter existingVoter;  // already registered voter
    private Voter newVoter;       // newly registered voter
    private String reason;        // why it is pending
    private String status;        // PENDING, APPROVED, REJECTED

    public PendingVerification(Voter existingVoter, Voter newVoter) {
        this.existingVoter = existingVoter;
        this.newVoter      = newVoter;
        this.reason        = "Duplicate full name detected: "
                + existingVoter.getName();
        this.status        = "PENDING";
    }

    // Getters
    public Voter getExistingVoter() { return existingVoter; }
    public Voter getNewVoter()      { return newVoter; }
    public String getReason()       { return reason; }
    public String getStatus()       { return status; }

    // Setters
    public void setStatus(String status) { this.status = status; }

    public void display() {
        System.out.println("--- Pending Verification ---");
        System.out.println("Reason          : " + reason);
        System.out.println("Status          : " + status);
        System.out.println("Existing Voter  : " + existingVoter.getName()
                + " | ID: " + existingVoter.getVoterID());
        System.out.println("New Voter       : " + newVoter.getName()
                + " | ID: " + newVoter.getVoterID());
    }
}