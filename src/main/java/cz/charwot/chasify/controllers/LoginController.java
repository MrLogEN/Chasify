package cz.charwot.chasify.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import cz.charwot.chasify.utils.FXMLUtils;

public class LoginController {

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in all fields.");
        } else {
            // Insert real auth logic here
            messageLabel.setText("Attempting to log in...");
        }
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
        }
    }
}

