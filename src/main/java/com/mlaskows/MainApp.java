package com.mlaskows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class MainApp extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        LOG.info("Starting Hello JavaFX and Maven demonstration application");

        String fxmlFile = "/fxml/hello.fxml";
        LOG.debug("Loading FXML for main view from: {}", fxmlFile);
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        LOG.debug("Showing JFX scene with height " + height + " and width " +
                width);
        Scene scene = new Scene(rootNode, width, height);
        //scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Antsp");
        stage.setScene(scene);
        stage.show();

        final MainController controller = loader.getController();
        controller.setPrimaryStage(stage);
    }
}
