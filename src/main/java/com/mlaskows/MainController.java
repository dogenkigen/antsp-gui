package com.mlaskows;

import com.mlaskows.antsp.config.*;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mlaskows.TspFileHelper.formatComment;
import static java.lang.System.exit;

public class MainController {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    // TODO set by user
    private static final int DRAW_SIZE = 7;

    private Tsp tsp;

    // menu

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private MenuItem solveMenuItem;

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

    private void initFormForAlgorithmType(AlgorithmType algorithmType) {
        final AcoConfig config;
        switch (algorithmType) {
            case MIN_MAX:
                config = AcoConfigFactory
                        .createDefaultMinMaxConfig(tsp.getDimension());
                break;
            case RANK_BASED:
                config = AcoConfigFactory
                        .createDefaultRankedBasedConfig(tsp.getDimension());
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

    private void solve() {
        if (tsp == null) {
            //show error or disable button if nothing loaded
            return;
        }
        final AcoConfig config = getConfig(algorithmTypeChoiceBox
                .getSelectionModel().getSelectedItem());
        LOG.debug("Solving with config " + config.toString());
        final StaticMatrices matrices = new StaticMatricesBuilder(tsp)
                .withHeuristicInformationMatrix()
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
        final Solution solution = new AntSystemSolver(matrices, config).getSolution();
        solutionLenLabel.setText(String.valueOf(solution.getTourLength()));
        new SolvedMapDrawer(mapCanvas, tsp, solution).draw(DRAW_SIZE);
    }

    private AcoConfig getConfig(AlgorithmType algorithmType) {
        final AcoConfigBuilder configBuilder;
        switch (algorithmType) {
            case MIN_MAX:
                configBuilder = new MinMaxConfigBuilder();
                // TODO add missing values
                break;
            case RANK_BASED:
                configBuilder = new RankedBasedConfigBuilder();
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
        LOG.debug("Opening TSP: " + tsp.getName() + " " +
                formatComment(tsp.getComment(), maxCommentLen));
        nameLabel.setText(tsp.getName());
        dimensionLabel.setText(String.valueOf(tsp.getDimension()));
        commentLabel.setText(formatComment(tsp.getComment(), maxCommentLen));
        initFormForAlgorithmType(algorithmTypeChoiceBox.getValue());
        new UnsolvedMapDrawer(mapCanvas, tsp).draw(DRAW_SIZE);
    }

}
