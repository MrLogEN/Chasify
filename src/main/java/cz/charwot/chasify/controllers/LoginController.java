package cz.charwot.chasify.controllers;

import cz.charwot.chasify.repositories.UserRepository;
import cz.charwot.chasify.services.PasswordService;
import cz.charwot.chasify.utils.HibernateUtil;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;

import cz.charwot.chasify.models.User;
import cz.charwot.chasify.services.UserService;
import cz.charwot.chasify.utils.FXMLUtils;
import cz.charwot.chasify.utils.Result;

@Controller
public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    public void initialize() {
        userService = new UserService(new UserRepository(HibernateUtil.getEntityManagerFactory()), new PasswordService());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        loginButton.setDisable(true);
        registerButton.setDisable(true);

        Task<Result<User, String>> loginTask = new Task<>() {
            @Override
            protected Result<User, String> call() {
                return userService.authenticate(email, password);
            }
        };

        loginTask.setOnRunning(event -> {
            FXMLUtils.setInfoMessage(messageLabel, "Logging in...");
        });

        loginTask.setOnSucceeded(event -> {
            var result = loginTask.getValue();

            loginButton.setDisable(false);
            registerButton.setDisable(false);
            if(result.isOk()) {
                FXMLUtils.setSuccessMessage(messageLabel, "Logged in");

                FXMLUtils.switchViewWithStyles(
                (Stage) messageLabel.getScene().getWindow(),
                    "/views/ProjectsView.fxml",
                    "/css/styles.css");
            }
            else {
                FXMLUtils.setErrorMessage(messageLabel, result.unwrapErr());
            }

        });

        loginTask.setOnFailed(event -> {
            Throwable e = loginTask.getException();
            if (e != null) {
                logger.error("Login task failed: ", e);
            }
            FXMLUtils.setErrorMessage(messageLabel, "Failed to login!");

            loginButton.setDisable(false);
            registerButton.setDisable(false);

        });

        Thread thread = new Thread(loginTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLUtils.switchViewWithStyles(
                (Stage) messageLabel.getScene().getWindow(),
                "/views/RegisterView.fxml",
                "/css/styles.css");
        }
        catch (Exception e) {
            logger.error("Could not load the register view!", e);
            messageLabel.setText("Could not load register screen.");
            FXMLUtils.setErrorMessage(messageLabel, "Could not load register screen.");
        }
    }
}

