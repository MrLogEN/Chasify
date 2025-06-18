package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.User;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ActivityRepository implements IActivityRepository {

    private static final Logger logger = LoggerFactory.getLogger(ActivityRepository.class);
    private final EntityManagerFactory emf;

    public ActivityRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Activity activity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(activity);
            tx.commit();
            logger.debug("Activity created: {}", activity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Failed to create activity!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Activity findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Activity.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Activity> findByTask(Task task) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Activity a WHERE a.task = :task", Activity.class)
                     .setParameter("task", task)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Activity> findByUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Activity a WHERE a.user = :user", Activity.class)
                     .setParameter("user", user)
                     .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Activity> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT a FROM Activity a", Activity.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void update(Activity activity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Activity existing = em.find(Activity.class, activity.getId());
            if (existing == null) {
                throw new IllegalArgumentException("Cannot update activity: activity does not exist!");
            }
            em.merge(activity);
            tx.commit();
            logger.debug("Activity updated: {}", activity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Failed to update activity!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(Activity activity) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Activity managed = em.contains(activity) ? activity : em.merge(activity);
            em.remove(managed);
            tx.commit();
            logger.debug("Activity deleted: {}", activity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Failed to delete activity!", e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Activity activity = em.find(Activity.class, id);
            if (activity == null) {
                throw new IllegalArgumentException("Cannot delete activity: activity does not exist!");
            }
            em.remove(activity);
            tx.commit();
            logger.debug("Activity deleted by ID: {}, Activity: {}", id, activity);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            logger.error("Failed to delete activity by ID!", e);
            throw e;
        } finally {
            em.close();
        }
    }
}

