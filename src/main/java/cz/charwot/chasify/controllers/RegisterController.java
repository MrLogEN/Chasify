package cz.charwot.chasify.controllers;

import java.util.concurrent.ExecutorCompletionService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;

import cz.charwot.chasify.utils.FXMLUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterController {

    private static Logger logger = LoggerFactory.getLogger(RegisterController.class);

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            messageLabel.setText("All fields are required.");
        } else if (!password.equals(confirm)) {
            messageLabel.setText("Passwords do not match.");
        } else {
            // Insert registration logic here
            messageLabel.setText("Registering...");
        }
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

