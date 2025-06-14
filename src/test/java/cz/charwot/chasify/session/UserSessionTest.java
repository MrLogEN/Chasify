package cz.charwot.chasify.session;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import cz.charwot.chasify.models.User;

import static org.junit.jupiter.api.Assertions.*;

public class UserSessionTest {

	@AfterEach
	void tearDown() {
		if (isSessionActive()) {
			UserSession.end();
		}
	}

	private boolean isSessionActive() {
		try {
			UserSession.getInstance();
			return true;
		}
		catch (IllegalStateException e) {
			return false;
		}
	}

	@Test
	void startSession_shouldStoreUserCorrectly() {
		User user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");

		UserSession.start(user);

		assertTrue(isSessionActive());
		assertEquals(user, UserSession.getInstance().getUser());
	}

	@Test
	void startSession_shouldThrowWhenCalledTwice() {
		User user1 = new User();
		User user2 = new User();

		UserSession.start(user1);

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
			UserSession.start(user2);	
		});

		assertEquals("Session is already active.", ex.getMessage());
	}

	@Test
	void getInstance_withoutSessionShouldThrow() {
		assertThrows(IllegalStateException.class, UserSession::getInstance);
	}

	@Test
	void endSession_shouldClearInstance() {
		User user = new User();
		UserSession.start(user);

		UserSession.end();

		assertThrows(IllegalStateException.class, UserSession::getInstance);
	}

	@Test
	void isActive_shouldReflectSessionState() {
		assertFalse(isSessionActive());

		User user = new User();
		UserSession.start(user);

		assertTrue(isSessionActive());

		UserSession.end();

		assertFalse(isSessionActive());
	}


}
