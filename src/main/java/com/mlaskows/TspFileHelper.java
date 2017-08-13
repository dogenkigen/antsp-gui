package com.mlaskows;

import com.mlaskows.tsplib.TspLibParser;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

public class TspFileHelper {

    public static Tsp getTsp(Window primaryStage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter("TSP file (*.tsp)", "*.tsp");
        fileChooser.getExtensionFilters().add(extFilter);
        final File file = fileChooser.showOpenDialog(primaryStage);
        try {
            return TspLibParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
