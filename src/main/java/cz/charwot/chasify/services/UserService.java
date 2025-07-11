package cz.charwot.chasify.services;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.slf4j.Logger;

import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.UserRepository;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.Result;
import jakarta.validation.ConstraintViolationException;

@Service
public class UserService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordService passwordService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
    }

    public Result<Boolean, String> register(String username, String email, String password) {
        if(username == null || username.isBlank()) {
            logger.warn("The username mustn't be blank!");
            return Result.err("The username mustn't be blank!");
        }
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if(email == null || email.isBlank() || !email.matches(regex)) {
            logger.warn("The email is invalid!");
            return Result.err("The email is invalid!");
        }

        User user = userRepository.findByEmail(email);
        if(user != null) {
            logger.warn("User with the email {} already exists.", email);
            return Result.err("User already exists.");
        }

        var passworValidationResult = passwordService.validatePassword(password);
        if(passworValidationResult.isErr()) {
            logger.warn(passworValidationResult.unwrapErr());
            return passworValidationResult;
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

        return Result.ok(true);

    }

    public Result<User, String> authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            logger.warn("User with email: {} does not exist!", email);
            return Result.err("User does not exist!");
        }

        boolean passwordMatches = passwordService.verifyPassword(password, user.getPasswordHash());
        if(!passwordMatches) {
            logger.warn("Password invalid!");
            return Result.err("Password invalid!");
        }

        if(UserSession.isActive()) {
            UserSession.end();
        }

        UserSession.start(user);
        logger.info("User {} logged in.", user.getUsername());

        user.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
        userRepository.update(user);

        return Result.ok(user);
    }

    public Result<Boolean, String> logout() {
        if(!UserSession.isActive()) {
            return Result.err("User is already logged out.");
        }

        UserSession.end();
        return Result.ok(true);
    }

    public List<User> getAllUsers(){
        var users = userRepository.getAll();
        return users;
    }

    public List<User> getAllUsersWithoutSelf(User self){
        var users = userRepository.getAll();
        users.remove(self);
        return users;
    }
    public List<User> getAllUsersWithoutList(List<User> list){
        var users = userRepository.getAll();
        if(list != null && !list.isEmpty()) {
            users.removeAll(list);
        }
        return users;
    }
}
