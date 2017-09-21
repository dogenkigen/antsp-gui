package com.mlaskows;

import com.mlaskows.antsp.config.AcoConfig;
import com.mlaskows.antsp.config.MinMaxConfig;
import com.mlaskows.antsp.config.RankedBasedConfig;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.matrices.StaticMatrices;
import com.mlaskows.antsp.datamodel.matrices.StaticMatricesBuilder;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.antsp.solvers.Solver;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.antsp.solvers.antsolvers.ElitistAntSolver;
import com.mlaskows.antsp.solvers.antsolvers.MinMaxAntSolver;
import com.mlaskows.antsp.solvers.antsolvers.RankBasedAntSolver;
import com.mlaskows.dialog.DialogUtil;
import com.mlaskows.dialog.ProgressDialog;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolvingTask extends Task<Solution> {

    private static final Logger LOG = LoggerFactory.getLogger(SolvingTask.class);

    private final ProgressDialog progressDialog = new ProgressDialog();
    private final Tsp tsp;
    private final AcoConfig config;
    private final AlgorithmType algorithmType;
    private Solver solver;

    public SolvingTask(Tsp tsp, AcoConfig config, AlgorithmType algorithmType) {
        super();
        this.tsp = tsp;
        this.config = config;
        this.algorithmType = algorithmType;
        progressDialog.setOnCloseRequest(event -> {
            if (solver != null) {
                solver.stop();
            }
            cancel();
        });
    }

    @Override
    protected Solution call() throws Exception {
        runInPlatformThread(() -> progressDialog.setText("Initializing data..."));
        final StaticMatrices matrices = getMatrices();
        if (isCancelled()) {
            return null;
        }
        runInPlatformThread(() -> progressDialog.setText("Solving problem..."));
        solver = getSolver(matrices);
        return solver.getSolution();
    }

    private Solver getSolver(StaticMatrices matrices) {
        switch (algorithmType) {
            case ANT_SYSTEM:
                return new AntSystemSolver(matrices, config);
            case RANK_BASED:
                return new RankBasedAntSolver(matrices, (RankedBasedConfig) config);
            case MIN_MAX:
                return new MinMaxAntSolver(matrices, (MinMaxConfig) config);
            case ELITIST:
                return new ElitistAntSolver(matrices, config);
            default:
                throw new IllegalArgumentException(algorithmType.toString()
                        + " not implemented yet");
        }
    }

    private StaticMatrices getMatrices() {
        final StaticMatricesBuilder builder = new StaticMatricesBuilder(tsp);
        if (algorithmType.isAntBased()) {
            builder.withHeuristicInformationMatrix();
        }
        return builder
                .withNearestNeighbors(config.getNearestNeighbourFactor())
                .build();
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
        final String error = "Failed to solve ";
        final String message = getException().toString();
        LOG.error(error + message);
        progressDialog.hide();
        DialogUtil.showError(error, message);
    }

    private void runInPlatformThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

}
