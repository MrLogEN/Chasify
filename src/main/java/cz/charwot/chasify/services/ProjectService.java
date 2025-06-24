package cz.charwot.chasify.services;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.ProjectRepository;
import cz.charwot.chasify.utils.Result;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void createProject(String name, String description, User owner, List<User> users) {
        Project p = new Project();
        p.setName(name);
        p.setDescription(description);
        p.setOwner(owner);
        p.setUsers(users);
        projectRepository.create(p);
    }

    public void updateProject(Project project) {
        projectRepository.update(project);
    }

    public void deleteProject(Project project) {
        projectRepository.delete(project);
    }

    public void deleteProjectById(int id) {
        projectRepository.deleteById(id);
    }

    public Project findProjectById(int id) {
        return projectRepository.findById(id);
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> findProjectsByOwner(User owner) {
        return projectRepository.findByOwner(owner);
    }

    public List<Project> findProjectsByUser(User user) {
        return projectRepository.findByUser(user);
    }

    public void addUserToProject(int projectId, User user) {
        projectRepository.addUserToProject(projectId, user);
    }

    public Result<List<Project>, String> findAllUserProjects(User user) {
        var byUser = projectRepository.findByUser(user);
        Set<Project> mergerdSet = new HashSet<>(byUser);
        mergerdSet.addAll(projectRepository.findByOwner(user));
        return Result.ok(new ArrayList<>(mergerdSet));
    }

    public Result<List<Project>, String> findAllUserProjectsWithoutSelf(User user, User self) {
        var byUser = projectRepository.findByUser(user);
        Set<Project> mergerdSet = new HashSet<>(byUser);
        mergerdSet.addAll(projectRepository.findByOwner(user));
        return Result.ok(new ArrayList<>(mergerdSet));
    }

    public void archiveProject(int id) {
        projectRepository.archive(id);
    }

    public void unarchiveProject(int id) {
        projectRepository.unarchive(id);
    }
}
