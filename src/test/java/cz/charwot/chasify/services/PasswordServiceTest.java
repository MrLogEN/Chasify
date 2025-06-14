package cz.charwot.chasify.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class PasswordServiceTest {

	private PasswordService ps;

	@BeforeEach
	public void setup() {
		this.ps = new PasswordService();
	}


	@Test
	public void hashPassword_shoulReturnPasswordHash() {

		String password = "super_secret_password";
		
		String hash = ps.hashPassword(password);

		assertNotEquals(password, hash);
	}

	@Test
	public void verifyPassword_shouldReturnTrue() {
		String hash = "$2a$10$iAangSZBw3q8Yq8IWZU5duW6oQqsLidA1mKzJnqas5vHNC7BMjOhK";
		String password = "super_secret_password";

		assertTrue(ps.verifyPassword(password, hash));
	}

	@Test
	public void verifyPassword_shouldReturnFalse() {
		String randomHash = "$2a$10$iAangSZBw3q8Yq8IWZU5duW6oQqsLhdA1mKzJnqas5vHNC7BMjOhK";
		String password = "super_secret_password";

		assertFalse(ps.verifyPassword(password, randomHash));
	}

	@Test
	public void validatePassword_shouldPass() {
		String password = "SuperStrongPassword$7";

		assertDoesNotThrow(() -> ps.validatePassword(password));
	}


	@Test
	public void validatePassword_shouldThrowAnExceptionWhenPasswordIsNull() {
		String password = null;

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));

	}

	@Test
	public void validatePassword_shouldThrowAnExceptionWhenPasswordIsShorterThanEight() {
		String password = "aB$1342";

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));

	}

	@Test
	public void validatePassword_shouldThrowAnExceptionWhenItDoesNotContainACapitalLetter() {
		String password = "superstrongpassword7$";

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));
	}

	@Test
	public void validatePassword_shouldThrowAnExceptionWhenItDoesNotContainALowerCaseLetter() {
		String password = "SUPERSTRONGPASSWORD7$";

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));
	}

	@Test
	public void validatePassword_shouldThrowAnExceptionWhenItDoesNotContainANumber() {
		String password = "SUPERSTRONGPASSWORD7$";

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));
	}

	@Test
	public void validatePassword_shouldThrowAnExceptionWhenItDoesNotContainASpecialCharacter() {
		String password = "SUPERSTRONGPASSWORD7";

		assertThrows(IllegalArgumentException.class, () -> ps.validatePassword(password));
	}







}
