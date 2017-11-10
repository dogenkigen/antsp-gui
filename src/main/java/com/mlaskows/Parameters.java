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

import com.mlaskows.antsp.config.*;
import com.mlaskows.antsp.solvers.AlgorithmType;
import javafx.beans.property.*;

public class Parameters {

    private ObjectProperty<AlgorithmType> algorithmType =
            new SimpleObjectProperty<>();
    private DoubleProperty evaporationFactor = new SimpleDoubleProperty();
    private IntegerProperty pheromoneImportance = new SimpleIntegerProperty();
    private IntegerProperty heuristicImportance = new SimpleIntegerProperty();
    private IntegerProperty nnFactor = new SimpleIntegerProperty();
    private IntegerProperty antsCount = new SimpleIntegerProperty();
    private IntegerProperty maxStagnationCount = new SimpleIntegerProperty();
    private IntegerProperty minLimitDivider = new SimpleIntegerProperty();
    private IntegerProperty reinitializationCount = new SimpleIntegerProperty();
    private IntegerProperty weight = new SimpleIntegerProperty();
    private BooleanProperty localSearch = new SimpleBooleanProperty();

    public void initParameters(int tspDimension) {
        final AcoConfig config;
        setLocalSearch(false);
        switch (getAlgorithmType()) {
            case MIN_MAX:
                config = AcoConfigFactory
                        .createDefaultMaxMinConfig(tspDimension);
                setReinitializationCount(((MaxMinConfig) config).getReinitializationCount());
                setLocalSearch(true);
                break;
            case RANK_BASED:
                config = AcoConfigFactory
                        .createDefaultRankedBasedConfig(tspDimension);
                setWeight(((RankedBasedConfig) config).getWeight());
                break;
            case ELITIST:
                config = AcoConfigFactory
                        .createDefaultElitistConfig(tspDimension);
                break;
            default:
                config = AcoConfigFactory
                        .createDefaultAntSystemConfig(tspDimension);
        }

        setEvaporationFactor(config.getPheromoneEvaporationFactor());
        setPheromoneImportance(config.getPheromoneImportance());
        setHeuristicImportance(config.getHeuristicImportance());
        setNnFactor(config.getNearestNeighbourFactor());
        setAntsCount(config.getAntsCount());
        setMaxStagnationCount(config.getMaxStagnationCount());
    }

    public AcoConfig getConfig() {
        final AcoConfigBuilder configBuilder;
        switch (getAlgorithmType()) {
            case MIN_MAX:
                configBuilder = new MaxMinConfigBuilder()
                        .withReinitializationCount(getReinitializationCount());
                break;
            case RANK_BASED:
                configBuilder = new RankedBasedConfigBuilder()
                        .withWeight(getWeight());
                break;
            default:
                configBuilder = new AcoConfigBuilder();
        }
        configBuilder.withAntsCount(getAntsCount())
                .withHeuristicImportance(getHeuristicImportance())
                .withPheromoneImportance(getPheromoneImportance())
                .withMaxStagnationCount(getMaxStagnationCount())
                .withPheromoneEvaporationFactor(getEvaporationFactor())
                .withNearestNeighbourFactor(getNnFactor())
                .withWithLocalSearch(isLocalSearch());
        return configBuilder.build();
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType.get();
    }

    public ObjectProperty<AlgorithmType> algorithmTypeProperty() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType.set(algorithmType);
    }

    public double getEvaporationFactor() {
        return evaporationFactor.get();
    }

    public DoubleProperty evaporationFactorProperty() {
        return evaporationFactor;
    }

    public void setEvaporationFactor(double evaporationFactor) {
        this.evaporationFactor.set(evaporationFactor);
    }

    public int getPheromoneImportance() {
        return pheromoneImportance.get();
    }

    public IntegerProperty pheromoneImportanceProperty() {
        return pheromoneImportance;
    }

    public void setPheromoneImportance(int pheromoneImportance) {
        this.pheromoneImportance.set(pheromoneImportance);
    }

    public int getHeuristicImportance() {
        return heuristicImportance.get();
    }

    public IntegerProperty heuristicImportanceProperty() {
        return heuristicImportance;
    }

    public void setHeuristicImportance(int heuristicImportance) {
        this.heuristicImportance.set(heuristicImportance);
    }

    public int getNnFactor() {
        return nnFactor.get();
    }

    public IntegerProperty nnFactorProperty() {
        return nnFactor;
    }

    public void setNnFactor(int nnFactor) {
        this.nnFactor.set(nnFactor);
    }

    public int getAntsCount() {
        return antsCount.get();
    }

    public IntegerProperty antsCountProperty() {
        return antsCount;
    }

    public void setAntsCount(int antsCount) {
        this.antsCount.set(antsCount);
    }

    public int getMaxStagnationCount() {
        return maxStagnationCount.get();
    }

    public IntegerProperty maxStagnationCountProperty() {
        return maxStagnationCount;
    }

    public void setMaxStagnationCount(int maxStagnationCount) {
        this.maxStagnationCount.set(maxStagnationCount);
    }

    public IntegerProperty minLimitDividerProperty() {
        return minLimitDivider;
    }

    public int getReinitializationCount() {
        return reinitializationCount.get();
    }

    public IntegerProperty reinitializationCountProperty() {
        return reinitializationCount;
    }

    public void setReinitializationCount(int reinitializationCount) {
        this.reinitializationCount.set(reinitializationCount);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    public boolean isLocalSearch() {
        return localSearch.get();
    }

    public BooleanProperty localSearchProperty() {
        return localSearch;
    }

    public void setLocalSearch(boolean localSearch) {
        this.localSearch.set(localSearch);
    }
}
