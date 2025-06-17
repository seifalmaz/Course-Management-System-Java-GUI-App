import java.io.Serializable;

/**
 * User class representing a user with a username and password.
 * Provides authentication and basic user information management.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    /**
     * Constructor to initialize username and password.
     *
     * @param username The user's username.
     * @param password The user's password (in real applications, hash before
     * storing).
     * @throws IllegalArgumentException if username or password is null or
     * empty.
     */
    public User(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.username = username;
        this.password = password; // In real applications, hash the password before storing
    }

    /**
     * Authenticates the user by comparing provided credentials.
     *
     * @param username The username to check.
     * @param password The password to check.
     * @return true if credentials match, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        return this.username.equals(username) && this.password.equals(password);
    }

    /**
     * Gets the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets a new username.
     *
     * @param username The new username.
     * @throws IllegalArgumentException if username is null or empty.
     */
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        this.username = username;
    }

    /**
     * Sets a new password.
     *
     * @param password The new password (in real applications, hash before
     * storing).
     * @throws IllegalArgumentException if password is null or empty.
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password; // Fixed: Corrected the typo 'passwo\n        rd'
    }

    /**
     * Get the user's password.
     * 
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns a string representation of the User object for debugging/logging.
     *
     * @return A string containing the username.
     */
    @Override
    public String toString() {
        return "User{username='" + username + "'}";
    }

    /**
     * Hashes a password for demo purposes.
     *
     * @param password The password to hash.
     * @return A hexadecimal string representing the hash.
     * @note Use a secure hashing algorithm (e.g., bcrypt, Argon2) in
     * production.
     */
    public static String hashPassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return Integer.toHexString(password.hashCode()); // Use a secure hashing algorithm in production
    }
}
