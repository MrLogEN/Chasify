package cz.charwot.chasify.services;

import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.Status;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.ITaskRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class TaskService {

    private final ITaskRepository taskRepository;

    public TaskService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void createTask(String name, String desc, Status status, Project project, User assignee) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(desc);
        task.setStatus(status);
        task.setProject(project);
        taskRepository.create(task);
    }

    public void createTask(String name, String desc, Status status, Project project, User assignee, OffsetDateTime dueDate, Duration estimate) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(desc);
        task.setStatus(status);
        task.setProject(project);
        task.setDueDate(dueDate);
        task.setTimeEstimate(estimate);
        task.setAssignee(assignee);
        taskRepository.create(task);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public void deleteTaskById(int id) {
        taskRepository.deleteById(id);
    }

    public Task findTaskById(int id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> findTasksByProject(Project project) {
        return taskRepository.findByProject(project);
    }

    public List<Task> findTasksByAssignee(User assignee) {
        return taskRepository.findByAssignee(assignee);
    }

    public Duration getTotalTimeSpent(Task task) {
        task = taskRepository.findById(task.getId());
        var activities = task.getActivities();
        Duration totalTime = Duration.ZERO;
        for(Activity a: activities) {
            totalTime = totalTime.plus(Duration.between(a.getStartTime(), a.getEndTime()));
        }

        return totalTime;
    }

    public List<User> getAssigneeCandidates(Project project) {
        Set<User> candidates = new HashSet<User>(project.getUsers());
        candidates.add(project.getOwner());
        return new LinkedList<>(candidates);
    }

    public List<User> getAssigneeCandidatesWithoutAssignee(Project project, User assignee) {
        Set<User> candidates = new HashSet<User>(project.getUsers());
        candidates.add(project.getOwner());
        candidates.remove(assignee);
        return new LinkedList<>(candidates);
    }
}
