package cz.charwot.chasify.services;

import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hibernate.grammars.hql.HqlParser.OffsetContext;
import org.slf4j.Logger;

import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.UserRepository;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.Result;
import jakarta.validation.ConstraintViolationException;

public class UserService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Result<Void, String> register(String username, String email, String password) {
        if(username == null || username.isBlank()) {
            logger.warn("The username mustn't be blank!");
            return Result.err("The username mustn't be blank!");
        }
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if(email == null || email.isBlank() || !email.matches(regex)) {
            logger.warn("The email is invalid!");
            return Result.err("The email is invalid!");
        }

        var passworValidationResult = passwordService.validatePassword(password);
        if(passworValidationResult.isErr()) {
            logger.warn(passworValidationResult.unwrapErr());
            return passworValidationResult;
        }

        User user = userRepository.findByEmail(email);
        if(user != null) {
            logger.warn("User with the email {} already exists.", email);
            return Result.err("User already exists.");
        }

        String passwordHash = passwordService.hashPassword(password);
        user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));

        try{
            userRepository.create(user);
        }
        catch (Exception e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                logger.warn("Duplicate email or username: {}", email);
                return Result.err("User already exists.");
            }

            logger.error("Unexpected error during user registration", e);
            return Result.err("Unexpected error occurred.");
        }

        if(UserSession.isActive()) {
            UserSession.end();
        }
        UserSession.start(user);

        logger.info("User {} registered", username);

        return Result.ok(null);

    }


}
