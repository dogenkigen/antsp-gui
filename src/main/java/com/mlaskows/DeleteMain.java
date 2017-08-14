package com.mlaskows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;


public class DeleteMain extends Application{


    public static void main(String[] args) {
        launch( args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/layout.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
/*
        MenuBar menuBar = new MenuBar ();
        final String os = System.getProperty ("os.name");
        if (os != null && os.startsWith ("Mac"))
            menuBar.useSystemMenuBarProperty ().set (true);

        BorderPane borderPane = new BorderPane ();
        borderPane.setTop (menuBar);

        primaryStage.setScene (new Scene (borderPane));*/

        stage.setTitle("FXML Welcome");
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }
}
