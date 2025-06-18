package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;

import java.util.List;

public interface ITaskRepository {
    void create(Task task);
    void update(Task task) throws IllegalArgumentException;
    void delete(Task task);
    void deleteById(Long id) throws IllegalArgumentException;
    Task findById(Long id);
    List<Task> findAll();
    List<Task> findByProject(Project project);
    List<Task> findByAssignee(User assignee);
}

