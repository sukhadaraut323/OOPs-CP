package core;

import persons.Candidate;
import persons.Voter;
import java.util.ArrayList;

public class DocumentVerifier {

    // ─────────────────────────────────────────
    //           CANDIDATE METHODS
    // ─────────────────────────────────────────

    public static boolean isDuplicateCandidateName(String newName,
            ArrayList<Candidate> existingCandidates) {
        for (Candidate c : existingCandidates) {
            if (c.getName().equalsIgnoreCase(newName)) return true;
        }
        return false;
    }

    public static ArrayList<Candidate> getDuplicateCandidates(String name,
            ArrayList<Candidate> existingCandidates) {
        ArrayList<Candidate> duplicates = new ArrayList<>();
        for (Candidate c : existingCandidates) {
            if (c.getName().equalsIgnoreCase(name)) duplicates.add(c);
        }
        return duplicates;
    }

    // Returns:
    //  "BOTH"      → both original, different people, add new candidate
    //  "EXISTING"  → only existing is original, block new
    //  "NEW"       → only new is original, remove existing add new
    //  "SAME"      → same person trying to register twice, block
    //  "FAILED"    → document verification failed for one or both
    public static String dualVerifyCandidates(
            Candidate existingCandidate,
            String existingVoterID, String existingDocument,
            Candidate newCandidate,
            String newVoterID, String newDocument) {

        // ── Step 1: Verify each against their own submitted docs ───
        boolean existingVerified =
                existingCandidate.getVoterID().equals(existingVoterID)
                && existingCandidate.getAadhaarOrPAN().equals(existingDocument);

        boolean newVerified =
                newCandidate.getVoterID().equals(newVoterID)
                && newCandidate.getAadhaarOrPAN().equals(newDocument);

        // ── Step 2: Both must verify first ────────────────────────
        if (!existingVerified && !newVerified) {
            return "FAILED"; // neither could verify
        }
        if (!existingVerified) {
            // Existing failed verification → new is original
            newCandidate.setDualVerified(true);
            return "NEW";
        }
        if (!newVerified) {
            // New failed verification → existing is original
            existingCandidate.setDualVerified(true);
            return "EXISTING";
        }

        // ── Step 3: Both verified, now cross check ─────────────────
        boolean sameVoterID =
                existingCandidate.getVoterID().equals(newCandidate.getVoterID());
        boolean sameDocument =
                existingCandidate.getAadhaarOrPAN()
                        .equals(newCandidate.getAadhaarOrPAN());

        if (sameVoterID && sameDocument) {
            return "SAME"; // exact same person
        }

        if (sameVoterID || sameDocument) {
            // One document matches — partial overlap
            // Existing registered first so existing is trusted original
            existingCandidate.setDualVerified(true);
            return "EXISTING";
        }

        // ── Step 4: Completely different docs — both are originals ──
        existingCandidate.setDualVerified(true);
        newCandidate.setDualVerified(true);
        return "BOTH";
    }

    // ─────────────────────────────────────────
    //           VOTER METHODS
    // ─────────────────────────────────────────

    public static boolean isDuplicateVoterName(String newName,
            ArrayList<Voter> existingVoters) {
        for (Voter v : existingVoters) {
            if (v.getName().equalsIgnoreCase(newName)) return true;
        }
        return false;
    }

    public static ArrayList<Voter> getDuplicateVoters(String name,
            ArrayList<Voter> existingVoters) {
        ArrayList<Voter> duplicates = new ArrayList<>();
        for (Voter v : existingVoters) {
            if (v.getName().equalsIgnoreCase(name)) duplicates.add(v);
        }
        return duplicates;
    }

    // Returns:
    //  "BOTH"      → both original, different people, add new voter
    //  "EXISTING"  → only existing is original, block new
    //  "NEW"       → only new is original, remove existing add new
    //  "SAME"      → same person trying to register twice, block
    //  "FAILED"    → document verification failed for one or both
    public static String dualVerifyVoters(
            Voter existingVoter,
            String existingVoterID, String existingDocument,
            Voter newVoter,
            String newVoterID, String newDocument) {

        // ── Step 1: Verify each against their own submitted docs ───
        boolean existingVerified =
                existingVoter.getVoterID().equals(existingVoterID)
                && existingVoter.getAadhaarOrPAN().equals(existingDocument);

        boolean newVerified =
                newVoter.getVoterID().equals(newVoterID)
                && newVoter.getAadhaarOrPAN().equals(newDocument);

        // ── Step 2: Handle individual failures ────────────────────
        if (!existingVerified && !newVerified) {
            return "FAILED";
        }
        if (!existingVerified) {
            // Existing voter's docs don't match → new is original
            return "NEW";
        }
        if (!newVerified) {
            // New voter's docs don't match → existing is original
            return "EXISTING";
        }

        // ── Step 3: Both verified, cross check ────────────────────
        boolean sameVoterID =
                existingVoter.getVoterID().equals(newVoter.getVoterID());
        boolean sameDocument =
                existingVoter.getAadhaarOrPAN()
                        .equals(newVoter.getAadhaarOrPAN());

        if (sameVoterID && sameDocument) {
            return "SAME";
        }

        if (sameVoterID || sameDocument) {
            // Partial overlap → existing registered first → existing trusted
            return "EXISTING";
        }

        // ── Step 4: Completely different → both are originals ──────
        return "BOTH";
    }

    // ── Legacy compatibility methods ──────────
    public static boolean isDuplicateName(String newName,
            ArrayList<Candidate> existingCandidates) {
        return isDuplicateCandidateName(newName, existingCandidates);
    }

    public static ArrayList<Candidate> getDuplicates(String name,
            ArrayList<Candidate> existingCandidates) {
        return getDuplicateCandidates(name, existingCandidates);
    }
}