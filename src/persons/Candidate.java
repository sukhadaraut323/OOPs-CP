package persons;

import interfaces.Verifiable;

public class Candidate extends Person implements Verifiable {
    private String candidateID;
    private String voterID;
    private String aadhaarOrPAN;
    private String partyID;
    private String constituencyID;
    private int voteCount;
    private boolean isDualVerified;

    public Candidate(String name, int age, String phoneNumber,
                     String candidateID, String voterID, String aadhaarOrPAN,
                     String partyID, String constituencyID) {
        super(name, age, phoneNumber);
        this.candidateID = candidateID;
        this.voterID = voterID;
        this.aadhaarOrPAN = aadhaarOrPAN;
        this.partyID = partyID;
        this.constituencyID = constituencyID;
        this.voteCount = 0;
        this.isDualVerified = false;
    }

    public void incrementVote() { voteCount++; }

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
    public String getCandidateID() { return candidateID; }
    public String getVoterID() { return voterID; }
    public String getAadhaarOrPAN() { return aadhaarOrPAN; }
    public String getPartyID() { return partyID; }
    public String getConstituencyID() { return constituencyID; }
    public int getVoteCount() { return voteCount; }
    public boolean isDualVerified() { return isDualVerified; }
    public void setDualVerified(boolean val) { isDualVerified = val; }

    @Override
    public void getDetails() {
        System.out.println("--- Candidate Details ---");
        System.out.println("Name         : " + getName());
        System.out.println("Age          : " + getAge());
        System.out.println("Candidate ID : " + candidateID);
        System.out.println("Party ID     : " + partyID);
        System.out.println("Constituency : " + constituencyID);
        System.out.println("Vote Count   : " + voteCount);
        System.out.println("Dual Verified: " + isDualVerified);
    }
}