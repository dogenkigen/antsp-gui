package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.config.AcoConfigFactory;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.tsplib.TspLibParser;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.lang.System.exit;

public class MainController implements Initializable {

    private static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    // TODO set by user
    private static final int ADDITIONAL_DRAW_SIZE = 5;

    private Stage primaryStage;

    @FXML
    private Menu fileMenu;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private Canvas mapCanvas;

    @FXML
    private MenuItem solveMenuItem;

    private Tsp tsp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeMenuItem.setOnAction(event -> exit(0));
        openMenuItem.setOnAction(event -> openFile());
        solveMenuItem.setOnAction(event -> solve());
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
        new SolvedMapDrawer(mapCanvas, tsp, solution).draw(ADDITIONAL_DRAW_SIZE);
    }

    private void openFile() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser
                .ExtensionFilter("TSP file (*.tsp)", "*.tsp");
        fileChooser.getExtensionFilters().add(extFilter);
        final File file = fileChooser.showOpenDialog(primaryStage);
        try {
            tsp = TspLibParser.parse(file.getAbsolutePath());
            new UnsolvedMapDrawer(mapCanvas, tsp).draw(ADDITIONAL_DRAW_SIZE);
            LOG.debug(tsp.getComment());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
