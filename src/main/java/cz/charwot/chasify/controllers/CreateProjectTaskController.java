package cz.charwot.chasify.controllers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.Status;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.services.ProjectService;
import cz.charwot.chasify.services.TaskService;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Controller
public class CreateProjectTaskController extends BaseController{


    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    @FXML
    private TextField taskName;

    @FXML
    private TextArea taskDescription;


    public TaskService getTaskService() {
        return taskService;
    }

    public TextField getTaskName() {
        return taskName;
    }

    public TextArea getTaskDescription() {
        return taskDescription;
    }

    public ComboBox<Status> getStatusComboBox() {
        return statusComboBox;
    }

    public ListView<User> getUsersList() {
        return usersList;
    }

    public VBox getSelectedUserContainer() {
        return selectedUserContainer;
    }

    public Label getSelectedUserLabel() {
        return selectedUserLabel;
    }

    public DatePicker getDueDatePicker() {
        return dueDatePicker;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }

    public Spinner<Double> getTimeEstimateSpinner() {
        return timeEstimateSpinner;
    }

    public Project getProject() {
        return project;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    @FXML
    private ComboBox<Status> statusComboBox;

    @FXML
    private ListView<User> usersList;

    @FXML
    private VBox selectedUserContainer;

    @FXML
    private Label selectedUserLabel;

    @FXML
    private DatePicker dueDatePicker;

    @FXML
    private Label messageLabel;

    @FXML
    private Spinner<Double> timeEstimateSpinner;

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setTaskName(TextField taskName) {
        this.taskName = taskName;
    }

    public void setTaskDescription(TextArea taskDescription) {
        this.taskDescription = taskDescription;
    }

    public void setStatusComboBox(ComboBox<Status> statusComboBox) {
        this.statusComboBox = statusComboBox;
    }

    public void setUsersList(ListView<User> usersList) {
        this.usersList = usersList;
    }

    public void setSelectedUserContainer(VBox selectedUserContainer) {
        this.selectedUserContainer = selectedUserContainer;
    }

    public void setSelectedUserLabel(Label selectedUserLabel) {
        this.selectedUserLabel = selectedUserLabel;
    }

    public void setDueDatePicker(DatePicker dueDatePicker) {
        this.dueDatePicker = dueDatePicker;
    }

    public void setMessageLabel(Label messageLabel) {
        this.messageLabel = messageLabel;
    }

    public void setTimeEstimateSpinner(Spinner<Double> timeEstimateSpinner) {
        this.timeEstimateSpinner = timeEstimateSpinner;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    private Project project;

    private User selectedUser;

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public void sendProject(Project project) {
        this.project = projectService.findProjectById(project.getId());
        FXMLUtils.setUserListCellFactory(usersList);

        List<User> candidates = taskService.getAssigneeCandidates(project);

        setUserList(candidates);
    }

    public void setUserList(List<User> candidates) {
        ObservableList<User> observableCandidates = FXCollections
        .observableList(candidates);
        usersList.setItems(observableCandidates);

        usersList.setOnMouseClicked(event -> {
            if (selectedUser == null && !usersList.getSelectionModel().isEmpty()) {
                selectedUser = usersList.getSelectionModel().getSelectedItem();
                usersList.getItems().remove(selectedUser);
                reloadSelectedUserLabel();
            }
        });

        selectedUserContainer.setOnMouseClicked(event -> {
            if (selectedUser != null) {
                usersList.getItems().add(selectedUser);
                selectedUser = null;
                reloadSelectedUserLabel();
            }   
        });
    }

    public void reloadSelectedUserLabel() {
        if (selectedUser == null) {
            selectedUserLabel.setText("No user selected");
        }
        else {
            selectedUserLabel.setText(selectedUser.getUsername() + " (" + selectedUser.getEmail() + ")");
        }
    }

    @FXML
    public void initialize() {
        super.initialize();
        statusComboBox.getItems().addAll(Status.values());
        statusComboBox.setValue(Status.OPEN);
        SpinnerValueFactory<Double> valueFactory = 
            new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 1000, 1.0, 0.5);
        timeEstimateSpinner.setValueFactory(valueFactory);

        dueDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                ZonedDateTime utcNow = ZonedDateTime.now(ZoneId.of("UTC"));
                LocalDate utcToday = utcNow.toLocalDate();
                if (date.isBefore(utcToday)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #cccccc;");
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) messageLabel.getScene().getWindow(); 
        if(project != null) {
            FXMLUtils.switchViewWithStylesProjectTasks(
                stage,
                "/views/ProjectTasksView.fxml",
                "/css/styles.css",
                project);
        }
        else {
            FXMLUtils.switchViewWithDefaultStyles(stage, "/views/ProjectsView.fxml");
        }
    }

    @FXML
    private void handleCreate() {
        LocalDate dueDate = dueDatePicker.getValue();
        String name = taskName.getText();
        String desc = taskDescription.getText();
        Status status = statusComboBox.getValue();
        User assignee = selectedUser;
        double timeEstimate = timeEstimateSpinner.getValue();

        if (project == null) {
            FXMLUtils.setErrorMessage(messageLabel, "A task must be part of a projects!");
            return;
        }
         
        if(assignee == null) {
            FXMLUtils.setErrorMessage(messageLabel, "You must select an assignee!");
            return;
        }
        if(name == null || name.isBlank()) {
            FXMLUtils.setErrorMessage(messageLabel, "Name cannot be blank!");
            return;
        }

        OffsetDateTime offsetDueDate = null;
        if(dueDate != null) {
             offsetDueDate = dueDate.atTime(LocalTime.MIDNIGHT)
            .atOffset(ZoneOffset.UTC);
        }

        Duration estimate = Duration.ofMinutes((long) (timeEstimate * 60));

        taskService.createTask(name, desc, status, project, assignee, offsetDueDate, estimate);

        Stage stage = (Stage) timeEstimateSpinner.getScene().getWindow();

        FXMLUtils.switchViewWithStylesProjectTasks(
            stage,
            "/views/ProjectTasksView.fxml",
            "/css/styles.css",
            project);

    }


}
