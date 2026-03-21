package persons;

public class ElectionOfficer extends Person {
    private String officerID;
    private String password;

    public ElectionOfficer(String name, int age, String phoneNumber,
                           String officerID, String password) {
        super(name, age, phoneNumber);
        this.officerID = officerID;
        this.password = password;
    }

    public boolean login(String officerID, String password) {
        if (this.officerID.equals(officerID) && this.password.equals(password)) {
            System.out.println("✅ Admin login successful. Welcome, " + getName() + "!");
            return true;
        }
        System.out.println("❌ Invalid admin credentials!");
        return false;
    }

    public String getOfficerID() { return officerID; }

    @Override
    public void getDetails() {
        System.out.println("--- Election Officer ---");
        System.out.println("Name      : " + getName());
        System.out.println("Officer ID: " + officerID);
    }
}