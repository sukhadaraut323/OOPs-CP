package persons;

public abstract class Person {
    private String name;
    private int age;
    private String phoneNumber;

    public Person(String name, int age, String phoneNumber) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
    }

    // Getters
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPhoneNumber() { return phoneNumber; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // Abstract method
    public abstract void getDetails();
}