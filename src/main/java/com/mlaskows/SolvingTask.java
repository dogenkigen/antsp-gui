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
import com.mlaskows.antsp.config.MaxMinConfig;
import com.mlaskows.antsp.config.RankedBasedConfig;
import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.antsp.datamodel.data.StaticData;
import com.mlaskows.antsp.datamodel.data.StaticDataBuilder;
import com.mlaskows.antsp.solvers.AlgorithmType;
import com.mlaskows.antsp.solvers.Solver;
import com.mlaskows.antsp.solvers.antsolvers.AntSystemSolver;
import com.mlaskows.antsp.solvers.antsolvers.ElitistAntSolver;
import com.mlaskows.antsp.solvers.antsolvers.MaxMinAntSolver;
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
        final StaticData data = getData();
        if (isCancelled()) {
            return null;
        }
        runInPlatformThread(() -> progressDialog.setText("Solving problem..."));
        solver = getSolver(data);
        return solver.getSolution();
    }

    private Solver getSolver(StaticData data) {
        switch (algorithmType) {
            case ANT_SYSTEM:
                return new AntSystemSolver(data, config);
            case RANK_BASED:
                return new RankBasedAntSolver(data, (RankedBasedConfig) config);
            case MIN_MAX:
                return new MaxMinAntSolver(data, (MaxMinConfig) config);
            case ELITIST:
                return new ElitistAntSolver(data, config);
            default:
                throw new IllegalArgumentException(algorithmType.toString()
                        + " not implemented yet");
        }
    }

    private StaticData getData() {
        final StaticDataBuilder builder = new StaticDataBuilder(tsp);
        if (algorithmType.isAntBased()) {
            builder.withHeuristicInformationMatrix()
            .withHeuristicSolution();
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
