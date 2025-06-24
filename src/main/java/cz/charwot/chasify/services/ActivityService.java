package cz.charwot.chasify.services;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.repositories.ActivityRepository;
import cz.charwot.chasify.repositories.TaskRepository;
import cz.charwot.chasify.repositories.UserRepository;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ActivityService(ActivityRepository activityRepository, UserRepository userRepository, TaskRepository taskRepository){
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public void deleteActivity(Activity activity) {
        activityRepository.delete(activity);
    }

    public void createActivity(String description, OffsetDateTime start, OffsetDateTime stop, int userId, int taskId) {
        User user = userRepository.findById(userId);
        Task task = taskRepository.findById(taskId);
        Activity activity = new Activity();
        activity.setDescription(description);
        activity.setStartTime(start);
        activity.setEndTime(stop);
        activity.setUser(user);
        activity.setTask(task);
        activityRepository.create(activity);
    }
}
