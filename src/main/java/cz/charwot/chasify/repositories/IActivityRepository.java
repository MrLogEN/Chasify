package cz.charwot.chasify.repositories;

import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.User;

import java.util.List;

public interface IActivityRepository {
    void create(Activity activity);
    Activity findById(Long id);
    List<Activity> findByTask(Task task);
    List<Activity> findByUser(User user);
    List<Activity> findAll();
    void update(Activity activity);
    void delete(Activity activity);
    void deleteById(Long id);
}

