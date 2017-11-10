/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.mlaskows.dialog;

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
