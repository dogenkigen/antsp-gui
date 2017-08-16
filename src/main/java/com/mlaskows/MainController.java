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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.mlaskows.TspFileHelper.formatComment;
import static java.lang.System.exit;

public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    // TODO set by user
    private static final int DRAW_SIZE = 7;

    private Tsp tsp;

    private Solution solution;

    // menu

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

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

    @FXML
    private Button solveButton;

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
        /*final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac"))
            menuBar.useSystemMenuBarProperty().set(true);*/
        // TODO switch to onAction="#handleButtonAction"
        closeMenuItem.setOnAction(event -> exit(0));
        openMenuItem.setOnAction(event -> openFile());
        solveMenuItem.setOnAction(event -> solve());
        solveButton.setOnAction(event -> solve());
        saveImageMenuItem.setOnAction(event -> saveImage());
        saveSolutionMenuItem.setOnAction(event -> saveSolution());

        algorithmTypeChoiceBox
                .setItems(FXCollections.observableArrayList(AlgorithmType.values()));
        algorithmTypeChoiceBox.getSelectionModel().selectFirst();
        algorithmTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener(
                        (ObservableValue<? extends AlgorithmType> observable,
                         AlgorithmType oldValue,
                         AlgorithmType newValue) -> initFormForAlgorithmType(newValue)
                );
    }

    private void saveSolution() {
        try {
            new SolutionSaver(tsp, solution).save();
        } catch (IOException e) {
            // TODO show error
            LOG.error("Can't save solution file " + e.getMessage());
        }
    }

    private void saveImage() {
        WritableImage writableImage =
                new WritableImage((int) mapCanvas.getWidth(), (int) mapCanvas.getHeight());
        mapCanvas.snapshot(null, writableImage);
        try {
            new ImageSaver(writableImage).save();
        } catch (IOException e) {
            // TODO show error
            LOG.error("Can't save image file " + e.getMessage());
        }
    }

    private void initFormForAlgorithmType(AlgorithmType algorithmType) {
        hideAllOptionalFieldsAndValues();
        final AcoConfig config;
        switch (algorithmType) {
            case MIN_MAX:
                showMinMaxOptionalFields();
                config = AcoConfigFactory
                        .createDefaultMinMaxConfig(tsp.getDimension());
                minLimitDividerTextField
                        .setText(String.valueOf(((MinMaxConfig) config).getMinPheromoneLimitDivider()));
                reinitializationCountTextField
                        .setText(String.valueOf(((MinMaxConfig) config).getReinitializationCount()));
                localSearchCheckBox.setSelected(true);
                break;
            case RANK_BASED:
                showRankBasedOptionalFields();
                config = AcoConfigFactory
                        .createDefaultRankedBasedConfig(tsp.getDimension());
                weightTextField
                        .setText(String.valueOf(((RankedBasedConfig) config).getWeight()));
                break;
            default:
                config = AcoConfigFactory
                        .createDefaultAntSystemConfig(tsp.getDimension());
        }
        evaporationFactorTextField.setText(String.valueOf(config.getPheromoneEvaporationFactor()));
        pheromoneImportanceTextField.setText(String.valueOf(config.getPheromoneImportance()));
        heuristicImportanceTextField.setText(String.valueOf(config.getHeuristicImportance()));
        nnFactorTextField.setText(String.valueOf(config.getNearestNeighbourFactor()));
        antsCountTextField.setText(String.valueOf(config.getAntsCount()));
        maxStagnationTextField.setText(String.valueOf(config.getMaxStagnationCount()));
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

    private void hideAllOptionalFieldsAndValues() {
        formGridPane.getRowConstraints().get(7).setMaxHeight(0);
        formGridPane.getRowConstraints().get(8).setMaxHeight(0);
        formGridPane.getChildren().remove(minLimitDividerLabel);
        formGridPane.getChildren().remove(minLimitDividerTextField);
        formGridPane.getChildren().remove(reinitializationCountLabel);
        formGridPane.getChildren().remove(reinitializationCountTextField);
        formGridPane.getChildren().remove(weightLabel);
        formGridPane.getChildren().remove(weightTextField);

        localSearchCheckBox.setSelected(false);
    }

    private void solve() {
        final AlgorithmType algorithmType = algorithmTypeChoiceBox
                .getSelectionModel().getSelectedItem();
        final AcoConfig config = getConfig(algorithmType);
        LOG.debug("Solving with config " + config.toString());

        Task<Solution> task = new SolvingTask(tsp, config, algorithmType);
        final Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        task.setOnSucceeded(e -> {
            try {
                solution = task.get();
                solutionLenLabel.setText(String.valueOf(solution.getTourLength()));
                new SolvedMapDrawer(mapCanvas, tsp, solution).draw(DRAW_SIZE);
                saveImageMenuItem.setDisable(false);
                saveSolutionMenuItem.setDisable(false);
            } catch (InterruptedException | ExecutionException ex) {
                // TODO show error
                LOG.error("Can't solve problem " + ex.getMessage());
            }
        });
    }

    private AcoConfig getConfig(AlgorithmType algorithmType) {
        final AcoConfigBuilder configBuilder;
        switch (algorithmType) {
            case MIN_MAX:
                configBuilder = new MinMaxConfigBuilder()
                        .withMinPheromoneLimitDivider(Integer.parseInt(minLimitDividerTextField.getText()))
                        .withReinitializationCount(Integer.parseInt(reinitializationCountTextField.getText()));
                break;
            case RANK_BASED:
                configBuilder = new RankedBasedConfigBuilder()
                        .withWeight(Integer.parseInt(weightTextField.getText()));
                break;
            default:
                configBuilder = new AcoConfigBuilder();
        }
        configBuilder.withAntsCount(Integer.parseInt(antsCountTextField.getText()))
                .withHeuristicImportance(Integer.parseInt(heuristicImportanceTextField.getText()))
                .withPheromoneImportance(Integer.parseInt(pheromoneImportanceTextField.getText()))
                .withMaxStagnationCount(Integer.parseInt(maxStagnationTextField.getText()))
                .withPheromoneEvaporationFactor(Double.parseDouble(evaporationFactorTextField.getText()))
                .withNearestNeighbourFactor(Integer.parseInt(nnFactorTextField.getText()))
                .withWithLocalSearch(localSearchCheckBox.isSelected());
        return configBuilder.build();
    }

    private void openFile() {
        tsp = TspFileHelper.getTsp();
        int maxCommentLen = (int) infoGridPane.getWidth() / 10;
        final String comment = formatComment(tsp.getComment(), maxCommentLen);
        LOG.debug("Opening TSP: " + tsp.getName() + " " +
                comment);
        showInfo(comment);
        enableElementsAfterLoadingProblem();
        initFormForAlgorithmType(algorithmTypeChoiceBox.getValue());
        new UnsolvedMapDrawer(mapCanvas, tsp).draw(DRAW_SIZE);
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

}
