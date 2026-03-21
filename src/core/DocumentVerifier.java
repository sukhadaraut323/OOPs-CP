package core;

import persons.Candidate;
import java.util.ArrayList;

public class DocumentVerifier {

    // Check if any existing candidate has the exact same full name
    public static boolean isDuplicateName(String newName, ArrayList<Candidate> existingCandidates) {
        for (Candidate c : existingCandidates) {
            if (c.getName().equalsIgnoreCase(newName)) {
                return true;
            }
        }
        return false;
    }

    // Find all candidates with the same name
    public static ArrayList<Candidate> getDuplicates(String name, ArrayList<Candidate> existingCandidates) {
        ArrayList<Candidate> duplicates = new ArrayList<>();
        for (Candidate c : existingCandidates) {
            if (c.getName().equalsIgnoreCase(name)) {
                duplicates.add(c);
            }
        }
        return duplicates;
    }

    // Called when a duplicate name is detected
    // BOTH the existing candidate AND the new candidate must be dual verified
    public static boolean dualVerifyBoth(
            Candidate existingCandidate,
            String existingVoterID, String existingDocument,
            Candidate newCandidate,
            String newVoterID, String newDocument) {

        System.out.println("⚠️  Duplicate name detected: " + newCandidate.getName());
        System.out.println("   Both candidates must be dual verified.");
        System.out.println();

        // --- Verify existing candidate ---
        System.out.println("🔍 Verifying EXISTING candidate: " + existingCandidate.getName()
                + " (ID: " + existingCandidate.getCandidateID() + ")");

        boolean existingVoterIDMatch   = existingCandidate.getVoterID().equals(existingVoterID);
        boolean existingDocumentMatch  = existingCandidate.getAadhaarOrPAN().equals(existingDocument);

        if (!existingVoterIDMatch || !existingDocumentMatch) {
            System.out.println("❌ Existing candidate verification FAILED!");
            System.out.println("   VoterID Match  : " + existingVoterIDMatch);
            System.out.println("   Document Match : " + existingDocumentMatch);
            System.out.println("   Existing candidate is flagged. Please verify manually.");
            return false;
        }
        System.out.println("✅ Existing candidate verified successfully!");
        existingCandidate.setDualVerified(true);

        System.out.println();

        // --- Verify new candidate ---
        System.out.println("🔍 Verifying NEW candidate: " + newCandidate.getName()
                + " (ID: " + newCandidate.getCandidateID() + ")");

        boolean newVoterIDMatch   = newCandidate.getVoterID().equals(newVoterID);
        boolean newDocumentMatch  = newCandidate.getAadhaarOrPAN().equals(newDocument);

        if (!newVoterIDMatch || !newDocumentMatch) {
            System.out.println("❌ New candidate verification FAILED!");
            System.out.println("   VoterID Match  : " + newVoterIDMatch);
            System.out.println("   Document Match : " + newDocumentMatch);
            System.out.println("   New candidate is rejected.");
            return false;
        }
        System.out.println("✅ New candidate verified successfully!");
        newCandidate.setDualVerified(true);

        System.out.println();

        // --- Cross check — are they actually the same person? ---
        boolean sameVoterID  = existingCandidate.getVoterID().equals(newCandidate.getVoterID());
        boolean sameDocument = existingCandidate.getAadhaarOrPAN().equals(newCandidate.getAadhaarOrPAN());

        if (sameVoterID || sameDocument) {
            System.out.println("❌ SAME PERSON detected! Both candidates share identical documents.");
            System.out.println("   Registration of new candidate REJECTED.");
            existingCandidate.setDualVerified(false); // reset existing too
            newCandidate.setDualVerified(false);
            return false;
        }

        System.out.println("✅ Both candidates are DIFFERENT people with the same name.");
        System.out.println("   Both are now dual-verified and allowed to contest.");
        return true;
    }

    // Display verification summary for any candidate
    public static void printVerificationSummary(Candidate candidate) {
        System.out.println("--- Verification Summary ---");
        System.out.println("Candidate   : " + candidate.getName());
        System.out.println("Candidate ID: " + candidate.getCandidateID());
        System.out.println("Voter ID    : " + candidate.getVoterID());
        System.out.println("Document    : " + candidate.getAadhaarOrPAN());
        System.out.println("Dual Verify : " + (candidate.isDualVerified() ? "✅ Verified" : "❌ Not Verified"));
    }
}