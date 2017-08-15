package com.mlaskows;

import com.mlaskows.tsplib.TspLibParser;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class TspFileHelper {

    public static Tsp getTsp() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter("TSP file (*.tsp)", "*.tsp");
        fileChooser.getExtensionFilters().add(extFilter);
        final File file = fileChooser.showOpenDialog(null);
        try {
            return TspLibParser.parse(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatComment(String comment, int maxCommentLen) {
        StringBuilder sb = new StringBuilder();
        int length = 0;
        for (String s : comment.split(" ")) {
            length += s.length();
            if (length >= maxCommentLen) {
                sb.append(System.getProperty("line.separator"));
                length = 0;
            }
            sb.append(" ");
            sb.append(s);
        }
        return sb.toString();
    }

}
