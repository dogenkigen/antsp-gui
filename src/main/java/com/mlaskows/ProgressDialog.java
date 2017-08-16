package com.mlaskows;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ProgressDialog extends Dialog {

    private final Label label = new Label();

    public ProgressDialog() {
        super();
        ButtonType cancel = new ButtonType("Cancel", ButtonBar
                .ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(cancel);

        final ProgressBar progressBar = new ProgressBar();
        progressBar.setMinWidth(300);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(progressBar, 0, 1);

        getDialogPane().setContent(expContent);
    }

    public void setText(String text) {
        label.setText(text);
    }

}
