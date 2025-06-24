package cz.charwot.chasify.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import cz.charwot.chasify.controllers.CreateProjectTaskController;
import cz.charwot.chasify.controllers.CreateTaskActivityController;
import cz.charwot.chasify.controllers.EditProjectController;
import cz.charwot.chasify.controllers.EditProjectTaskController;
import cz.charwot.chasify.controllers.ProjectTaskController;
import cz.charwot.chasify.controllers.TaskActivitiesController;
import cz.charwot.chasify.models.Activity;
import cz.charwot.chasify.models.Project;
import cz.charwot.chasify.models.Task;
import cz.charwot.chasify.models.User;

public class FXMLUtils {

    private static Logger logger = LoggerFactory.getLogger(FXMLUtils.class);
    private static ApplicationContext ctx;

    private FXMLUtils(){}

    public static void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext;
    }

    /**
     * Loads a CSS stylesheet from the given path in resources and applies it to the provided root node.
     * 
     * @param root The root node to apply the stylesheet to
     * @param cssResourcePath The path to the CSS file in resources, e.g. "/css/styles.css"
     */
    public static void applyStylesheet(Parent root, String cssResourcePath) {
        if (root == null || cssResourcePath == null || cssResourcePath.isEmpty()) {
            throw new IllegalArgumentException("Root node and CSS path must not be null or empty");
        }

        String css = FXMLUtils.class.getResource(cssResourcePath).toExternalForm();
        root.getStylesheets().add(css);
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(fxmlPath));
        loader.setControllerFactory(ctx::getBean);
        return loader;
    }

    /**
     * Switches the current view by replacing the root node of the existing Scene.
     *
     * @param stage the main application stage
     * @param fxmlPath path to the FXML view, e.g., "/views/RegisterView.fxml"
     */
    public static void switchView(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

    /**
     * Switches the current view by replacing the root node of the existing Scene.
     *
     * @param stage the main application stage
     * @param fxmlPath path to the FXML view, e.g., "/views/RegisterView.fxml"
     */
    public static void switchViewWithStyles(Stage stage, String fxmlPath, String stylesPath) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, stylesPath);

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

    public static void switchViewWithDefaultStyles(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, "/css/styles.css");

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

    /**
     * Makes the text appear as an error.
     *
     * @param label an element that inherits {@link Labeled}
     * @param message a message to show to the user
     */
    public static void setErrorMessage(Labeled label, String message) {
        clearStatusClasses(label);
        label.getStyleClass().add("message-error-label");
        label.setText(message);
    }

    /**
     * Makes the text appear as a warning.
     *
     * @param label an element that inherits {@link Labeled}
     * @param message a message to show to the user
     */
    public static void setWarningMessage(Labeled label, String message) {
        clearStatusClasses(label);
        label.getStyleClass().add("message-warning-label");
        label.setText(message);
    }

    /**
     * Makes the text appear as an info.
     *
     * @param label an element that inherits {@link Labeled}
     * @param message a message to show to the user
     */
    public static void setInfoMessage(Labeled label, String message) {
        clearStatusClasses(label);
        label.getStyleClass().add("message-info-label");
        label.setText(message);
    }

    /**
     * Makes the text appear as a success.
     *
     * @param label an element that inherits {@link Labeled}
     * @param message a message to show to the user
     */
    public static void setSuccessMessage(Labeled label, String message) {
        clearStatusClasses(label);
        label.getStyleClass().add("message-success-label");
        label.setText(message);
    }

    public static void setUserListCellFactory(ListView<User> lw) {
        lw.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUsername() + " (" + user.getEmail() + ")");
                }
            }
        });
    }

    public static void setProjectsListCellFactory(ListView<Project> lw) {
        lw.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Project project, boolean empty) {
                super.updateItem(project, empty);
                if (empty || project == null) {
                    setText(null);
                } else {
                    setText(project.getId() + " - " + project.getName());
                }
            }
        });
    }

    public static void setTasksListCellFactory(ListView<Task> lw) {
        lw.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    setText(task.getId() + " - " + task.getName());
                }
            }
        });
    }

    private static void clearStatusClasses(Labeled label) {
        label.getStyleClass().removeAll("message-error-label");
        label.getStyleClass().removeAll("message-warnign-label");
        label.getStyleClass().removeAll("message-info-label");
        label.getStyleClass().removeAll("message-success-label");
    }
    public static void switchViewWithStylesEditProject(Stage stage, String fxmlPath, String stylesPath, Project project) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, stylesPath);

            Object controller = loader.getController();
            if(controller instanceof EditProjectController editProjectController) {
                editProjectController.sendProject(project);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }


    public static void switchViewWithStylesProjectTasks(Stage stage, String fxmlPath, String stylesPath, Project project) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, stylesPath);

            Object controller = loader.getController();
            if(controller instanceof ProjectTaskController projectTaskController) {
                projectTaskController.sendProject(project);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }
    public static void switchViewWithDefaultStylesCreateProjectTasks(Stage stage, String fxmlPath, Project project) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, "/css/styles.css");

            Object controller = loader.getController();
            if(controller instanceof CreateProjectTaskController createProjectTaskController) {
                createProjectTaskController.sendProject(project);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }
    public static void switchViewWithDefaultStylesEditProjectTasks(Stage stage, String fxmlPath, Task task) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, "/css/styles.css");

            Object controller = loader.getController();
            if(controller instanceof EditProjectTaskController editProjectTaskController) {
                editProjectTaskController.sendTask(task);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

    public static void setActivitiesListCellFactory(ListView<Activity> lw) {
        lw.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Activity activity, boolean empty) {
                super.updateItem(activity, empty);
                if (empty || activity == null) {
                    setText(null);
                } else {
                    setText(activity.getId() + " - " + activity.getDescription());
                }
            }
        });
    }
    public static void switchViewWithDefaultStylesTaskActivities(Stage stage, String fxmlPath, Task task) {
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, "/css/styles.css");

            Object controller = loader.getController();
            if(controller instanceof TaskActivitiesController activitiesController) {
                activitiesController.sendTask(task);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

    public static void switchViewWithDefaultStylesCreateTaskActivity(Stage stage, Task task) {
        String fxmlPath = "/views/CreateTaskActivity.fxml";
        try {
            FXMLLoader loader = getLoader(fxmlPath);
            Parent newRoot = loader.load();

            applyStylesheet(newRoot, "/css/styles.css");

            Object controller = loader.getController();
            if(controller instanceof CreateTaskActivityController createTaskActivityController) {
                createTaskActivityController.sendTask(task);
            }

            Scene scene = stage.getScene();

            if (scene == null) {
                // Fallback: create a new scene if none exists yet
                scene = new Scene(newRoot);
                stage.setScene(scene);
            } else {
                scene.setRoot(newRoot); // reuse existing scene to prevent resizing
            }

        } catch (Exception e) {
            logger.error("Failed to load FXML: {}", fxmlPath, e);
        }
    }

}
