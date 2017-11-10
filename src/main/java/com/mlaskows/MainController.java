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

package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.dialog.DialogUtil;
import com.mlaskows.draw.SolvedMapDrawer;
import com.mlaskows.draw.UnsolvedMapDrawer;
import com.mlaskows.filter.DoubleFilter;
import com.mlaskows.filter.IntegerFilter;
import com.mlaskows.save.ImageSaver;
import com.mlaskows.save.SolutionSaver;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.mlaskows.TspFileHelper.formatComment;
import static java.lang.System.exit;

public class MainController {

    private static final String HOMEPAGE_ADDRESS = "https://github.com/dogenkigen/antsp-gui";

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    private Tsp tsp;

    private Solution solution;

    private Parameters parameters = new Parameters();

    @FXML
    private BorderPane mainBorderPane;

    // menu

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem solveMenuItem;

    @FXML
    private MenuItem saveImageMenuItem;

    @FXML
    private MenuItem saveSolutionMenuItem;

    @FXML
    private MenuItem defaultValuesMenuItem;

    // info

    @FXML
    private GridPane infoGridPane;

    @FXML
    private Label nameLabel;

    @FXML
    private Label dimensionLabel;

    @FXML
    private Label commentLabel;

    @FXML
    private Label solutionLenLabel;


    // form

    @FXML
    private GridPane formGridPane;

    @FXML
    private ChoiceBox<AlgorithmType> algorithmTypeChoiceBox;

    @FXML
    private TextField evaporationFactorTextField;

    @FXML
    private TextField pheromoneImportanceTextField;

    @FXML
    private TextField heuristicImportanceTextField;

    @FXML
    private TextField nnFactorTextField;

    @FXML
    private TextField antsCountTextField;

    @FXML
    private TextField maxStagnationTextField;

    @FXML
    private CheckBox localSearchCheckBox;

    private final Label reinitializationCountLabel =
            new Label("Reinitialization count");

    private final TextField reinitializationCountTextField = new TextField();

    private final Label weightLabel = new Label("Weight");

    private final TextField weightTextField = new TextField();

    // map

    private Canvas mapCanvas;

    @FXML
    private StackPane mapStackPane;

