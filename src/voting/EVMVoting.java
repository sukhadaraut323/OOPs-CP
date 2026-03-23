package voting;

import core.Ballot;
import election.GeneralElection;
import persons.Candidate;
import persons.Voter;

import java.util.ArrayList;
import java.util.Scanner;

public class EVMVoting {
    private GeneralElection election;
    private ArrayList<Ballot> ballots;
    private int ballotCounter;
    private Scanner sc;                         

    public EVMVoting(GeneralElection election, Scanner sc) {  
        this.election      = election;
        this.ballots       = new ArrayList<>();
        this.ballotCounter = 1;
        this.sc            = sc;                
    }

    // Main EVM voting process for a voter
    public boolean startVoting(Voter voter) {
                                                
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║          🗳️  EVM VOTING           ║");
        System.out.println("╚══════════════════════════════════╝");

        // Step 1 — Check election is open
        if (!election.isOpen()) {
            System.out.println("❌ Election is currently closed. Voting not allowed.");
            return false;
        }

        // Step 1b — Check voting time   
        if (!isWithinVotingHours()) {
            java.time.LocalTime now = java.time.LocalTime.now();
            System.out.println("Voting is only allowed between 7:00 AM and 5:30 PM.");
            System.out.println("Current time: " + now.format(
                    java.time.format.DateTimeFormatter.ofPattern("hh:mm a")));
            return false;
        }

        // Step 2 — Check voter already voted
        if (voter.hasVoted()) {
            System.out.println("❌ You have already cast your vote!");
            return false;
        }

        // Step 2b — Check voter is verified
        if (!voter.isVerified()) {
            System.out.println("Your registration is PENDING admin verification.");
            System.out.println("Please visit the Election Office.");
            return false;
        }

        // Step 3 — Verify voter identity
        System.out.print("🔐 Enter your Voter ID to verify: ");
        String inputVoterID = sc.nextLine();

        if (!voter.verifyIdentity(inputVoterID)) {
            System.out.println("❌ Voter ID verification failed!");
            return false;
        }
        System.out.println("✅ Identity verified!");

        // Step 4 — Get candidates in voter's constituency
        String constituencyID = voter.getConstituencyID();
        ArrayList<Candidate> constCandidates =
                election.getCandidatesByConstituency(constituencyID);

        if (constCandidates.isEmpty()) {
            System.out.println("❌ No candidates found in your constituency!");
            return false;
        }

        // Step 5 — Display EVM ballot
        displayEVMBallot(constCandidates);

        // Step 6 — Voter selects candidate
        System.out.print("🔢 Enter candidate number to vote: ");
        int choice = -1;

        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input!");
            return false;
        }

        if (choice < 1 || choice > constCandidates.size()) {
            System.out.println("❌ Invalid candidate selection!");
            return false;
        }

        // Step 7 — Check if selected candidate is dual verified
        Candidate selectedCandidate = constCandidates.get(choice - 1);

        if (isDuplicateNameConstituency(selectedCandidate, constCandidates)
                && !selectedCandidate.isDualVerified()) {
            System.out.println("⚠️  This candidate has not been dual verified yet.");
            System.out.println("   Vote cannot be cast for unverified candidate.");
            return false;
        }

        // Step 8 — Confirm vote
        System.out.println("\nYou selected: " + selectedCandidate.getName()
                + " (" + selectedCandidate.getCandidateID() + ")");
        System.out.print("Confirm vote? (yes/no): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("❌ Vote cancelled.");
            return false;
        }

        // Step 9 — Cast vote
        voter.castVote(selectedCandidate.getCandidateID());
        selectedCandidate.incrementVote();

        // Step 10 — Generate ballot receipt
        String ballotID = "BAL" + String.format("%04d", ballotCounter++);
        Ballot ballot   = new Ballot(ballotID, voter.getVoterID(),
                selectedCandidate.getCandidateID(), constituencyID);
        ballots.add(ballot);

        System.out.println();
        ballot.printBallot();
        System.out.println("🎉 Thank you for voting!");

        return true;
    }

    // Display EVM screen with candidates
    private void displayEVMBallot(ArrayList<Candidate> candidates) {
        System.out.println();
        System.out.println("┌──────────────────────────────────┐");
        System.out.println("│         CANDIDATES BALLOT         │");
        System.out.println("├──────────────────────────────────┤");
        for (int i = 0; i < candidates.size(); i++) {
            Candidate c = candidates.get(i);
            System.out.printf("│  %d. %-15s  ID: %-8s   │%n",
                    (i + 1), c.getName(), c.getCandidateID());
        }
        System.out.println("└──────────────────────────────────┘");
    }

    // Check if a candidate has a duplicate name in the same constituency
    private boolean isDuplicateNameConstituency(Candidate target,
                                                 ArrayList<Candidate> candidates) {
        int count = 0;
        for (Candidate c : candidates) {
            if (c.getName().equalsIgnoreCase(target.getName())) {
                count++;
            }
        }
        return count > 1;
    }

    // View all ballots cast (admin only)
    public void viewAllBallots() {
        if (ballots.isEmpty()) {
            System.out.println("No ballots cast yet.");
            return;
        }
        System.out.println("--- All Ballots ---");
        for (Ballot b : ballots) {
            b.printBallot();
            System.out.println();
        }
    }

    // ── Voting time validation ─────────────────
    private boolean isWithinVotingHours() {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime start = java.time.LocalTime.of(7, 0);   // 7:00 AM
        java.time.LocalTime end   = java.time.LocalTime.of(17, 30); // 5:30 PM
        return !now.isBefore(start) && !now.isAfter(end);
    }

    public int getTotalVotesCast() { return ballots.size(); }
    public ArrayList<Ballot> getBallots() { return ballots; }
}