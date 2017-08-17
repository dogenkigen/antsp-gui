package com.mlaskows;

import com.mlaskows.antsp.config.*;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.draw.SolvedMapDrawer;
import com.mlaskows.draw.UnsolvedMapDrawer;
import com.mlaskows.save.ImageSaver;
import com.mlaskows.save.SolutionSaver;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static com.mlaskows.TspFileHelper.formatComment;
import static java.lang.System.exit;

public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    private Tsp tsp;

    private Solution solution;

    private Parameters parameters = new Parameters();

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

    private final Label minLimitDividerLabel = new Label("Min. limit divider");

    private final TextField minLimitDividerTextField = new TextField();

    private final Label reinitializationCountLabel =
            new Label("Reinitialization count");

    private final TextField reinitializationCountTextField = new TextField();

    private final Label weightLabel = new Label("Weight");

    private final TextField weightTextField = new TextField();

    // map

    @FXML
    private Canvas mapCanvas;

    @FXML
    public void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);
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
        showInfo(comment);
        enableElementsAfterLoadingProblem();
        initForm();
        new UnsolvedMapDrawer(mapCanvas, tsp).draw();
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
                solutionLenLabel.setText(String.valueOf(solution.getTourLength()));
                new SolvedMapDrawer(mapCanvas, tsp, solution).draw();
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
                showMinMaxOptionalFields();
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

    private void showMinMaxOptionalFields() {
        formGridPane.add(minLimitDividerLabel, 0, 7);
        formGridPane.add(minLimitDividerTextField, 1, 7);
        formGridPane.getRowConstraints().get(7).setMaxHeight(31);
        formGridPane.add(reinitializationCountLabel, 0, 8);
        formGridPane.add(reinitializationCountTextField, 1, 8);
        formGridPane.getRowConstraints().get(8).setMaxHeight(31);
    }

    private void hideAllOptionalFields() {
        formGridPane.getRowConstraints().get(7).setMaxHeight(0);
        formGridPane.getRowConstraints().get(8).setMaxHeight(0);
        formGridPane.getChildren().remove(minLimitDividerLabel);
        formGridPane.getChildren().remove(minLimitDividerTextField);
        formGridPane.getChildren().remove(reinitializationCountLabel);
        formGridPane.getChildren().remove(reinitializationCountTextField);
        formGridPane.getChildren().remove(weightLabel);
        formGridPane.getChildren().remove(weightTextField);
    }

    private void enableElementsAfterLoadingProblem() {
        formGridPane.setDisable(false);
        solveMenuItem.setDisable(false);
    }

    private void showInfo(String comment) {
        nameLabel.setText(tsp.getName());
        dimensionLabel.setText(String.valueOf(tsp.getDimension()));
        commentLabel.setText(comment);
    }

    private void enableElementsAfterSolvingProblem() {
        saveImageMenuItem.setDisable(false);
        saveSolutionMenuItem.setDisable(false);
    }

    private void initBinding() {
        algorithmTypeChoiceBox
                .valueProperty()
                .bindBidirectional(parameters.algorithmTypeProperty());
        evaporationFactorTextField
                .textProperty()
                .bindBidirectional(parameters.evaporationFactorProperty(), new DecimalFormat());
        pheromoneImportanceTextField
                .textProperty()
                .bindBidirectional(parameters.pheromoneImportanceProperty(), new DecimalFormat());
        heuristicImportanceTextField
                .textProperty()
                .bindBidirectional(parameters.heuristicImportanceProperty(), new DecimalFormat());
        nnFactorTextField
                .textProperty()
                .bindBidirectional(parameters.nnFactorProperty(), new DecimalFormat());
        antsCountTextField
                .textProperty()
                .bindBidirectional(parameters.antsCountProperty(), new DecimalFormat());
        maxStagnationTextField
                .textProperty()
                .bindBidirectional(parameters.maxStagnationCountProperty(), new DecimalFormat());
        minLimitDividerTextField
                .textProperty()
                .bindBidirectional(parameters.minLimitDividerProperty(), new DecimalFormat());
        reinitializationCountTextField
                .textProperty()
                .bindBidirectional(parameters.reinitializationCountProperty(), new DecimalFormat());
        weightTextField
                .textProperty()
                .bindBidirectional(parameters.weightProperty(), new DecimalFormat());
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
        minLimitDividerTextField.setTextFormatter(getIntegerTextFormatter());
        reinitializationCountTextField.setTextFormatter(getIntegerTextFormatter());
        weightTextField.setTextFormatter(getIntegerTextFormatter());
    }

    private TextFormatter<Integer> getIntegerTextFormatter() {
        return new TextFormatter<>(new IntegerStringConverter(), 0, new IntegerFilter());
    }

    private TextFormatter<Double> getDoubleTextFormatter() {
        return new TextFormatter<>(new DoubleStringConverter(), 0.0, new DoubleFilter());
    }

}
