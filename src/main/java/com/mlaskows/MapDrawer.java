package com.mlaskows;

import com.mlaskows.tsplib.datamodel.Node;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDrawer {

    private static final Logger LOG = LoggerFactory.getLogger(SolvedMapDrawer.class);

    private final Canvas mapCanvas;
    private final Tsp tsp;
    private final double xSubstract;
    private final double ySubstract;
    private final double xDivider;
    private final double yDivider;

    public MapDrawer(Canvas mapCanvas, Tsp tsp) {
        this.mapCanvas = mapCanvas;
        this.tsp = tsp;
        xSubstract = getMinX() + 1;
        ySubstract = getMinY() + 1;
        xDivider = getMaxX() / mapCanvas.getWidth();
        yDivider = getMaxY() / mapCanvas.getHeight();
        logMinaMax();
    }

    protected void cleanCanvas(GraphicsContext gc) {
        gc.clearRect(0, 0, mapCanvas.getWidth()
                , mapCanvas.getHeight());
    }

    protected double getY(Node node) {
        return (node.getY() - ySubstract) / yDivider;
    }

    protected double getX(Node node) {
        return (node.getX() - xSubstract) / xDivider;
    }


    private double getMaxY() {
        return tsp.getNodes().stream()
                .mapToDouble(n -> n.getY())
                .max()
                .getAsDouble();
    }

    private double getMaxX() {
        return tsp.getNodes().stream()
                .mapToDouble(n -> n.getX())
                .max()
                .getAsDouble();
    }

    private double getMinY() {
        return tsp.getNodes().stream()
                .mapToDouble(n -> n.getY())
                .min()
                .getAsDouble();
    }

    private double getMinX() {
        return tsp.getNodes().stream()
                .mapToDouble(n -> n.getX())
                .min()
                .getAsDouble();
    }

    private void logMinaMax() {
        // FIXME delete this method
        final double maxX = getMaxX();
        final double minX = getMinX();
        final double maxY = getMaxY();
        final double minY = getMinY();
        LOG.debug("min x " + minX + " max x " + maxX + " min y " + minY + " " +
                "max y " +
                maxY);
    }

}
