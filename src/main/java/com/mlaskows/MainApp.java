package com.mlaskows;

import com.mlaskows.dialog.DialogUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
                    LOG.error("Unexpected error", e);
                    final String message =
                            ((InvocationTargetException) e.getCause())
                                    .getTargetException()
                                    .getMessage();
                    DialogUtil.showError("Can't perform action", message);
                }
        );

        final Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/styles.css");

        final MainController controller = loader.getController();
        stage.addEventHandler(WindowEvent.WINDOW_SHOWING, w -> Platform
                .runLater(() -> controller.initMapCanvas()));
        stage.setTitle("Antsp");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.setMaximized(true);
        stage.show();
    }
}
