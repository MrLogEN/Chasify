package cz.charwot.chasify.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import cz.charwot.chasify.services.UserService;
import cz.charwot.chasify.utils.FXMLUtils;
import cz.charwot.chasify.utils.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @Autowired
    private UserService userService;

    @FXML
    private TextField emailField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label messageLabel;


    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        registerButton.setDisable(true);
        loginButton.setDisable(true);

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            FXMLUtils.setWarningMessage(messageLabel, "All fields are required.");
            return;
        }
        if (!password.equals(confirm)) {
            FXMLUtils.setWarningMessage(messageLabel, "Passwords do not match.");
            return;
        } 

        Task<Result<Boolean, String>> registerTask = new Task<>() {
            @Override
            protected Result<Boolean, String> call() {
                return userService.register(username, email, password);
            }
        };

        registerTask.setOnRunning(event -> {
            FXMLUtils.setInfoMessage(messageLabel, "Registrating user...");
        });

        registerTask.setOnSucceeded(event -> {
            var result = registerTask.getValue();
            registerButton.setDisable(false);
            loginButton.setDisable(false);

            if(result.isOk()) {
                FXMLUtils.switchViewWithStyles(
                (Stage) messageLabel.getScene().getWindow(),
                "/views/ProjectsView.fxml",
                "/css/styles.css");
            }
            else {
                FXMLUtils.setErrorMessage(messageLabel, result.unwrapErr());
            }
        });

        registerTask.setOnFailed(event -> {
            Throwable e = registerTask.getException();
            if (e != null) {
                logger.error("Register task failed: ", e);
            }
            FXMLUtils.setErrorMessage(messageLabel, "Failed to register!");

            loginButton.setDisable(false);
            registerButton.setDisable(false);

        });

        Thread taskThread = new Thread(registerTask);
        taskThread.setDaemon(true);
        taskThread.start();
    }

    @FXML
    private void handleLogin() {
        try {
            FXMLUtils.switchViewWithStyles(
                (Stage) messageLabel.getScene().getWindow(),
                "/views/LoginView.fxml",
                "/css/styles.css");

        }
        catch (Exception e) {
            logger.error("Could not load Login view.", e);
            messageLabel.setText("Could not load login screen.");
        }
    }
}

