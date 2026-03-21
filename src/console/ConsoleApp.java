package console;

import core.*;
import election.GeneralElection;
import persons.*;
import utils.Validator;
import voting.EVMVoting;

import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApp {

    static Scanner sc = new Scanner(System.in);

    static GeneralElection election = null;
    static EVMVoting evmVoting      = null;
    static ElectionOfficer admin    = null;
    static ArrayList<Voter> voters  = new ArrayList<>();

    static final String ADMIN_ID       = "ADMIN001";
    static final String ADMIN_PASSWORD = "admin@123";

    public static void main(String[] args) {
        admin = new ElectionOfficer("Chief Election Officer", 50,
                "9000000000", ADMIN_ID, ADMIN_PASSWORD);

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║      ELECTION SIMULATOR SYSTEM       ║");
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
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
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
                case "12" -> {
                    System.out.println("Admin logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void setupElection() {
        System.out.println("\n--- SETUP ELECTION ---");
        System.out.print("Election ID   : ");
        String id    = sc.nextLine();
        System.out.print("Election Name : ");
        String name  = sc.nextLine();
        System.out.print("Start Date    : ");
        String start = sc.nextLine();
        System.out.print("End Date      : ");
        String end   = sc.nextLine();

        election  = new GeneralElection(id, name, start, end);
        evmVoting = new EVMVoting(election, sc);
        System.out.println("Election setup complete!");
    }

    static void addConstituency() {
        if (!checkElectionSetup()) return;
        System.out.println("\n--- ADD CONSTITUENCY ---");
        System.out.print("Constituency ID   : ");
        String id   = sc.nextLine();
        System.out.print("Constituency Name : ");
        String name = sc.nextLine();

        // Duplicate check
        for (Constituency c : election.getConstituencies()) {
            if (c.getConstituencyID().equalsIgnoreCase(id)) {
                System.out.println("Constituency ID already exists!");
                return;
            }
        }

        election.addConstituency(new Constituency(id, name));
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

        // Duplicate check
        for (Party p : election.getParties()) {
            if (p.getPartyID().equalsIgnoreCase(id)) {
                System.out.println("Party ID already exists!");
                return;
            }
        }

        election.addParty(new Party(id, name, symbol));
    }

    static void addCandidate() {
        if (!checkElectionSetup()) return;
        System.out.println("\n--- ADD CANDIDATE ---");

        System.out.print("Name             : ");
        String name = sc.nextLine();

        // Name validation
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Only letters allowed (min 3 chars).");
            return;
        }

        System.out.print("Age              : ");
        int age;
        try { age = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) {
            System.out.println("Invalid age.");
            return;
        }
        if (!Validator.isValidAge(age)) {
            System.out.println("Age must be between 18 and 120.");
            return;
        }

        System.out.print("Phone            : ");
        String phone = sc.nextLine();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Must be 10 digits starting with 6-9.");
            return;
        }

        System.out.print("Candidate ID     : ");
        String candidateID = sc.nextLine();

        System.out.print("Voter ID         : ");
        String voterID = sc.nextLine();
        if (!Validator.isValidVoterID(voterID)) {
            System.out.println(Validator.voterIDError());
            return;
        }

        System.out.print("Aadhaar / PAN    : ");
        String document = sc.nextLine();
        if (!Validator.isValidDocument(document)) {
            System.out.println(Validator.documentError());
            return;
        }

        System.out.print("Party ID         : ");
        String partyID = sc.nextLine();

        // Party exists check
        boolean partyFound = false;
        for (Party p : election.getParties()) {
            if (p.getPartyID().equalsIgnoreCase(partyID)) {
                partyFound = true; break;
            }
        }
        if (!partyFound) {
            System.out.println("Party ID not found. Add party first.");
            return;
        }

        System.out.print("Constituency ID  : ");
        String constituencyID = sc.nextLine();

        // Constituency exists check
        Constituency constituency =
                election.getConstituencyByID(constituencyID);
        if (constituency == null) {
            System.out.println("Constituency ID not found. Add constituency first.");
            return;
        }

        // Create candidate
        Candidate newCandidate = new Candidate(name, age, phone,
                candidateID, voterID, document, partyID, constituencyID);

        ArrayList<Candidate> allCandidates = election.getCandidates();

        // ── Duplicate name check ───────────────
        if (DocumentVerifier.isDuplicateCandidateName(name, allCandidates)) {
            System.out.println("\nWARNING: Duplicate name detected: " + name);
            System.out.println("Dual verification required for BOTH candidates.");

            ArrayList<Candidate> duplicates =
                    DocumentVerifier.getDuplicateCandidates(name, allCandidates);
            Candidate existingCandidate = duplicates.get(0);

            // Re-verify existing candidate
            System.out.println("\nStep 1 of 2: Re-verify EXISTING candidate: "
                    + existingCandidate.getName()
                    + " (ID: " + existingCandidate.getCandidateID() + ")");
            System.out.print("   Enter their Voter ID    : ");
            String existingVoterID = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN : ");
            String existingDocument = sc.nextLine();

            // Verify new candidate
            System.out.println("\nStep 2 of 2: Verify NEW candidate: "
                    + newCandidate.getName()
                    + " (ID: " + newCandidate.getCandidateID() + ")");
            System.out.print("   Enter their Voter ID    : ");
            String newVoterID = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN : ");
            String newDocument = sc.nextLine();

            // Run dual verification
            String result = DocumentVerifier.dualVerifyCandidates(
                    existingCandidate, existingVoterID, existingDocument,
                    newCandidate, newVoterID, newDocument);

            switch (result) {
                case "FAILED" -> {
                    System.out.println("Dual verification FAILED.");
                    System.out.println("Documents do not match. Registration rejected.");
                    return;
                }
                case "SAME" -> {
                    System.out.println("Same person detected!");
                    System.out.println("Duplicate registration rejected.");
                    return;
                }
                case "EXISTING" -> {
                    System.out.println("Result: EXISTING candidate is the original.");
                    System.out.println("New candidate registration BLOCKED.");
                    return;
                }
                case "NEW" -> {
                    System.out.println("Result: NEW candidate is the original.");
                    System.out.println("Removing existing candidate...");

                    // Remove existing from election
                    election.getCandidates().remove(existingCandidate);

                    // Remove from constituency
                    Constituency existingConst = election.getConstituencyByID(
                            existingCandidate.getConstituencyID());
                    if (existingConst != null) {
                        existingConst.getCandidateIDs()
                                .remove(existingCandidate.getCandidateID());
                    }

                    // Remove from party
                    for (Party p : election.getParties()) {
                        p.getCandidateIDs()
                                .remove(existingCandidate.getCandidateID());
                    }

                    System.out.println("Existing candidate removed. Adding new candidate...");
                    // Fall through to add new candidate
                }
                case "BOTH" -> {
                    System.out.println("Result: BOTH candidates are genuine originals.");
                    System.out.println("New candidate approved.");
                    // Fall through to add new candidate
                }
            }
        }

        // Add candidate to election
        election.addCandidate(newCandidate);
        constituency.addCandidate(candidateID);

        for (Party p : election.getParties()) {
            if (p.getPartyID().equalsIgnoreCase(partyID)) {
                p.addCandidate(candidateID); break;
            }
        }
    }

    static void openElection() {
        if (!checkElectionSetup()) return;

        if (election.getCandidates().isEmpty()) {
            System.out.println("Add at least one candidate before opening election!");
            return;
        }
        if (election.getConstituencies().isEmpty()) {
            System.out.println("Add at least one constituency before opening election!");
            return;
        }

        election.openElection();
        election.conductElection();
    }

    static void closeElection() {
        if (!checkElectionSetup()) return;
        if (!election.isOpen()) {
            System.out.println("Election is already closed.");
            return;
        }
        election.closeElection();
    }

    static void announceResults() {
        if (!checkElectionSetup()) return;
        if (election.isOpen()) {
            System.out.println("Please close the election before announcing results!");
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
            System.out.println("EVM not initialized yet.");
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
        if (!Validator.isValidName(name)) {
            System.out.println("Invalid name. Only letters allowed (min 3 chars).");
            return;
        }

        System.out.print("Age               : ");
        int age;
        try { age = Integer.parseInt(sc.nextLine()); }
        catch (NumberFormatException e) {
            System.out.println("Invalid age.");
            return;
        }
        if (!Validator.isValidAge(age)) {
            System.out.println("You must be between 18 and 120 years old.");
            return;
        }

        System.out.print("Phone             : ");
        String phone = sc.nextLine();
        if (!Validator.isValidPhone(phone)) {
            System.out.println("Invalid phone. Must be 10 digits starting with 6-9.");
            return;
        }

        System.out.print("Voter ID          : ");
        String voterID = sc.nextLine();
        if (!Validator.isValidVoterID(voterID)) {
            System.out.println(Validator.voterIDError());
            return;
        }

        // Duplicate VoterID check
        for (Voter v : voters) {
            if (v.getVoterID().equalsIgnoreCase(voterID)) {
                System.out.println("Voter ID already registered!");
                return;
            }
        }

        System.out.print("Aadhaar / PAN     : ");
        String document = sc.nextLine();
        if (!Validator.isValidDocument(document)) {
            System.out.println(Validator.documentError());
            return;
        }

        System.out.print("Constituency ID   : ");
        String constituencyID = sc.nextLine();

        // Election check
        if (election == null) {
            System.out.println("No election scheduled yet. Contact admin.");
            return;
        }

        // Constituency check
        Constituency c = election.getConstituencyByID(constituencyID);
        if (c == null) {
            System.out.println("Constituency ID not found. Check with admin.");
            return;
        }

        // Create voter object
        Voter newVoter = new Voter(name, age, phone,
                voterID, document, constituencyID);

        // ── Duplicate voter NAME check ─────────
        if (DocumentVerifier.isDuplicateVoterName(name, voters)) {
            System.out.println("\nWARNING: Another voter with same name exists.");

            ArrayList<Voter> duplicates =
                    DocumentVerifier.getDuplicateVoters(name, voters);
            Voter existingVoter = duplicates.get(0);

            System.out.println("Existing voter: " + existingVoter.getName()
                    + " | Voter ID: " + existingVoter.getVoterID());
            System.out.println("Dual verification required for BOTH voters.");

            // Re-verify existing voter
            System.out.println("\nStep 1 of 2: Re-verify EXISTING voter: "
                    + existingVoter.getName());
            System.out.print("   Enter their Voter ID    : ");
            String exVoterID = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN : ");
            String exDocument = sc.nextLine();

            // Verify new voter
            System.out.println("\nStep 2 of 2: Verify NEW voter: " + name);
            System.out.print("   Enter their Voter ID    : ");
            String newVoterID2 = sc.nextLine();
            System.out.print("   Enter their Aadhaar/PAN : ");
            String newDocument = sc.nextLine();

            // Run dual verification
            String result = DocumentVerifier.dualVerifyVoters(
                    existingVoter, exVoterID, exDocument,
                    newVoter, newVoterID2, newDocument);

            switch (result) {
                case "FAILED" -> {
                    System.out.println("Dual verification FAILED.");
                    System.out.println("Documents do not match. Registration rejected.");
                    return;
                }
                case "SAME" -> {
                    System.out.println("Same person detected!");
                    System.out.println("You are already registered. Rejected.");
                    return;
                }
                case "EXISTING" -> {
                    System.out.println("Result: EXISTING voter is the original.");
                    System.out.println("New voter registration BLOCKED.");
                    return;
                }
                case "NEW" -> {
                    System.out.println("Result: NEW voter is the original.");
                    System.out.println("Removing existing voter...");

                    // Remove existing voter
                    voters.remove(existingVoter);

                    // Remove from constituency
                    Constituency existingConst = election.getConstituencyByID(
                            existingVoter.getConstituencyID());
                    if (existingConst != null) {
                        existingConst.getVoterIDs()
                                .remove(existingVoter.getVoterID());
                    }

                    System.out.println("Existing voter removed. Registering new voter...");
                    // Fall through to register new voter
                }
                case "BOTH" -> {
                    System.out.println("Result: BOTH voters are genuine originals.");
                    System.out.println("New voter registration approved.");
                    // Fall through to register new voter
                }
            }
        }

        // Register voter
        c.addVoter(voterID);
        voters.add(newVoter);
        System.out.println("Voter registered successfully!");
        newVoter.getDetails();
    }

    static void voterLogin() {
        System.out.println("\n--- VOTER LOGIN ---");
        System.out.print("Enter Voter ID: ");
        String voterID = sc.nextLine();

        // Format check
        if (!Validator.isValidVoterID(voterID)) {
            System.out.println(Validator.voterIDError());
            return;
        }

        // Election check
        if (election == null) {
            System.out.println("No election scheduled yet. Contact admin.");
            return;
        }

        // Find voter
        Voter loggedInVoter = null;
        for (Voter v : voters) {
            if (v.getVoterID().equalsIgnoreCase(voterID)) {
                loggedInVoter = v;
                break;
            }
        }

        if (loggedInVoter == null) {
            System.out.println("Voter not found. Please register first.");
            return;
        }

        // Constituency check
        Constituency c = election.getConstituencyByID(
                loggedInVoter.getConstituencyID());
        if (c == null) {
            System.out.println("Your constituency is not registered in this election.");
            return;
        }

        System.out.println("Welcome, " + loggedInVoter.getName() + "!");
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
                case "4" -> {
                    System.out.println("Voter logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice!");
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
            System.out.println("Election not set up yet! Please setup election first.");
            return false;
        }
        return true;
    }
}