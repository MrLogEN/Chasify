package cz.charwot.chasify.controllers;

import java.time.Duration;

import org.springframework.stereotype.Controller;

import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

@Controller
public class BaseController {


    @FXML
    protected ImageView userIcon;

    @FXML
    protected Label userNameLabel;

    @FXML
    protected Button logoutButton;

    @FXML
    public void initialize() {

       if(UserSession.isActive()) {
            userNameLabel.setText(UserSession.getInstance().getUser().getUsername());
        }
        userIcon.setImage(new Image(getClass().getResource("/images/user.png").toExternalForm()));

    }


    @FXML
    private void handleLogout() {
        UserSession.end();
        FXMLUtils.switchViewWithStyles(
                (Stage) logoutButton.getScene().getWindow(),
                "/views/LoginView.fxml",
                "/css/styles.css");

    }

    public String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

