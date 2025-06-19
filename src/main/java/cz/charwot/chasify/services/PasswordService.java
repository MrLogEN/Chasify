package cz.charwot.chasify.services;

import org.mindrot.jbcrypt.BCrypt;

import cz.charwot.chasify.utils.Result;

public class PasswordService {

    public String hashPassword(String password) {


        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String password, String hashedPassword) {
            return BCrypt.checkpw(password, hashedPassword);
    }

    public Result<Boolean, String> validatePassword(String password) throws IllegalArgumentException {
        if (password == null) {
            return Result.err("Password cannot be null");
        }
        if (password.length() < 8) {
            return Result.err("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            return Result.err("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            return Result.err("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            return Result.err("Password must contain at least one number");
        }
        if (!password.matches(".*[!@#$%^&*()_+=<>?/\\[\\]{}|~`-].*")) {
            return Result.err("Password must contain at least one special character");
        }

        return Result.ok(true);
    }

}
