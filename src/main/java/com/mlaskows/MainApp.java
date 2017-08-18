package com.mlaskows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class MainApp extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/layout.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                    final String message =
                            ((InvocationTargetException) e.getCause())
                                    .getTargetException()
                                    .getMessage();
                    DialogUtil.showError("Can't perform action", message);
                }
        );

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        final Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Antsp");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
