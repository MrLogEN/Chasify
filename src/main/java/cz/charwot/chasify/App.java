package cz.charwot.chasify;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import cz.charwot.chasify.utils.FXMLUtils;

public class App extends Application
{
    public static void main( String[] args )
    {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginView.fxml"));
        Parent root = loader.load();
        FXMLUtils.applyStylesheet(root, "/css/styles.css");

        stage.setTitle("Chasify - Login");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();

    }
}
