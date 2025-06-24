package cz.charwot.chasify.repositories;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;

import org.slf4j.Logger;

import jakarta.persistence.*;
import java.util.List;

@Repository
public class ProjectRepository implements IProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRepository.class);

    private final EntityManagerFactory emf;

    public ProjectRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void create(Project project) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(project);
            tx.commit();
            logger.debug("Project created: {}", project);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to create project!", e);
            throw e;
        }
        finally {
            em.close();
        }
    }

    @Override
    public void update(Project project) throws IllegalArgumentException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Project existing = em.find(Project.class, project.getId());
            if(existing == null) {
                throw new IllegalArgumentException("Cannot update project: project does not exist!");
            }
            em.merge(project);
            tx.commit();
            logger.debug("Project created: {}", project);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to update project!", e);
            throw e;
        }
        finally {
            em.close();
        }
    }

    @Override
    public void delete(Project project) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Project attachedProject = em.contains(project) ? project : em.merge(project);
            em.remove(attachedProject);
            em.merge(project);
            tx.commit();
            logger.debug("Project deleted: {}", project);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to delete project!", e);
            throw e;
        }
        finally {
            em.close();
        }

    }

    @Override
    public void deleteById(int id) throws IllegalArgumentException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Project existing = em.find(Project.class, id);
            if(existing == null) {
                throw new IllegalArgumentException("Cannot delete project: project does not exist!");
            }
            tx.commit();
            logger.debug("Project deleted by ID: {}, Project: {}", id, existing);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to delete project!", e);
            throw e;
        }
        finally {
            em.close();
        }

    }

    @Override
    public Project findById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Project.class, id);
        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Project> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Project> findByOwner(User owner) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p WHERE p.owner = :owner", Project.class)
            .setParameter("owner", owner)
            .getResultList();
        }
        finally {
            em.close();
        }
    }

    @Override
    public List<Project> findByUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Project p JOIN p.users u WHERE u = :user", Project.class)
            .setParameter("user", user)
            .getResultList();
        }
        finally {
            em.close();
        }
    }

    @Override
    public void addUserToProject(int projectId, User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Project project = em.find(Project.class, projectId);
            if (project == null) {
                throw new IllegalArgumentException("Project not found with ID: " + projectId);
            }

            User managedUser = em.find(User.class, user.getId());
            if (managedUser == null) {
                throw new IllegalArgumentException("User not found with ID: " + user.getId());
            }

            if (!project.getUsers().contains(managedUser)) {
                project.getUsers().add(managedUser);
            }

            tx.commit();
            logger.debug("User {} added to project {}", user.getId(), projectId);
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to add user {} to project {}", user.getId(), projectId, e);
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void archive(int id) {
        toggleArchive(id, true);
    }

    @Override
    public void unarchive(int id) {
        toggleArchive(id, false);
    }

    private void toggleArchive(int id, boolean archived) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Project project = em.find(Project.class, id);
            if (project == null) {
                throw new IllegalArgumentException("Project not found with ID: " + id);
            }
            project.setArchived(archived);
            tx.commit();
            logger.debug("Project {}archived: {}", archived ? "" : "un", project);
        }
        catch (Exception e) {
            if(tx.isActive()) {
                tx.rollback();
            }
            logger.error("Failed to (un)archive  project!", e);
            throw e;
        }
        finally {
            em.close();
        }
    }

}
