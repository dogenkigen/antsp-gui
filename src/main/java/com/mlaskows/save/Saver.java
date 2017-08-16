package com.mlaskows.save;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public abstract class Saver {

    public abstract void save() throws IOException;

    protected File getFile(String description, String extension) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter(description, extension);
        fileChooser.getExtensionFilters().add(extFilter);
        return fileChooser.showSaveDialog(null);
    }
}
