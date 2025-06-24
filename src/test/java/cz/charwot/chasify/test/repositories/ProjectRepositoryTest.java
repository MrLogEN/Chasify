package cz.charwot.chasify.test.repositories;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjectRepositoryTest {

	@Mock private EntityManagerFactory emf;
	@Mock private EntityManager em;
	@Mock private EntityTransaction tx;

	@InjectMocks private ProjectRepository projectRepository;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		when(emf.createEntityManager()).thenReturn(em);
		when(em.getTransaction()).thenReturn(tx);
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	void create_shouldPersistProject() {
		Project project = new Project();

		projectRepository.create(project);

		verify(em).persist(project);
		verify(tx).begin();
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void create_shouldRollbackOnException() {
		Project project = new Project();
		when(tx.isActive()).thenReturn(true);
		doThrow(RuntimeException.class).when(em).persist(project);

		assertThrows(RuntimeException.class, () -> projectRepository.create(project));

		verify(tx).begin();
		verify(tx).rollback();
		verify(em).close();
	}

	@Test
	void findById_shouldReturnProject() {
		Project project = new Project();
		when(em.find(Project.class, 1)).thenReturn(project);

		Project result = projectRepository.findById(1);

		assertSame(project, result);
		verify(em).close();
	}

	@Test
	void update_shouldMergeExistingProject() {
		Project project = new Project();
		project.setId(1);
		when(em.find(Project.class, 1)).thenReturn(project);

		projectRepository.update(project);

		verify(em).merge(project);
		verify(tx).begin();
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void update_shouldThrowWhenProjectDoesNotExist() {
		Project project = new Project();
		project.setId(42);
		when(tx.isActive()).thenReturn(true);
		when(em.find(Project.class, 42)).thenReturn(null);
		when(tx.isActive()).thenReturn(true);

		IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
		() -> projectRepository.update(project));

		assertEquals("Cannot update project: project does not exist!", thrown.getMessage());
		verify(tx).rollback();
		verify(em).close();
	}

	@Test
	void delete_shouldRemoveMergedProject() {
		Project project = new Project();
		when(em.contains(project)).thenReturn(false);
		when(em.merge(project)).thenReturn(project);

		projectRepository.delete(project);

		verify(em).remove(project);
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void deleteById_shouldThrowIfNotFound() {
		when(em.find(Project.class, 10)).thenReturn(null);
		when(tx.isActive()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> projectRepository.deleteById(10));
		verify(tx).rollback();
		verify(em).close();
	}

	@Test
	void archive_shouldUpdateArchivedFlag() {
		Project project = new Project();
		project.setId(99);
		when(em.find(Project.class, 99)).thenReturn(project);

		projectRepository.archive(99);

		assertTrue(project.isArchived());
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void unarchive_shouldUpdateArchivedFlag() {
		Project project = new Project();
		project.setId(100);
		project.setArchived(true);
		when(em.find(Project.class, 100)).thenReturn(project);

		projectRepository.unarchive(100);

		assertFalse(project.isArchived());
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void addUserToProject_shouldAddUserWhenValid() {
		int projectId = 1;
		int userId = 42;

		User user = new User();
		user.setId(userId);

		Project project = new Project();
		project.setId(projectId);
		project.setUsers(new ArrayList<>());

		when(em.find(Project.class, projectId)).thenReturn(project);
		when(em.find(User.class, userId)).thenReturn(user);

		projectRepository.addUserToProject(projectId, user);

		verify(tx).begin();
		verify(tx).commit();
		assertTrue(project.getUsers().contains(user));
	}

	@Test
	void addUserToProject_shouldThrowWhenProjectNotFound() {
		int projectId = 1;
		int userId = 42;

		User user = new User();
		user.setId(userId);

		when(em.find(Project.class, projectId)).thenReturn(null);
		when(tx.isActive()).thenReturn(true);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
		() -> projectRepository.addUserToProject(projectId, user));

		verify(tx).begin();
		verify(tx).rollback();
		assertEquals("Project not found with ID: " + projectId, ex.getMessage());
	}

	@Test
	void addUserToProject_shouldThrowWhenUserNotFound() {
		when(tx.isActive()).thenReturn(true);
		int projectId = 1;
		int userId = 42;

		User user = new User();
		user.setId(userId);

		Project project = new Project();
		project.setId(projectId);
		project.setUsers(new ArrayList<>());

		when(em.find(Project.class, projectId)).thenReturn(project);
		when(em.find(User.class, userId)).thenReturn(null);

		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
		() -> projectRepository.addUserToProject(projectId, user));

		verify(tx).begin();
		verify(tx).rollback();
		assertEquals("User not found with ID: " + userId, ex.getMessage());
	}

	@Test
	void addUserToProject_shouldNotAddDuplicateUser() {
		int projectId = 1;
		int userId = 42;

		User user = new User();
		user.setId(userId);

		Project project = new Project();
		project.setId(projectId);
		ArrayList<User> users = new ArrayList<>();
		users.add(user);
		project.setUsers(users);

		when(em.find(Project.class, projectId)).thenReturn(project);
		when(em.find(User.class, userId)).thenReturn(user);

		projectRepository.addUserToProject(projectId, user);

		verify(tx).begin();
		verify(tx).commit();
		assertEquals(1, project.getUsers().size());  // Still one user
	}
}

