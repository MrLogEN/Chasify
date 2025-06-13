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
    public User findById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void update(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void deleteById(int id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

}
