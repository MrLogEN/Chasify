package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;

import java.util.List;

public interface IProjectRepository {

       /**
     * Persists a new project.
     * @param project the project to create
     */
    void create(Project project);

    /**
     * Updates an existing project.
     * @param project the project to update
     * @throws IllegalArgumentException if the project does not exist
     */
    void update(Project project);

    /**
     * Deletes the given project.
     * @param project the project to delete
     */
    void delete(Project project);

    /**
     * Deletes a project by its ID.
     * @param id the ID of the project
     */
    void deleteById(int id);

    /**
     * Finds a project by its ID.
     * @param id the ID of the project
     * @return the matching project or null if not found
     */
    Project findById(int id);

    /**
     * Returns all projects in the system.
     * @return a list of all projects
     */
    List<Project> findAll();

    /**
     * Returns all projects owned by a specific user.
     * @param owner the user who owns the projects
     * @return a list of projects owned by the user
     */
    List<Project> findByOwner(User owner);

    /**
     * Returns all projects where the user is a participant.
     * @param user the user who is part of the project
     * @return a list of associated projects
     */
    List<Project> findByUser(User user);

    /**
     * Adds a user to the specified project.
     * @param projectId the id of a project to add a user to
     * @param user the user to be added to the project
     */
    void addUserToProject(int projectId, User user);

    /**
     * Archives a project by ID.
     * @param id the project ID
     * @throws IllegalArgumentException if the project does not exist
     */
    void archive(int id);

    /**
     * Unarchives a project by ID.
     * @param id the project ID
     * @throws IllegalArgumentException if the project does not exist
     */
    void unarchive(int id); 
}
