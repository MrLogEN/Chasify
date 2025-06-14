package cz.charwot.chasify.repositories;

import cz.charwot.chasify.utils.HibernateUtil;
import cz.charwot.chasify.models.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class UserRepository implements IUserRepository {

    private final EntityManagerFactory emf;
    private final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository(EntityManagerFactory entityManagerFactory) {
        this.emf = entityManagerFactory;
    }



    @Override
    public void create(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user);
            tx.commit();
            logger.debug("User created: {}", user);
        }
        catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to create user!", e);
            throw e;
        }
        finally {
            em.close();
        }

    }

    @Override
    public User findById(long id) {
        EntityManager em = emf.createEntityManager();
        try{
            return em.find(User.class, id);
        }
        finally {
            em.close();
        }
    }

    @Override
    public User findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em
            .createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email)
            .getSingleResultOrNull();
            return user;
        }
        finally {
            em.close();
        }

    }

    @Override
    public void update(User user) throws IllegalArgumentException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            User existing = em.find(User.class, user.getId());
            if(existing == null) {
                throw new IllegalArgumentException("Cannot update user: user does not exist!");
            }
            em.merge(user);
            tx.commit();
            logger.debug("User updated: {}", user);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }

            logger.error("Failed to update user!", e);
            throw e;
        }
        finally {
            em.close();
        }
    }

    @Override
    public void delete(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            User attachedUser = em.contains(user) ? user : em.merge(user);
            em.remove(attachedUser);
            tx.commit();
            logger.debug("User deleted: {}", user);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }

            logger.error("Failed to delete user!", e);
            throw e;
        }
        finally {
            em.close();
        }
    }

    @Override
    public void deleteById(long id) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            User user = em.find(User.class, id);
            if(user != null) {
                em.remove(user);
                logger.debug("User deleted by ID: {}\n, User: {}", id, user);
            }
            tx.commit();
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to delete user by ID!", e);
            throw e;
        }
        finally {
            em.close();
        }

    }

}
