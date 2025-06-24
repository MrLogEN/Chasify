package cz.charwot.chasify.controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.services.ActivityService;
import cz.charwot.chasify.services.TaskService;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@Controller
public class TaskActivitiesController extends BaseController{

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TaskService taskService;

    @FXML
    private Button createButton;

    @FXML
    private ListView<Activity> activitiesList;

    @FXML
    private TextField activityDescription;

    @FXML
    private Label spentTime;

    @FXML
    private Label createdAt;

    @FXML
    private Button deleteButton;

    private Task task;

    private Activity currentActivity;

    @FXML
    private void handleDelete(){
        if(currentActivity != null) {
            activityService.deleteActivity(currentActivity);
            task = taskService.findTaskById(task.getId());
            ObservableList<Activity> observableList = 
            FXCollections.observableArrayList(task.getActivities());
            activitiesList.setItems(observableList);
        }
    }

    @FXML
    private void handleNew() {
        if(task != null) {
            Stage stage = (Stage) createButton.getScene().getWindow();
            FXMLUtils.switchViewWithDefaultStylesCreateTaskActivity(stage, task);
        }
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) createButton.getScene().getWindow();
        FXMLUtils.switchViewWithStylesProjectTasks(
            stage,
            "/views/ProjectTasksView.fxml",
            "/css/styles.css",
            task.getProject());
    }

    @FXML
    public void initialize() {
        super.initialize();
        activitiesList.refresh();
    }

    public void sendTask(Task task) {
        this.task = taskService.findTaskById(task.getId());
        FXMLUtils.setActivitiesListCellFactory(activitiesList);
        ObservableList<Activity> observableList = 
        FXCollections.observableArrayList(task.getActivities());
        activitiesList.setItems(observableList);
        activitiesList.refresh();

        activitiesList.getSelectionModel().selectedItemProperty()
            .addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    currentActivity = newVal;
                    activityDescription.setText(newVal.getDescription());
                    Duration spent = Duration.between(
                        newVal.getStartTime(),
                        newVal.getEndTime());

                    spentTime.setText(formatDuration(spent));
                    createdAt.setText(newVal.getCreatedAt().toString());
                }
            });
    }
}
