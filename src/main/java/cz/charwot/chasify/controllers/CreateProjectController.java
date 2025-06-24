package cz.charwot.chasify.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.User;
import cz.charwot.chasify.services.ProjectService;
import cz.charwot.chasify.services.UserService;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.FXMLUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

@Controller
public class CreateProjectController extends BaseController{

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;
    
    @FXML
    private TextField projectName;

    @FXML
    private TextArea projectDescription;

    @FXML   
    private ListView<User> usersList;

    @FXML   
    private ListView<User> usersListSelected;

    @FXML
    private Label messageLabel;

   @FXML
    private Button backButton;

    @FXML
    public void initialize() {
        super.initialize();
        FXMLUtils.setUserListCellFactory(usersList);
        FXMLUtils.setUserListCellFactory(usersListSelected);

        usersList.setOnMouseClicked(event -> moveItem(usersList, usersListSelected));
        usersListSelected.setOnMouseClicked(event -> moveItem(usersListSelected, usersList));

        if(UserSession.isActive()) {
            var users = userService.getAllUsersWithoutSelf(
                UserSession.getInstance().getUser()
            );
            ObservableList<User> obs = FXCollections
                .observableArrayList(users);
            usersList.setItems(obs);
            ObservableList<User> obs2 = FXCollections.observableArrayList();
            usersListSelected.setItems(obs2);
        }

    }

    @FXML
    private void handleCreate() {
        String name = projectName.getText();
        String desc = projectDescription.getText();
        List<User> users = usersListSelected.getItems();

        if(!UserSession.isActive()){
            FXMLUtils.setErrorMessage(messageLabel, "You must be logged in!");
            return;
        }
        if(name.isBlank()) {
            FXMLUtils.setErrorMessage(messageLabel, "Name cannot be blank!");
            return;
        }

        projectService.createProject(name, desc, UserSession.getInstance().getUser(), users);

        FXMLUtils.switchViewWithStyles(
            (Stage) messageLabel.getScene().getWindow(), 
            "/views/ProjectsView.fxml",
            "/css/styles.css");
    }
    
    @FXML
    private void handleBack() {
        FXMLUtils.switchViewWithStyles(
        (Stage) messageLabel.getScene().getWindow(),
            "/views/ProjectsView.fxml",
            "/css/styles.css");

    }

    private void moveItem(ListView<User> source, ListView<User> target) {
        User selectedUser = source.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            source.getItems().remove(selectedUser);
            target.getItems().add(selectedUser);
        }
    }

}

