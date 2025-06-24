package cz.charwot.chasify;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import cz.charwot.chasify.utils.FXMLUtils;

@SpringBootApplication
public class App extends Application
{

    private static ConfigurableApplicationContext springContext;

    public static void main( String[] args )
    {
        springContext = new SpringApplicationBuilder(App.class).run();
        FXMLUtils.setApplicationContext(springContext);
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = FXMLUtils.getLoader("/views/LoginView.fxml");
        Parent root = loader.load();
        FXMLUtils.applyStylesheet(root, "/css/styles.css");

        stage.setTitle("Chasify - Login");
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();

    }

    @Override
    public void stop() {
        springContext.stop();
    }
}
/*@SpringBootApplication
public class App {

    private static ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        springContext = new SpringApplicationBuilder(App.class).run();
    }
}
*/
