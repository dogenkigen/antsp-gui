package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.config.AcoConfigFactory;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
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

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private MenuItem solveMenuItem;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private Label nameLabel;

    @FXML
    private Label dimensionLabel;

    @FXML
    private Label commentLabel;

    @FXML
    private Label solutionLenLabel;

    @FXML
    private ChoiceBox algorithmTypeChoiceBox;

    @FXML
    private Button solveButton;

    private Tsp tsp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeMenuItem.setOnAction(event -> exit(0));
        openMenuItem.setOnAction(event -> openFile());
        solveMenuItem.setOnAction(event -> solve());
        solveButton.setOnAction(event -> solve());
        algorithmTypeChoiceBox.setItems(FXCollections.observableArrayList("AS"));
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
        new UnsolvedMapDrawer(mapCanvas, tsp).draw(ADDITIONAL_DRAW_SIZE);
    }

    /**
     * Sets primary stage.
     *
     * @param primaryStage
     */
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

}
