package cz.charwot.chasify.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Project;
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
public class EditProjectController extends BaseController{

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
    private Button submitButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Button backButton;

    private Project project;

    @FXML
    public void initialize() {
        super.initialize();
        projectName.setDisable(true);
        FXMLUtils.setUserListCellFactory(usersList);
        FXMLUtils.setUserListCellFactory(usersListSelected);

        usersList.setOnMouseClicked(event -> moveItem(usersList, usersListSelected));
        usersListSelected.setOnMouseClicked(event -> moveItem(usersListSelected, usersList));

    }

    @FXML
    private void handleEdit() {
        String desc = projectDescription.getText();
        List<User> users = usersListSelected.getItems();

        if(!UserSession.isActive()){
            FXMLUtils.setErrorMessage(messageLabel, "You must be logged in!");
            return;
        }

        project.setDescription(desc);
        project.setUsers(users != null ? users : new ArrayList<>());

        projectService.updateProject(project);

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

    public void sendProject(Project project) {
        this.project = projectService.findProjectById(project.getId());
        if(UserSession.isActive()) {

            var removeUsers = project.getUsers();
            removeUsers.add(project.getOwner());

            var users = userService.getAllUsersWithoutList(
                removeUsers
            );

            projectName.setText(project.getName());
            projectDescription.setText(project.getDescription());

            ObservableList<User> availableUsers = FXCollections
            .observableArrayList(users);
            usersList.setItems(availableUsers);

            ObservableList<User> selectedUsers = FXCollections.observableArrayList(
                project.getUsers().stream()
                .filter(u -> !u.equals(this.project.getOwner()))
                .toList()
            );
            usersListSelected.setItems(selectedUsers);
        }
    }

    private void moveItem(ListView<User> source, ListView<User> target) {
        User selectedUser = source.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            source.getItems().remove(selectedUser);
            target.getItems().add(selectedUser);
        }
    }

}

