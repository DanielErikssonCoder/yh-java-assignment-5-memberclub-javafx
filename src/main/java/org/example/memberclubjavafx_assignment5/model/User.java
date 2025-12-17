package org.example.memberclubjavafx_assignment5.model;

/**
 * Represents a system user with authentication credentials.
 * Users can log in to the rental system using username and password.
 */
public class User {

    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;

    /**
     * Creates a new user with the specified credentials.
     * @param username the unique username for login
     * @param password the password for authentication
     * @param firstName the user's first name for display purpose
     * @param lastName the user's last name for display purpose
     */
    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Validates if the provided password matches this user's password.
     * @param inputPassword the password to validate
     * @return true if the password is a match
     */
    public boolean validatePassword(String inputPassword) {
        return inputPassword.equals(this.password);
    }
}