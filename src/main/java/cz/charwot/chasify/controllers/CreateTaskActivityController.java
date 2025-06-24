package cz.charwot.chasify.controllers;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.services.ActivityService;
import cz.charwot.chasify.services.TaskService;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

@Controller
public class CreateTaskActivityController extends BaseController{
    @FXML private TextField activityName;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Label timerLabel;
    @FXML private Button submitButton;
    @FXML private Label messageLabel;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private TaskService taskService;

    private Timeline timeline;
    private long totalElapsedSeconds = 0;
    private OffsetDateTime currentStartTime;
    private OffsetDateTime firstStartTime;
    private OffsetDateTime lastStopTime;
    private boolean isRunning = false;

    private Task task;

    @FXML
    public void initialize() {
        stopButton.setDisable(true);
        setupTimerDisplay();
    }

    public void sendTask(Task task) {
        this.task = taskService.findTaskById(task.getId());
    }

    private void setupTimerDisplay() {
        timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), e -> updateTimerDisplay()));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void updateTimerDisplay() {
        if (isRunning && currentStartTime != null) {
            long currentSessionSeconds = ChronoUnit.SECONDS.between(currentStartTime, OffsetDateTime.now());
            long displaySeconds = totalElapsedSeconds + currentSessionSeconds;
            timerLabel.setText(formatTime(displaySeconds));
        }
    }

    private String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @FXML
    private void handleStart() {
        if (!isRunning) {
            currentStartTime = OffsetDateTime.now();
            if (firstStartTime == null) {
                firstStartTime = currentStartTime;
            }
            isRunning = true;
            timeline.play();

            startButton.setDisable(true);
            stopButton.setDisable(false);
            messageLabel.setText("Timer started");
        }
    }

    @FXML
    private void handleStop() {
        if (isRunning) {
            lastStopTime = OffsetDateTime.now();
            long sessionSeconds = ChronoUnit.SECONDS.between(currentStartTime, lastStopTime);
            totalElapsedSeconds += sessionSeconds;

            isRunning = false;
            timeline.stop();

            startButton.setDisable(false);
            stopButton.setDisable(true);
            messageLabel.setText("Timer stopped");

            // Update display with final time
            timerLabel.setText(formatTime(totalElapsedSeconds));
        }
    }

    @FXML
    private void handleCreate() {
        String name = activityName.getText().trim();
        if (name.isEmpty()) {
            FXMLUtils.setErrorMessage(messageLabel, "Please enter an activity name");
            return;
        }

        if (firstStartTime == null) {
            FXMLUtils.setErrorMessage(messageLabel, "Please start the timer first");
            return;
        }

        if (isRunning) {
            handleStop();
        }

        Stage stage = (Stage) messageLabel.getScene().getWindow();
        if(!UserSession.isActive()) {
            FXMLUtils.setErrorMessage(messageLabel, "You must be logged in!");
            FXMLUtils.switchViewWithDefaultStyles(stage, "/views/LoginView.fxml");
            return;
        }
        if (task == null) {
            FXMLUtils.setErrorMessage(messageLabel, "The activity must be assciated with a task.");
            return;
        }
        if(firstStartTime == null) {
            FXMLUtils.setErrorMessage(messageLabel, "The activity must start.");
            return;

        }
        if(lastStopTime == null) {
            FXMLUtils.setErrorMessage(messageLabel, "The activity must end.");
            return;

        }
        activityService.createActivity(
            name, 
            firstStartTime,
            lastStopTime, 
            UserSession.getInstance().getUser().getId(),
            task.getId());

        FXMLUtils.setSuccessMessage(messageLabel, "Activity created successfully");
        FXMLUtils.switchViewWithDefaultStylesTaskActivities(stage, "/views/TaskActivitiesView.fxml", task);
    }

    public OffsetDateTime getFirstStartTime() {
        return firstStartTime;
    }

    public OffsetDateTime getLastStopTime() {
        return lastStopTime;
    }

    public long getTotalElapsedSeconds() {
        return totalElapsedSeconds;
    }

    @FXML
    private void handleBack() {
        if(task != null) {
            task = taskService.findTaskById(task.getId());
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            FXMLUtils.switchViewWithDefaultStylesTaskActivities(stage, 
                "/views/TaskActivitiesView.fxml", task);
        }
    }
}
