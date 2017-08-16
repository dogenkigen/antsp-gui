package com.mlaskows;

import com.mlaskows.tsplib.TspLibParser;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class TspFileHelper {

    public static Tsp getTsp() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter("TSP file (*.tsp)", "*.tsp");
        fileChooser.getExtensionFilters().add(extFilter);
        final File file = fileChooser.showOpenDialog(null);
        // TODO add behaviour when user escape durring file picking
        return TspLibParser.parse(file.getAbsolutePath());
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
            sb.append(s);
            sb.append(" ");
        }
        return sb.toString();
    }

}
