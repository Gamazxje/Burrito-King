package main.java.model;

public class User {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private boolean isVIP;
    private String email;
    private int credits;

    // Constructor for registration (default values for isVIP, email, and credits)
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isVIP = false;
        this.email = "";
        this.credits = 0;
    }

    // Constructor for full initialization
    public User(String username, String password, String firstName, String lastName, boolean isVIP, String email, int credits) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isVIP = isVIP;
        this.email = email;
        this.credits = credits;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public String getEmail() {
        return email;
    }

    public int getCredits() {
        return credits;
    }

    // Setters
    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addCredits(int credits) {
        this.credits += credits;
    }

    public void redeemCredits(int credits) {
        this.credits -= credits;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
   
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
