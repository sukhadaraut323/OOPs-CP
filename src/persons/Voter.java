package persons;

import interfaces.Votable;
import interfaces.Verifiable;

public class Voter extends Person implements Votable, Verifiable {
    private String voterID;
    private String aadhaarOrPAN;
    private String constituencyID;
    private boolean hasVoted;
    private boolean isDualVerified;

    public Voter(String name, int age, String phoneNumber,
                 String voterID, String aadhaarOrPAN, String constituencyID) {
        super(name, age, phoneNumber);
        this.voterID = voterID;
        this.aadhaarOrPAN = aadhaarOrPAN;
        this.constituencyID = constituencyID;
        this.hasVoted = false;
        this.isDualVerified = false;
    }

    // Votable
    @Override
    public boolean castVote(String candidateID) {
        if (hasVoted) {
            System.out.println("❌ You have already voted!");
            return false;
        }
        hasVoted = true;
        System.out.println("✅ Vote cast successfully for candidate: " + candidateID);
        return true;
    }

    @Override
    public boolean hasVoted() { return hasVoted; }

    // Verifiable
    @Override
    public boolean verifyIdentity(String voterID) {
        return this.voterID.equals(voterID);
    }

    @Override
    public boolean dualVerify(String voterID, String documentNumber) {
        if (this.voterID.equals(voterID) && this.aadhaarOrPAN.equals(documentNumber)) {
            isDualVerified = true;
            return true;
        }
        return false;
    }

    // Getters
    public String getVoterID() { return voterID; }
    public String getAadhaarOrPAN() { return aadhaarOrPAN; }
    public String getConstituencyID() { return constituencyID; }
    public boolean isDualVerified() { return isDualVerified; }

    @Override
    public void getDetails() {
        System.out.println("--- Voter Details ---");
        System.out.println("Name        : " + getName());
        System.out.println("Age         : " + getAge());
        System.out.println("Phone       : " + getPhoneNumber());
        System.out.println("Voter ID    : " + voterID);
        System.out.println("Constituency: " + constituencyID);
        System.out.println("Has Voted   : " + hasVoted);
    }
}