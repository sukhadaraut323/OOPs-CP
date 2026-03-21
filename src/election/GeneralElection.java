package election;

import core.Constituency;
import core.Party;
import core.Result;
import persons.Candidate;

import java.util.ArrayList;
import java.util.HashMap;

public class GeneralElection extends Election {

    private ArrayList<Constituency> constituencies;
    private ArrayList<Candidate> candidates;
    private ArrayList<Party> parties;
    private ArrayList<Result> results;

    public GeneralElection(String electionID, String electionName,
                           String startDate, String endDate) {
        super(electionID, electionName, startDate, endDate);
        this.constituencies = new ArrayList<>();
        this.candidates     = new ArrayList<>();
        this.parties        = new ArrayList<>();
        this.results        = new ArrayList<>();
    }

    // Add data
    public void addConstituency(Constituency c) {
        constituencies.add(c);
        System.out.println("✅ Constituency added: " + c.getConstituencyName());
    }

    public void addCandidate(Candidate c) {
        candidates.add(c);
        System.out.println("✅ Candidate added: " + c.getName());
    }

    public void addParty(Party p) {
        parties.add(p);
        System.out.println("✅ Party added: " + p.getPartyName());
    }

    // Conduct election — called by admin to start the process
    @Override
    public void conductElection() {
        if (!isOpen()) {
            System.out.println("❌ Election is not open yet!");
            return;
        }
        System.out.println("🗳️  General Election '" + getElectionName() + "' is now conducting.");
        System.out.println("   Voters can now cast their votes via EVM.");
    }

    // Calculate winner for a specific constituency
    @Override
    public Candidate calculateWinner(ArrayList<Candidate> constituencyCandidates) {
        Candidate winner = null;
        int maxVotes = -1;

        for (Candidate c : constituencyCandidates) {
            if (c.getVoteCount() > maxVotes) {
                maxVotes = c.getVoteCount();
                winner = c;
            }
        }
        return winner;
    }

    // Announce results for all constituencies
    @Override
    public void announceResult() {
        System.out.println();
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║       🗳️  ELECTION RESULTS        ║");
        System.out.println("╚══════════════════════════════════╝");

        // Party-wise seat count
        HashMap<String, Integer> partySeats = new HashMap<>();
        for (Party p : parties) {
            partySeats.put(p.getPartyID(), 0);
        }

        for (Constituency constituency : constituencies) {
            System.out.println("\n📍 Constituency: " + constituency.getConstituencyName());

            // Get candidates of this constituency
            ArrayList<Candidate> constCandidates = new ArrayList<>();
            for (Candidate c : candidates) {
                if (c.getConstituencyID().equals(constituency.getConstituencyID())) {
                    constCandidates.add(c);
                }
            }

            // Print all candidate votes
            for (Candidate c : constCandidates) {
                System.out.println("   " + c.getName() + " (" + getPartyName(c.getPartyID()) + ")"
                        + " → " + c.getVoteCount() + " votes");
            }

            // Find winner
            Candidate winner = calculateWinner(constCandidates);
            if (winner != null) {
                System.out.println("   🏆 Winner: " + winner.getName()
                        + " (" + getPartyName(winner.getPartyID()) + ")"
                        + " with " + winner.getVoteCount() + " votes");

                // Add result
                results.add(new Result(constituency.getConstituencyID(),
                        constituency.getConstituencyName(), winner));

                // Update party seats
                partySeats.put(winner.getPartyID(),
                        partySeats.getOrDefault(winner.getPartyID(), 0) + 1);
            }
        }

        // Overall party results
        System.out.println("\n╔══════════════════════════════════╗");
        System.out.println("║       📊 PARTY-WISE SEATS         ║");
        System.out.println("╚══════════════════════════════════╝");

        String overallWinnerParty = null;
        int maxSeats = -1;

        for (Party p : parties) {
            int seats = partySeats.getOrDefault(p.getPartyID(), 0);
            System.out.println("   " + p.getPartyName() + " (" + p.getPartySymbol() + ")"
                    + " → " + seats + " seats");
            if (seats > maxSeats) {
                maxSeats = seats;
                overallWinnerParty = p.getPartyName();
            }
        }

        System.out.println("\n🎉 Overall Winner: " + overallWinnerParty
                + " with " + maxSeats + " seats!");
    }

    // Helper — get party name from partyID
    private String getPartyName(String partyID) {
        for (Party p : parties) {
            if (p.getPartyID().equals(partyID)) {
                return p.getPartyName();
            }
        }
        return "Independent";
    }

    // Getters
    public ArrayList<Constituency> getConstituencies() { return constituencies; }
    public ArrayList<Candidate> getCandidates() { return candidates; }
    public ArrayList<Party> getParties() { return parties; }
    public ArrayList<Result> getResults() { return results; }

    // Find constituency by ID
    public Constituency getConstituencyByID(String id) {
        for (Constituency c : constituencies) {
            if (c.getConstituencyID().equals(id)) return c;
        }
        return null;
    }

    // Find candidate by ID
    public Candidate getCandidateByID(String id) {
        for (Candidate c : candidates) {
            if (c.getCandidateID().equals(id)) return c;
        }
        return null;
    }

    // Find voter's candidates by constituencyID
    public ArrayList<Candidate> getCandidatesByConstituency(String constituencyID) {
        ArrayList<Candidate> result = new ArrayList<>();
        for (Candidate c : candidates) {
            if (c.getConstituencyID().equals(constituencyID)) {
                result.add(c);
            }
        }
        return result;
    }
}