    @FXML
    public void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            menuBar.useSystemMenuBarProperty().set(true);
        }
        closeMenuItem.setOnAction(event -> exit(0));

        initValidation();
        initBinding();

        algorithmTypeChoiceBox
                .setItems(FXCollections.observableArrayList(AlgorithmType.values()));
        algorithmTypeChoiceBox.getSelectionModel().selectFirst();
        algorithmTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (ObservableValue<? extends AlgorithmType> observable,
                         AlgorithmType oldValue,
                         AlgorithmType newValue) -> initForm()
                );
    }

    public void initMapCanvas() {
        final int paddingValue = 10;
        final double rightWidth = mainBorderPane.getRight().getLayoutBounds().getWidth();
        final double mainBorderPaneWidth = mainBorderPane.getWidth();
        final double rightHeight = mainBorderPane.getRight().getLayoutBounds().getHeight();
        final double bottomHeight = mainBorderPane.getBottom().getLayoutBounds().getHeight();
        mapCanvas = new Canvas(mainBorderPaneWidth - rightWidth - paddingValue,
                rightHeight - bottomHeight - paddingValue);
        mapStackPane.setPadding(new Insets(paddingValue, paddingValue, paddingValue,
                paddingValue));
        mapStackPane.setAlignment(mapCanvas, Pos.CENTER);
        mapStackPane.getChildren().add(mapCanvas);
    }

    public void openFile() {
        try {
            final Optional<Tsp> tspOptional = TspFileHelper.getTsp();
            if (tspOptional.isPresent()) {
                this.tsp = tspOptional.get();
            } else {
                return;
            }
        } catch (Exception e) {
            DialogUtil.showError("Can't open TSP file", e.getMessage());
            return;
        }
        int maxCommentLen = (int) infoGridPane.getWidth() / 10;
        final String comment = formatComment(tsp.getComment(), maxCommentLen);
        LOG.debug("Opening TSP: " + tsp.getName() + " " + comment);
        new UnsolvedMapDrawer(mapCanvas, tsp).draw();
        initializeInfo(comment);
        enableElementsAfterLoadingProblem();
        initForm();
    }

    public void solve() {
        final AcoConfig config = parameters.getConfig();
        LOG.debug("Solving with config " + config.toString());

        Task<Solution> task = new SolvingTask(tsp, config, parameters.getAlgorithmType());
        final Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        task.setOnSucceeded(e -> {
            try {
                solution = task.get();
                new SolvedMapDrawer(mapCanvas, tsp, solution).draw();
                solutionLenLabel.setText(String.valueOf(solution.getTourLength()));
                enableElementsAfterSolvingProblem();
            } catch (InterruptedException | ExecutionException ex) {
                final String error = "Can't solve problem ";
                LOG.error(error + ex.getMessage());
                DialogUtil.showError(error, ex.getMessage());
            }
        });
    }

    public void saveSolution() {
        try {
            new SolutionSaver(tsp, solution).save();
        } catch (IOException e) {
            final String error = "Can't save solution file ";
            LOG.error(error + e.getMessage());
            DialogUtil.showError(error, e.getMessage());
        }
    }

    public void saveImage() {
        WritableImage writableImage =
                new WritableImage((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
        mapCanvas.snapshot(null, writableImage);
        try {
            new ImageSaver(writableImage).save();
        } catch (IOException e) {
            final String error = "Can't save image file ";
            LOG.error(error + e.getMessage());
            DialogUtil.showError(error, e.getMessage());
        }
    }

    public void initForm() {
        hideAllOptionalFields();
        parameters.initParameters(tsp.getDimension());
        switch (parameters.getAlgorithmType()) {
            case MIN_MAX:
                showMaxMinOptionalFields();
                break;
            case RANK_BASED:
                showRankBasedOptionalFields();
                break;
        }
    }

    private void showRankBasedOptionalFields() {
        formGridPane.add(weightLabel, 0, 7);
        formGridPane.add(weightTextField, 1, 7);
        formGridPane.getRowConstraints().get(7).setMaxHeight(31);
    }

    private void showMaxMinOptionalFields() {
        formGridPane.add(reinitializationCountLabel, 0, 7);
        formGridPane.add(reinitializationCountTextField, 1, 7);
        formGridPane.getRowConstraints().get(7).setMaxHeight(31);
    }

    private void hideAllOptionalFields() {
        formGridPane.getRowConstraints().get(7).setMaxHeight(0);
        formGridPane.getChildren().remove(reinitializationCountLabel);
        formGridPane.getChildren().remove(reinitializationCountTextField);
        formGridPane.getChildren().remove(weightLabel);
        formGridPane.getChildren().remove(weightTextField);
    }

    private void enableElementsAfterLoadingProblem() {
        formGridPane.setDisable(false);
        solveMenuItem.setDisable(false);
        saveImageMenuItem.setDisable(false);
        defaultValuesMenuItem.setDisable(false);
    }

    private void initializeInfo(String comment) {
        nameLabel.setText(tsp.getName());
        dimensionLabel.setText(String.valueOf(tsp.getDimension()));
        commentLabel.setText(comment);
        solutionLenLabel.setText("");
    }

    private void enableElementsAfterSolvingProblem() {
        saveSolutionMenuItem.setDisable(false);
    }

    private void initBinding() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setGroupingUsed(false);
        algorithmTypeChoiceBox
                .valueProperty()
                .bindBidirectional(parameters.algorithmTypeProperty());
        evaporationFactorTextField
                .textProperty()
                .bindBidirectional(parameters.evaporationFactorProperty(), decimalFormat);
        pheromoneImportanceTextField
                .textProperty()
                .bindBidirectional(parameters.pheromoneImportanceProperty(), decimalFormat);
        heuristicImportanceTextField
                .textProperty()
                .bindBidirectional(parameters.heuristicImportanceProperty(), decimalFormat);
        nnFactorTextField
                .textProperty()
                .bindBidirectional(parameters.nnFactorProperty(), decimalFormat);
        antsCountTextField
                .textProperty()
                .bindBidirectional(parameters.antsCountProperty(), decimalFormat);
        maxStagnationTextField
                .textProperty()
                .bindBidirectional(parameters.maxStagnationCountProperty(), decimalFormat);
        reinitializationCountTextField
                .textProperty()
                .bindBidirectional(parameters.reinitializationCountProperty(), decimalFormat);
        weightTextField
                .textProperty()
                .bindBidirectional(parameters.weightProperty(), decimalFormat);
        localSearchCheckBox
                .selectedProperty()
                .bindBidirectional(parameters.localSearchProperty());
    }

    private void initValidation() {
        evaporationFactorTextField.setTextFormatter(getDoubleTextFormatter());
        pheromoneImportanceTextField.setTextFormatter(getIntegerTextFormatter());
        heuristicImportanceTextField.setTextFormatter(getIntegerTextFormatter());
        nnFactorTextField.setTextFormatter(getIntegerTextFormatter());
        antsCountTextField.setTextFormatter(getIntegerTextFormatter());
        maxStagnationTextField.setTextFormatter(getIntegerTextFormatter());
        reinitializationCountTextField.setTextFormatter(getIntegerTextFormatter());
        weightTextField.setTextFormatter(getIntegerTextFormatter());
    }

    private TextFormatter<Integer> getIntegerTextFormatter() {
        return new TextFormatter<>(new IntegerStringConverter(), 0, new IntegerFilter());
    }

    private TextFormatter<Double> getDoubleTextFormatter() {
        return new TextFormatter<>(new DoubleStringConverter(), 0.0, new DoubleFilter());
    }

    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Antsp GUI");

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

        alert.getDialogPane().setContent(expContent);

        alert.showAndWait();
    }

    private void openHomepage() {
        try {
            Desktop.getDesktop().browse(new URI(HOMEPAGE_ADDRESS));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
