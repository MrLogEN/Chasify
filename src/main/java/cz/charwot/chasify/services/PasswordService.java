package cz.charwot.chasify.services;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordService {

    public String hashPassword(String password) {


        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
    }

    public static void validatePassword(String password) throws IllegalArgumentException {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()_+=<>?/\\[\\]{}|~`-].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

}
