package cz.charwot.chasify.utils;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FXMLUtils {

    private static Logger logger = LoggerFactory.getLogger(FXMLUtils.class);

    private FXMLUtils(){}

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

    /**
     * Switches the current view by replacing the root node of the existing Scene.
     *
     * @param stage the main application stage
     * @param fxmlPath path to the FXML view, e.g., "/views/RegisterView.fxml"
     */
    public static void switchView(Stage stage, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(fxmlPath));
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
            FXMLLoader loader = new FXMLLoader(FXMLUtils.class.getResource(fxmlPath));
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

}
