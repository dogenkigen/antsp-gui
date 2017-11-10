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

import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HelpDialog extends Alert {

    private static final String HOMEPAGE_ADDRESS = "https://github.com/dogenkigen/antsp-gui";

    public HelpDialog() {
        super(AlertType.INFORMATION);

        setTitle("About");
        setHeaderText("Antsp GUI");

        final Label label = new Label();
        label.setText("This application is free software distributed under GPLv3 license.\n" +
                "The purpose of this program is to solve TSP problems using Ant Colony Algorithms.\n" +
                "Version: 0.1\n" +
                "Author: Maciej Laskowski\n" +
                "Contact: mlaskowsk@gmail.com");

        final Hyperlink hyperlink = new Hyperlink();
        hyperlink.setText(HOMEPAGE_ADDRESS);
        hyperlink.setOnAction(event -> openHomepage());

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(hyperlink, 0, 1);

        getDialogPane().setContent(expContent);
    }

    private void openHomepage() {
        try {
            Desktop.getDesktop().browse(new URI(HOMEPAGE_ADDRESS));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
