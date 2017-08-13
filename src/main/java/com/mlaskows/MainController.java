package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.config.AcoConfigFactory;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.System.exit;

public class MainController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    // TODO set by user
    private static final int ADDITIONAL_DRAW_SIZE = 5;

    private Stage primaryStage;

    private Tsp tsp;

    // menu

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private MenuItem solveMenuItem;

    @FXML
    private Canvas mapCanvas;

    // info

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
    private Button solveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                         AlgorithmType newValue) -> initFormForAlgoritmType(newValue)
                );
    }

    private void initFormForAlgoritmType(AlgorithmType algorithmType) {
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
        final AcoConfig config = AcoConfigFactory.createDefaultAntSystemConfig(tsp.getDimension());
        final StaticMatrices matrices = new StaticMatricesBuilder(tsp)
                .withHeuristicInformationMatrix()
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
        final Solution solution = new AntSystemSolver(matrices, config).getSolution();
        solutionLenLabel.setText(String.valueOf(solution.getTourLength()));
        new SolvedMapDrawer(mapCanvas, tsp, solution).draw(ADDITIONAL_DRAW_SIZE);
    }

    private void openFile() {
        tsp = TspFileHelper.getTsp(primaryStage);
        LOG.debug("Opening TSP: " + tsp.getName() + " " + tsp.getComment());
        nameLabel.setText(tsp.getName());
        dimensionLabel.setText(String.valueOf(tsp.getDimension()));
        commentLabel.setText(tsp.getComment());
        initFormForAlgoritmType(algorithmTypeChoiceBox.getValue());
        new UnsolvedMapDrawer(mapCanvas, tsp).draw(ADDITIONAL_DRAW_SIZE);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
