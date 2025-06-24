package cz.charwot.chasify.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.User;
import cz.charwot.chasify.services.ProjectService;
import cz.charwot.chasify.session.UserSession;
import cz.charwot.chasify.utils.FXMLUtils;
import cz.charwot.chasify.utils.Result;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

@Controller
public class ProjectsController extends BaseController {


    @Autowired
    private ProjectService projectService;

    @FXML
    private Button editButtonProjectButton;

    @FXML
    private Button enterButton;

    @FXML
    private ListView<Project> projectsList;

    @FXML
    private Label projectTitle;

    @FXML
    private TextArea projectDescription;

    @FXML
    private Button editProjectButton;

    @FXML
    private Label projectId;

    @FXML
    private Label projectOwner;

    @FXML
    private ListView<User> usersList;

    @FXML
    public void initialize() {
        super.initialize();
        FXMLUtils.setUserListCellFactory(usersList);
        FXMLUtils.setProjectsListCellFactory(projectsList);
        populateProjectsList();
        if(UserSession.isActive()) {
            userNameLabel.setText(UserSession.getInstance().getUser().getUsername());
        }
        userIcon.setImage(new Image(getClass().getResource("/images/user.png").toExternalForm()));

        // Handle project selection
        projectsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ObservableList<User> users = FXCollections.observableArrayList(newVal.getUsers());

                projectTitle.setText(newVal.getName());
                projectId.setText(String.valueOf(newVal.getId()));
                projectOwner.setText(newVal.getOwner().getUsername());
                editProjectButton.setDisable(true);
                if(UserSession.isActive() && newVal.getOwner().equals(UserSession.getInstance().getUser())){
                    editProjectButton.setDisable(false);
                }
                projectDescription.setText(newVal.getDescription());
                usersList.setItems(users);
            }
        });

    }

    @FXML 
    private void handleNew() {
        FXMLUtils.switchViewWithStyles(
            (Stage) projectId.getScene().getWindow(), 
            "/views/CreateProjectView.fxml",
            "/css/styles.css");
    }

    @FXML
    private void handleLogout() {
        UserSession.end();
        FXMLUtils.switchViewWithStyles(
                (Stage) logoutButton.getScene().getWindow(),
                "/views/LoginView.fxml",
                "/css/styles.css");

    }
    
    @FXML
    private void handleEdit() {
        String ids = projectId.getText();
        int id = Integer.parseInt(ids);
        var project = projectService.findProjectById(id);
        if(project != null) {
            FXMLUtils.switchViewWithStylesEditProject(
            (Stage) projectTitle.getScene().getWindow(),
                "/views/EditProjectView.fxml",
                "/css/styles.css",
                project);

        }
    }

    @FXML
    private void handleEnter() {
        String ids = projectId.getText();
        int id = Integer.parseInt(ids);
        Project p = projectService.findProjectById(id);
        if (p == null) {
            return;
        }

        FXMLUtils.switchViewWithStylesProjectTasks(
            (Stage)enterButton.getScene().getWindow(),
            "/views/ProjectTasksView.fxml",
            "/css/styles.css",
            p);
    }

    private void populateProjectsList() {
        if(!UserSession.isActive()) {
            return;
        }

        Result<List<Project>, String> result = projectService.findAllUserProjects(UserSession.getInstance().getUser());
        if(result.isOk()){
            var projects = result.unwrap();
            ObservableList<Project> observableProjects = FXCollections
                    .observableArrayList(projects);

            projectsList.setItems(observableProjects);
        }

    }

}

