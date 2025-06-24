package cz.charwot.chasify.controllers;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.services.ProjectService;
import cz.charwot.chasify.services.TaskService;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

@Controller
public class ProjectTaskController extends BaseController{


    @Autowired
    private TaskService taskService;
    
    @Autowired
    private ProjectService projectService;

    @FXML
    private Button backButton;

    @FXML
    private Button createButton;

    @FXML
    private ListView<Task> tasksList;

    @FXML
    private Label taskTitle;

    @FXML
    private Label taskId;

    @FXML
    private TextArea taskDescription; 

    @FXML
    private Label taskTimeSpent;

    @FXML
    private Label taskAssignee;

    @FXML
    private Label taskStatus;

    @FXML
    private Button enterButton;

    private Project project;

    private Task currentTask;

    @FXML
    public void initilize() {
        super.initialize();
    }

    public void sendProject(Project project) {
        this.project = projectService.findProjectById(project.getId());
        FXMLUtils.setTasksListCellFactory(tasksList);

        List<Task> tasks = taskService.findTasksByProject(project);
        ObservableList<Task> observableList = FXCollections.observableArrayList(tasks);

        tasksList.setItems(observableList);

        tasksList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {

                currentTask = newVal;
                taskTitle.setText(newVal.getName());
                taskId.setText(String.valueOf(newVal.getId()));
                taskAssignee.setText(newVal.getAssignee().getUsername());
                taskDescription.setText(newVal.getDescription());
                taskDescription.setDisable(true);
                taskStatus.setText(newVal.getStatus().toString());
                Duration timeSpent = taskService.getTotalTimeSpent(newVal);
                taskTimeSpent.setText(formatDuration(timeSpent));
            }
        });


    }
    @FXML
    private void handleEdit() {
        if(currentTask != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLUtils.switchViewWithDefaultStylesEditProjectTasks(
                stage,
                "/views/EditProjectTaskView.fxml",
                currentTask);
        }
    }
    @FXML void handleBack() {
        FXMLUtils.switchViewWithDefaultStyles(
            (Stage) backButton.getScene().getWindow(),
            "/views/ProjectsView.fxml"
        );
    }

    @FXML
    private void handleNew() {
        Stage stage = (Stage) taskTitle.getScene().getWindow();

        if (project != null) {
            FXMLUtils.switchViewWithDefaultStylesCreateProjectTasks(stage, "/views/CreateProjectTaskView.fxml", project);
        }
    }

    @FXML
    private void handleEnter() {
        if (currentTask != null) {
            Stage stage = (Stage) backButton.getScene().getWindow();
            FXMLUtils.switchViewWithDefaultStylesTaskActivities(
                stage,
                "/views/TaskActivitiesView.fxml", currentTask);
        }
    }
}
