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
                setMinLimitDivider(((MaxMinConfig) config).getMinPheromoneLimitDivider());
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
                        .withMinPheromoneLimitDivider(getMinLimitDivider())
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

    public int getMinLimitDivider() {
        return minLimitDivider.get();
    }

    public IntegerProperty minLimitDividerProperty() {
        return minLimitDivider;
    }

    public void setMinLimitDivider(int minLimitDivider) {
        this.minLimitDivider.set(minLimitDivider);
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
