package interfaces;

public interface Verifiable {
    boolean verifyIdentity(String voterID);
    boolean dualVerify(String voterID, String documentNumber); // for duplicate names
}