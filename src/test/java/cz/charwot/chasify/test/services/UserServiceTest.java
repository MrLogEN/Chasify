package cz.charwot.chasify.test.services;

import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.UserRepository;
import cz.charwot.chasify.services.UserService;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.services.PasswordService;
import cz.charwot.chasify.utils.Result;
import jakarta.validation.constraints.AssertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordService passwordService;
	@InjectMocks
	private UserService userService;

	@BeforeEach
	public void setup() {
		if(UserSession.isActive()) {
			UserSession.end();
		}
	}

	@Test
	void testRegister_Success() {
		// Arrange
		
		String username = "testuser";
		String email = "test@example.com";
		String password = "Strong$Password123";

		//when(userRepository.findByEmail(email)).thenReturn(null);
		when(passwordService.validatePassword(password)).thenReturn(Result.ok(true));
		when(passwordService.hashPassword(password)).thenReturn("hashedPassword");
		// Act
		Result<Boolean, String> result = userService.register(username, email, password);
		
		// Assert
		assertTrue(result.isOk());
		verify(userRepository).create(any(User.class));
	}


	@Test
	void testRegister_BlankUsername() {
		// Act
		Result<Boolean, String> result = userService.register("", "test@example.com", "password");

		// Assert
		assertTrue(result.isErr());
		assertEquals("The username mustn't be blank!", result.unwrapErr());
		verifyNoInteractions(userRepository);
	}

	@Test
	void testRegister_InvalidEmail() {
		// Act
		Result<Boolean, String> result = userService.register("testuser", "invalid-email", "password");

		// Assert
		assertTrue(result.isErr());
		assertEquals("The email is invalid!", result.unwrapErr());
		verifyNoInteractions(userRepository);
	}

	@Test
	void testRegister_UserAlreadyExists() {
		// Arrange
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(new User());

		// Act
		Result<Boolean, String> result = userService.register("testuser", email, "password");

		// Assert
		assertTrue(result.isErr());
		assertEquals("User already exists.", result.unwrapErr());
		verify(userRepository, never()).create(any(User.class));
	}

	@Test
	void testAuthenticate_Success() {
		// Arrange
		String email = "test@example.com";
		String password = "password";
		User user = new User();
		user.setEmail(email);
		user.setPasswordHash("hashedPassword");

		when(userRepository.findByEmail(email)).thenReturn(user);
		when(passwordService.verifyPassword(password, "hashedPassword")).thenReturn(true);

		// Act
		Result<User, String> result = userService.authenticate(email, password);

		// Assert
		assertTrue(result.isOk());
		assertEquals(user, result.unwrap());
	}

	@Test
	void testAuthenticate_UserNotFound() {
		// Arrange
		String email = "test@example.com";
		when(userRepository.findByEmail(email)).thenReturn(null);

		// Act
		Result<User, String> result = userService.authenticate(email, "password");

		// Assert
		assertTrue(result.isErr());
		assertEquals("User does not exist!", result.unwrapErr());
	}

	@Test
	void testAuthenticate_InvalidPassword() {
		// Arrange
		String email = "test@example.com";
		String password = "wrongPassword";
		User user = new User();
		user.setEmail(email);
		user.setPasswordHash("hashedPassword");

		when(userRepository.findByEmail(email)).thenReturn(user);
		when(passwordService.verifyPassword(password, "hashedPassword")).thenReturn(false);

		// Act
		Result<User, String> result = userService.authenticate(email, password);

		// Assert
		assertTrue(result.isErr());
		assertEquals("Password invalid!", result.unwrapErr());
	}

	@Test
	void testLogout_UserAlreadyLoggedOut() {
		var result = userService.logout();

		assertTrue(result.isErr(), "User should not be able to log out when he is already logged out.");
		assertEquals("User is already logged out.", result.unwrapErr());

	}
	@Test
	void testLogout_UserLoggedOutSuccess() {
		User user = new User();
		user.setId(5);
		user.setEmail("alice@example.com");
		user.setUsername("Alice");

		UserSession.start(user);
		var result = userService.logout();

		assertTrue(result.isOk(), "User should able to log out.");
	}
}
