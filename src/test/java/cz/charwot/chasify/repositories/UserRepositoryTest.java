package cz.charwot.chasify.repositories; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import cz.charwot.chasify.models.User;

import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class UserRepositoryTest {

	@Mock
	private EntityManagerFactory emf;

	@Mock
	private EntityManager em;

	@Mock
	private EntityTransaction tx;
	
	private UserRepository userRepository;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		when(emf.createEntityManager()).thenReturn(em);
		when(em.getTransaction()).thenReturn(tx);

		userRepository = new UserRepository(emf);
	}


	@Test
	public void createUser_shouldPersistUser() {
		User user = new User();

		userRepository.create(user);

		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(tx).begin();
		inOrder.verify(em).persist(user);
		inOrder.verify(tx).commit();
		inOrder.verify(em).close();

		verify(tx, never()).rollback();

	}

	@Test
	void createUser_shouldRollbackOnException() {
		User user = new User();
		doThrow(new RuntimeException("DB error")).when(em).persist(user);
		when(tx.isActive()).thenReturn(true);

		try {
			userRepository.create(user);
		}
		catch (Exception ignored){}

		verify(tx).rollback();
		verify(em).close();


	}

}
