package cz.charwot.chasify.test.repositories;

import cz.charwot.chasify.models.*;
import cz.charwot.chasify.repositories.ActivityRepository;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ActivityRepositoryTest {

    private EntityManagerFactory emf;
    private EntityManager em;
    private EntityTransaction tx;
    private ActivityRepository repository;

    @BeforeEach
    void setUp() {
        emf = mock(EntityManagerFactory.class);
        em = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);

        when(emf.createEntityManager()).thenReturn(em);
        when(em.getTransaction()).thenReturn(tx);

        repository = new ActivityRepository(emf);
    }

    private Activity createDummyActivity() {
        Activity activity = new Activity();
        activity.setId(1L);
        activity.setDescription("Test Activity");
        activity.setCreatedAt(OffsetDateTime.now());
        activity.setStartTime(OffsetDateTime.now());
        activity.setEndTime(OffsetDateTime.now().plusHours(1));

        Task task = new Task();
        task.setId(1L);
        activity.setTask(task);

        User user = new User();
        user.setId(1L);
        activity.setUser(user);

        return activity;
    }

    @Test
    void create_shouldPersistActivity() {
        Activity activity = createDummyActivity();
        doNothing().when(em).persist(activity);

        repository.create(activity);

        verify(tx).begin();
        verify(em).persist(activity);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void findById_shouldReturnActivity() {
        Activity activity = createDummyActivity();
        when(em.find(Activity.class, 1L)).thenReturn(activity);

        Activity result = repository.findById(1L);

        assertEquals(activity, result);
        verify(em).close();
    }

    @Test
    void findByTask_shouldReturnList() {
        Task task = new Task();
        task.setId(1L);
        List<Activity> list = List.of(createDummyActivity());
        TypedQuery<Activity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Activity.class))).thenReturn(query);
        when(query.setParameter(eq("task"), eq(task))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        List<Activity> result = repository.findByTask(task);
        assertEquals(list, result);
    }

    @Test
    void findByUser_shouldReturnList() {
        User user = new User();
        user.setId(1L);
        List<Activity> list = List.of(createDummyActivity());
        TypedQuery<Activity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Activity.class))).thenReturn(query);
        when(query.setParameter(eq("user"), eq(user))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        List<Activity> result = repository.findByUser(user);
        assertEquals(list, result);
    }

    @Test
    void findAll_shouldReturnAllActivities() {
        List<Activity> list = List.of(createDummyActivity());
        TypedQuery<Activity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(Activity.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(list);

        List<Activity> result = repository.findAll();
        assertEquals(list, result);
    }

    @Test
    void update_shouldMergeActivity() {
        Activity activity = createDummyActivity();

        when(em.find(Activity.class, 1L)).thenReturn(activity);
        when(em.merge(activity)).thenReturn(activity);

        repository.update(activity);

        verify(tx).begin();
        verify(em).merge(activity);
        verify(tx).commit();
    }

    @Test
    void delete_shouldRemoveActivity() {
        Activity activity = createDummyActivity();
        when(em.merge(activity)).thenReturn(activity);

        repository.delete(activity);

        verify(tx).begin();
        verify(em).remove(activity);
        verify(tx).commit();
    }

    @Test
    void deleteById_shouldRemoveById() {
        Activity activity = createDummyActivity();
        when(em.find(Activity.class, 1L)).thenReturn(activity);

        repository.deleteById(1L);

        verify(tx).begin();
        verify(em).remove(activity);
        verify(tx).commit();
    }

    @Test
    void deleteById_shouldThrowIfNotFound() {
        when(em.find(Activity.class, 1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> repository.deleteById(1L));
    }

    @AfterEach
    void tearDown() {
        verify(em).close();
    }
}

