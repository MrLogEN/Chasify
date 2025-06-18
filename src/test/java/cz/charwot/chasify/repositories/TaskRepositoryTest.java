package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.*;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskRepositoryTest {

	@Mock
	private EntityManagerFactory emf;
	@Mock
	private EntityManager em;
	@Mock
	private EntityTransaction tx;

	private TaskRepository repository;

	private Task sampleTask;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		when(emf.createEntityManager()).thenReturn(em);
		when(em.getTransaction()).thenReturn(tx);
		repository = new TaskRepository(emf);

		Project project = new Project();
		project.setId(1L);
		User assignee = new User();
		assignee.setId(2L);

		sampleTask = new Task();
		sampleTask.setId(1L);
		sampleTask.setName("Task 1");
		sampleTask.setCreatedAt(OffsetDateTime.now());
		sampleTask.setProject(project);
		sampleTask.setAssignee(assignee);
	}

	@Test
	void create_shouldPersistTask() {
		repository.create(sampleTask);
		verify(tx).begin();
		verify(em).persist(sampleTask);
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void update_shouldMergeIfExists() {
		when(em.find(Task.class, sampleTask.getId())).thenReturn(sampleTask);
		repository.update(sampleTask);
		verify(tx).begin();
		verify(em).merge(sampleTask);
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void update_shouldThrowIfNotExists() {
		when(em.find(Task.class, sampleTask.getId())).thenReturn(null);
		when(tx.isActive()).thenReturn(true);
		assertThrows(IllegalArgumentException.class, () -> repository.update(sampleTask));
		verify(tx).rollback();
		verify(em).close();
	}

	@Test
	void delete_shouldRemoveTask() {
		when(em.contains(sampleTask)).thenReturn(true);
		repository.delete(sampleTask);
		verify(tx).begin();
		verify(em).remove(sampleTask);
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void deleteById_shouldRemoveIfExists() {
		when(em.find(Task.class, 1L)).thenReturn(sampleTask);
		repository.deleteById(1L);
		verify(tx).begin();
		verify(em).remove(sampleTask);
		verify(tx).commit();
		verify(em).close();
	}

	@Test
	void deleteById_shouldThrowIfNotFound() {
		when(em.find(Task.class, 1L)).thenReturn(null);
		when(tx.isActive()).thenReturn(true);
		assertThrows(IllegalArgumentException.class, () -> repository.deleteById(1L));
		verify(tx).rollback();
		verify(em).close();
	}

	@Test
	void findById_shouldReturnTask() {
		when(em.find(Task.class, 1L)).thenReturn(sampleTask);
		Task result = repository.findById(1L);
		assertEquals(sampleTask, result);
		verify(em).close();
	}

	@Test
	void findAll_shouldReturnAllTasks() {
		List<Task> tasks = List.of(sampleTask);
		TypedQuery<Task> query = mock(TypedQuery.class);
		when(em.createQuery("SELECT t FROM Task t", Task.class)).thenReturn(query);
		when(query.getResultList()).thenReturn(tasks);

		List<Task> result = repository.findAll();
		assertEquals(1, result.size());
		verify(em).close();
	}

	@Test
	void findByProject_shouldReturnMatchingTasks() {
		Project project = sampleTask.getProject();
		List<Task> tasks = List.of(sampleTask);
		TypedQuery<Task> query = mock(TypedQuery.class);
		when(em.createQuery("SELECT t FROM Task t WHERE t.project = :project", Task.class)).thenReturn(query);
		when(query.setParameter("project", project)).thenReturn(query);
		when(query.getResultList()).thenReturn(tasks);

		List<Task> result = repository.findByProject(project);
		assertEquals(1, result.size());
		verify(em).close();
	}

	@Test
	void findByAssignee_shouldReturnMatchingTasks() {
		User assignee = sampleTask.getAssignee();
		List<Task> tasks = List.of(sampleTask);
		TypedQuery<Task> query = mock(TypedQuery.class);
		when(em.createQuery("SELECT t FROM Task t WHERE t.assignee = :assignee", Task.class)).thenReturn(query);
		when(query.setParameter("assignee", assignee)).thenReturn(query);
		when(query.getResultList()).thenReturn(tasks);

		List<Task> result = repository.findByAssignee(assignee);
		assertEquals(1, result.size());
		verify(em).close();
	}
}

