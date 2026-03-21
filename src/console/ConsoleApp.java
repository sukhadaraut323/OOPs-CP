package console;

import core.*;
import election.GeneralElection;
import persons.*;
import voting.EVMVoting;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApp {

    static Scanner sc = new Scanner(System.in);

    // Central data
    static GeneralElection election = null;
    static EVMVoting evmVoting      = null;
    static ElectionOfficer admin    = null;
    static ArrayList<Voter> voters  = new ArrayList<>();

    // Hardcoded admin credentials
    static final String ADMIN_ID       = "ADMIN001";
    static final String ADMIN_PASSWORD = "admin@123";

    public static void main(String[] args) {
        admin = new ElectionOfficer("Chief Election Officer", 50,
                "9000000000", ADMIN_ID, ADMIN_PASSWORD);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    🗳️  ELECTION SIMULATOR SYSTEM      ║");
        System.out.println("╚══════════════════════════════════════╝");

        while (true) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Admin Login");
            System.out.println("2. Voter Register");
            System.out.println("3. Voter Login");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> adminLogin();
                case "2" -> voterRegister();
                case "3" -> voterLogin();
                case "4" -> {
                    System.out.println("👋 Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    // ─────────────────────────────────────────
    //              ADMIN SECTION
    // ─────────────────────────────────────────

    static void adminLogin() {
        System.out.println("\n--- ADMIN LOGIN ---");
        System.out.print("Officer ID : ");
        String id  = sc.nextLine();
        System.out.print("Password   : ");
        String pwd = sc.nextLine();

        if (!admin.login(id, pwd)) return;

        adminDashboard();
    }

    static void adminDashboard() {
        while (true) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1.  Setup Election");
            System.out.println("2.  Add Constituency");
            System.out.println("3.  Add Party");
            System.out.println("4.  Add Candidate");
            System.out.println("5.  Open Election");
            System.out.println("6.  Close Election");
            System.out.println("7.  Announce Results");
            System.out.println("8.  View All Voters");
            System.out.println("9.  View All Candidates");
            System.out.println("10. View All Ballots");
            System.out.println("11. View Election Info");
            System.out.println("12. Logout");
            System.out.print("Choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1"  -> setupElection();
                case "2"  -> addConstituency();
                case "3"  -> addParty();
                case "4"  -> addCandidate();
                case "5"  -> openElection();
                case "6"  -> closeElection();
                case "7"  -> announceResults();
                case "8"  -> viewAllVoters();
                case "9"  -> viewAllCandidates();
                case "10" -> viewAllBallots();
                case "11" -> viewElectionInfo();
                case "12" -> { System.out.println("🔓 Admin logged out."); return; }
                default   -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    static void setupElection() {
        System.out.println("\n--- SETUP ELECTION ---");
        System.out.print("Election ID   : ");
        String id   = sc.nextLine();
        System.out.print("Election Name : ");
        String name = sc.nextLine();
        System.out.print("Start Date    : ");
        String start = sc.nextLine();
        System.out.print("End Date      : ");
        String end  = sc.nextLine();

        election  = new GeneralElection(id, name, start, end);
        evmVoting = new EVMVoting(election, sc);
        System.out.println("✅ Election setup complete!");
    }

    static void addConstituency() {
        if (!checkElectionSetup()) return;
        System.out.println("\n--- ADD CONSTITUENCY ---");
        System.out.print("Constituency ID   : ");
        String id   = sc.nextLine();
        System.out.print("Constituency Name : ");
        String name = sc.nextLine();

        Constituency c = new Constituency(id, name);
        election.addConstituency(c);
    }

    static void addParty() {
        if (!checkElectionSetup()) return;
        System.out.println("\n--- ADD PARTY ---");
        System.out.print("Party ID     : ");
        String id     = sc.nextLine();
        System.out.print("Party Name   : ");
        String name   = sc.nextLine();
        System.out.print("Party Symbol : ");
        String symbol = sc.nextLine();

        Party p = new Party(id, name, symbol);
        election.addParty(p);
    }

    static void addCandidate() {
        if (!checkElectionSetup()) return;
        System.out.println("\n--- ADD CANDIDATE ---");
        System.out.print("Name             : ");
        String name = sc.nextLine();
        System.out.print("Age              : ");
        int age = Integer.parseInt(sc.nextLine());
        System.out.print("Phone            : ");
        String phone = sc.nextLine();
        System.out.print("Candidate ID     : ");
        String candidateID = sc.nextLine();
        System.out.print("Voter ID         : ");
        String voterID = sc.nextLine();
        System.out.print("Aadhaar / PAN    : ");
        String document = sc.nextLine();
        System.out.print("Party ID         : ");
        String partyID = sc.nextLine();
        System.out.print("Constituency ID  : ");
        String constituencyID = sc.nextLine();

        Candidate newCandidate = new Candidate(name, age, phone,
                candidateID, voterID, document, partyID, constituencyID);

        // Check for duplicate names
        ArrayList<Candidate> allCandidates = election.getCandidates();

        if (DocumentVerifier.isDuplicateName(name, allCandidates)) {
            System.out.println("\n⚠️  Duplicate name detected: " + name);
            System.out.println("   Dual verification required for BOTH candidates.");

            // Get existing duplicates
            ArrayList<Candidate> duplicates = DocumentVerifier.getDuplicates(name, allCandidates);
            Candidate existingCandidate = duplicates.get(0);

            // Get verification details for existing candidate
            System.out.println("\n🔍 Re-verify EXISTING candidate: " + existingCandidate.getName()
                    + " (ID: " + existingCandidate.getCandidateID() + ")");
            System.out.print("   Enter their Voter ID     : ");
            String existingVoterID = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN  : ");
            String existingDocument = sc.nextLine();

            // Get verification details for new candidate
            System.out.println("\n🔍 Verify NEW candidate: " + newCandidate.getName()
                    + " (ID: " + newCandidate.getCandidateID() + ")");
            System.out.print("   Enter their Voter ID     : ");
            String newVoterID = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN  : ");
            String newDocument = sc.nextLine();

            // Dual verify both
            boolean verified = DocumentVerifier.dualVerifyBoth(
                    existingCandidate, existingVoterID, existingDocument,
                    newCandidate, newVoterID, newDocument);

            if (!verified) {
                System.out.println("❌ Candidate registration failed due to verification issues.");
                return;
            }

        }

        // Add candidate to election and constituency
        election.addCandidate(newCandidate);

        Constituency constituency = election.getConstituencyByID(constituencyID);
        if (constituency != null) {
            constituency.addCandidate(candidateID);
        } else {
            System.out.println("⚠️  Constituency not found. Candidate added without constituency link.");
        }

        // Add candidate to party
        for (Party p : election.getParties()) {
            if (p.getPartyID().equals(partyID)) {
                p.addCandidate(candidateID);
                break;
            }
        }
    }

    static void openElection() {
        if (!checkElectionSetup()) return;
        election.openElection();
        election.conductElection();
    }

    static void closeElection() {
        if (!checkElectionSetup()) return;
        election.closeElection();
    }

    static void announceResults() {
        if (!checkElectionSetup()) return;
        if (election.isOpen()) {
            System.out.println("❌ Please close the election before announcing results!");
            return;
        }
        election.announceResult();
    }

    static void viewAllVoters() {
        if (voters.isEmpty()) {
            System.out.println("No voters registered yet.");
            return;
        }
        System.out.println("\n--- ALL VOTERS ---");
        for (Voter v : voters) {
            v.getDetails();
            System.out.println();
        }
    }

    static void viewAllCandidates() {
        if (!checkElectionSetup()) return;
        if (election.getCandidates().isEmpty()) {
            System.out.println("No candidates added yet.");
            return;
        }
        System.out.println("\n--- ALL CANDIDATES ---");
        for (Candidate c : election.getCandidates()) {
            c.getDetails();
            System.out.println();
        }
    }

    static void viewAllBallots() {
        if (evmVoting == null) {
            System.out.println("❌ EVM not initialized yet.");
            return;
        }
        evmVoting.viewAllBallots();
    }

    static void viewElectionInfo() {
        if (!checkElectionSetup()) return;
        election.displayElectionInfo();
    }

    // ─────────────────────────────────────────
    //              VOTER SECTION
    // ─────────────────────────────────────────

    static void voterRegister() {
        System.out.println("\n--- VOTER REGISTRATION ---");
        System.out.print("Name              : ");
        String name = sc.nextLine();
        System.out.print("Age               : ");
        int age = Integer.parseInt(sc.nextLine());

        if (age < 18) {
            System.out.println("❌ You must be 18 or older to register!");
            return;
        }

        System.out.print("Phone             : ");
        String phone = sc.nextLine();
        System.out.print("Voter ID          : ");
        String voterID = sc.nextLine();

        // Check duplicate voter ID
        for (Voter v : voters) {
            if (v.getVoterID().equals(voterID)) {
                System.out.println("❌ Voter ID already registered!");
                return;
            }
        }

        System.out.print("Aadhaar / PAN     : ");
        String document = sc.nextLine();
        System.out.print("Constituency ID   : ");
        String constituencyID = sc.nextLine();

        Voter voter = new Voter(name, age, phone, voterID, document, constituencyID);
        voters.add(voter);

        // Add voter to constituency
        if (election != null) {
            Constituency c = election.getConstituencyByID(constituencyID);
            if (c != null) c.addVoter(voterID);
        }

        System.out.println("✅ Voter registered successfully!");
        voter.getDetails();
    }

    static void voterLogin() {
        System.out.println("\n--- VOTER LOGIN ---");
        System.out.print("Enter Voter ID: ");
        String voterID = sc.nextLine();

        Voter loggedInVoter = null;
        for (Voter v : voters) {
            if (v.getVoterID().equals(voterID)) {
                loggedInVoter = v;
                break;
            }
        }

        if (loggedInVoter == null) {
            System.out.println("❌ Voter not found. Please register first.");
            return;
        }

        System.out.println("✅ Welcome, " + loggedInVoter.getName() + "!");
        voterDashboard(loggedInVoter);
    }

    static void voterDashboard(Voter voter) {
        while (true) {
            System.out.println("\n--- VOTER DASHBOARD ---");
            System.out.println("1. Cast Vote (EVM)");
            System.out.println("2. View My Details");
            System.out.println("3. View Candidates in My Constituency");
            System.out.println("4. Logout");
            System.out.print("Choice: ");

            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> {
                    if (!checkElectionSetup()) return;
                    evmVoting.startVoting(voter);
                }
                case "2" -> voter.getDetails();
                case "3" -> viewCandidatesForVoter(voter);
                case "4" -> { System.out.println("🔓 Voter logged out."); return; }
                default  -> System.out.println("❌ Invalid choice!");
            }
        }
    }

    static void viewCandidatesForVoter(Voter voter) {
        if (!checkElectionSetup()) return;
        ArrayList<Candidate> candidates =
                election.getCandidatesByConstituency(voter.getConstituencyID());

        if (candidates.isEmpty()) {
            System.out.println("No candidates found in your constituency.");
            return;
        }

        System.out.println("\n--- CANDIDATES IN YOUR CONSTITUENCY ---");
        for (Candidate c : candidates) {
            c.getDetails();
            System.out.println();
        }
    }

    // ─────────────────────────────────────────
    //              HELPER
    // ─────────────────────────────────────────

    static boolean checkElectionSetup() {
        if (election == null) {
            System.out.println("❌ Election not set up yet! Please setup election first.");
            return false;
        }
        return true;
    }
}