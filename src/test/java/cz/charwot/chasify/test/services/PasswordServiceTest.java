package cz.charwot.chasify.test.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;

import cz.charwot.chasify.utils.Result;
import cz.charwot.chasify.services.PasswordService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
	public void validatePassword_shouldReturnAnErrorWhenPasswordIsNull() {
		String password = null;

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password cannot be null", result.unwrapErr());

	}

	@Test
	public void validatePassword_shouldReturnAnErrorWhenPasswordIsShorterThanEight() {
		String password = "aB$1342";

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password must be at least 8 characters", result.unwrapErr());
	}

	@Test
	public void validatePassword_shouldReturnAnErrorWhenItDoesNotContainACapitalLetter() {
		String password = "superstrongpassword7$";

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password must contain at least one uppercase letter", result.unwrapErr());

	}

	@Test
	public void validatePassword_shouldReturnAnErrorWhenItDoesNotContainALowerCaseLetter() {
		String password = "SUPERSTRONGPASSWORD7$";

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password must contain at least one lowercase letter", result.unwrapErr());

	}

	@Test
	public void validatePassword_shouldReturnAnErrorWhenItDoesNotContainANumber() {
		String password = "SUperSTRONGPASSWORD$";

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password must contain at least one number", result.unwrapErr());

	}

	@Test
	public void validatePassword_shouldReturnAnErrorWhenItDoesNotContainASpecialCharacter() {
		String password = "SUperSTRONGPASSWORD7";

		Result<Boolean, String> result = ps.validatePassword(password);
		assertTrue(result.isErr());
		assertEquals("Password must contain at least one special character", result.unwrapErr());

	}







}
