package utils;

public class Validator {

    public static boolean isValidVoterID(String voterID) {
        if (voterID == null) return false;
        return voterID.matches("[A-Za-z0-9]{10}");
    }

    public static boolean isValidAadhaar(String aadhaar) {
        if (aadhaar == null) return false;
        return aadhaar.matches("[2-9][0-9]{11}");
    }

    public static boolean isValidPAN(String pan) {
        if (pan == null) return false;
        return pan.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    }

    public static boolean isValidDocument(String doc) {
        if (doc == null) return false;
        return isValidAadhaar(doc) || isValidPAN(doc.toUpperCase());
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return phone.matches("[6-9][0-9]{9}");
    }

    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 120;
    }

    public static boolean isValidName(String name) {
        if (name == null) return false;
        return name.matches("[A-Za-z ]{3,}");
    }

    public static String voterIDError() {
        return "Voter ID must be exactly 10 alphanumeric characters (e.g. ABC1234567)";
    }

    public static String documentError() {
        return "Enter valid Aadhaar (12 digits) or PAN (e.g. ABCDE1234F)";
    }
}