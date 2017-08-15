package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.Solver;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolvingTask extends Task<Solution> {

    private static final Logger LOG = LoggerFactory.getLogger(SolvingTask.class);

    private final ProgressDialog progressDialog = new ProgressDialog();
    private final Tsp tsp;
    private final AcoConfig config;
    private Solver solver;

    public SolvingTask(Tsp tsp, AcoConfig config) {
        super();
        this.tsp = tsp;
        this.config = config;
        progressDialog.setOnCloseRequest(event -> {
            solver.stop();
            cancel();
        });
    }

    @Override
    protected Solution call() throws Exception {
        runInPlatformThread(() -> progressDialog.setText("Initializing data..."));
        final StaticMatrices matrices = new StaticMatricesBuilder(tsp)
                .withHeuristicInformationMatrix()
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
        runInPlatformThread(() -> progressDialog.setText("Solving problem..."));
        solver = new AntSystemSolver(matrices, config);
        return solver.getSolution();
    }

    @Override
    protected void running() {
        progressDialog.show();
    }

    @Override
    protected void succeeded() {
        progressDialog.hide();
    }

    @Override
    protected void failed() {
        LOG.error("Failed to solve " + getException().getMessage());
        progressDialog.hide();
    }

    private void runInPlatformThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

}
