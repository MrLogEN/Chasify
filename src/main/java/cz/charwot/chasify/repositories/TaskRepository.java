package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class TaskRepository implements ITaskRepository {

    private final EntityManagerFactory emf;
    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);

    public TaskRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Task task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(task);
            tx.commit();
            logger.debug("Task created: {}", task);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to create task!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Task task) throws IllegalArgumentException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Task existing = em.find(Task.class, task.getId());
            if (existing == null) {
                throw new IllegalArgumentException("Cannot update task: task does not exist!");
            }
            em.merge(task);
            tx.commit();
            logger.debug("Task updated: {}", task);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to update task!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Task task) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Task attachedTask = em.contains(task) ? task : em.merge(task);
            em.remove(attachedTask);
            tx.commit();
            logger.debug("Task deleted: {}", task);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to delete task!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) throws IllegalArgumentException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Task task = em.find(Task.class, id);
            if (task == null) {
                throw new IllegalArgumentException("Cannot delete task: task does not exist!");
            }
            em.remove(task);
            tx.commit();
            logger.debug("Task deleted by ID: {}", id);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to delete task by ID!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Task findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Task.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Task> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Task t", Task.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Task> findByProject(Project project) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Task t WHERE t.project = :project", Task.class)
                     .setParameter("project", project)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Task> findByAssignee(User assignee) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Task t WHERE t.assignee = :assignee", Task.class)
                     .setParameter("assignee", assignee)
                     .getResultList();
        } finally {
            em.close();
        }
    }
}

