package cz.charwot.chasify.test.services;

import cz.charwot.chasify.TestContainerConfig;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.UserRepository;
import cz.charwot.chasify.services.PasswordService;
import cz.charwot.chasify.services.UserService;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTest extends TestContainerConfig {

	private static UserService userService;
	private static EntityManagerFactory entityManagerFactory;

	@BeforeAll
	public static void setUp() {
		TestContainerConfig.configureSystemProperties();

		// Set up JPA EntityManagerFactory
		entityManagerFactory = Persistence.createEntityManagerFactory("chasify-test-pu");

		// Instantiate dependencies manually
		UserRepository userRepository = new UserRepository(entityManagerFactory);
		PasswordService passwordService = new PasswordService();

		// Create UserService instance
		userService = new UserService(userRepository, passwordService);
	}

	@Test
	public void testRegisterUser() {
		String username = "testuser";
		String email = "testuser@example.com";
		String password = "Password123!";

		Result<Boolean, String> result = userService.register(username, email, password);

		assertTrue(result.isOk(), "User registration should succeed");
		assertEquals(true, result.unwrap(), "Result should be true");
		assertTrue(UserSession.isActive());
		assertEquals(email, UserSession.getInstance().getUser().getEmail()); 

	}

	@Test
	public void testAuthenticateUser() {
		String email = "testuser@example.com";
		String password = "Password123!";

		Result<User, String> result = userService.authenticate(email, password);

		assertTrue(result.isOk(), "User authentication should succeed");
		assertNotNull(result.unwrap(), "Authenticated user should not be null");
		assertEquals(email, result.unwrap().getEmail(), "Email should match");
		assertTrue(UserSession.isActive());
		assertEquals(email, UserSession.getInstance().getUser().getEmail()); 
	}
	@Test
	public void testRegisterUserWithDuplicateEmail() {
		String username1 = "testuser1";
		String email = "duplicate@example.com";
		String password1 = "Password123!";
		String username2 = "testuser2";
		String password2 = "Password456!";

		// Register the first user
		Result<Boolean, String> result1 = userService.register(username1, email, password1);
		assertTrue(result1.isOk(), "First user registration should succeed");

		// Attempt to register a second user with the same email
		Result<Boolean, String> result2 = userService.register(username2, email, password2);

		assertFalse(result2.isOk(), "Second user registration with duplicate email should fail");
		assertEquals("User already exists.", result2.unwrapErr(), "Error message should indicate duplicate email");
		assertTrue(UserSession.isActive());
		assertEquals(email, UserSession.getInstance().getUser().getEmail()); 

	}

	@Test
	public void testAuthenticateUserWithInvalidPassword() {
		String email = "testuser@example.com";
		String invalidPassword = "WrongPassword123!";

		// Attempt to authenticate with an invalid password
		Result<User, String> result = userService.authenticate(email, invalidPassword);
		assertFalse(result.isOk(), "Authentication with invalid password should fail");
		assertEquals("Password invalid!", result.unwrapErr(), "Error message should indicate invalid credentials");
	}

	@Test
	public void testRegisterUserWithInvalidEmail() {
		String username = "testuser";
		String invalidEmail = "invalid-email";
		String password = "Password123!";

		// Attempt to register a user with an invalid email
		Result<Boolean, String> result = userService.register(username, invalidEmail, password);
		assertFalse(result.isOk(), "User registration with invalid email should fail");
		assertEquals("The email is invalid!", result.unwrapErr(), "Error message should indicate invalid email format");
	}

}

