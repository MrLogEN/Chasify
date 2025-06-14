package cz.charwot.chasify.repositories; 

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import cz.charwot.chasify.models.User;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
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

		userRepository = new UserRepository(emf);
	}


	@Test
	public void createUser_shouldPersistUser() {
		User user = new User();
		when(em.getTransaction()).thenReturn(tx);

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
		when(em.getTransaction()).thenReturn(tx);
		doThrow(new RuntimeException("DB error")).when(em).persist(user);
		when(tx.isActive()).thenReturn(true);

		try {
			userRepository.create(user);
		}
		catch (Exception ignored){}

		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(tx).rollback();
		inOrder.verify(em).close();
	}

	@Test
	public void findById_shouldReturnAUser() {
		User user = new User();
		user.setId(1L);
		when(em.find(User.class, 1L)).thenReturn(user);

		User result = userRepository.findById(1L);

		assertEquals(1L, result.getId());
		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(em).find(User.class, 1L);
		inOrder.verify(em).close();
	}
	
	@Test
	public void findById_shouldReturnNullWhenTheUserDoesntExist() {
		when(em.find(User.class, 1L)).thenReturn(null);

		User result = userRepository.findById(1L);

		assertEquals(null, result);
		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(em).find(User.class, 1L);
		inOrder.verify(em).close();
	}

	@Test
	public void update_shouldUpdateTheUser() {
		User user = new User();
		user.setId(2L);
		user.setUsername("Alice");

		User userUpdate = new User();
		userUpdate.setId(2L);
		userUpdate.setUsername("Ben");

		when(em.getTransaction()).thenReturn(tx);
		when(em.find(User.class, 2L)).thenReturn(user);

		userRepository.update(userUpdate);

		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(tx).begin();
		inOrder.verify(em).merge(userUpdate);
		inOrder.verify(tx).commit();
		inOrder.verify(em).close();
	}

	@Test
	public void update_shouldThrowAnExceptionWhenTheUserDoesntExist() {
		User user = new User();
		user.setId(1L);
		when(em.getTransaction()).thenReturn(tx);
		when(em.find(User.class, 1L)).thenReturn(null);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
			userRepository.update(user);
		});

		assertEquals("Cannot update user: user does not exist!", thrown.getMessage());
	}

	@Test
	public void delete_shouldDeleteTheUser() {
		User user = new User();
		user.setId(3L);

		when(em.getTransaction()).thenReturn(tx);
		when(em.merge(user)).thenReturn(user);

		userRepository.delete(user);

		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(tx).begin();
		inOrder.verify(em).merge(user);
		inOrder.verify(em).remove(user);
		inOrder.verify(tx).commit();
		inOrder.verify(em).close();
	}

	@Test
	public void testDeleteById() {
		User user = new User();
		user.setId(4L);
		when(em.getTransaction()).thenReturn(tx);
		when(em.find(User.class, 4L)).thenReturn(user);

		userRepository.deleteById(4L);

		InOrder inOrder = inOrder(tx, em);
		inOrder.verify(tx).begin();
		inOrder.verify(em).find(User.class, 4L);
		inOrder.verify(em).remove(user);
		inOrder.verify(tx).commit();
		inOrder.verify(em).close();
	}

}
