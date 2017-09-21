package com.mlaskows.draw;

import com.mlaskows.tsplib.datamodel.item.Node;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class MapDrawer {

    private static final Logger LOG =
            LoggerFactory.getLogger(SolvedMapDrawer.class);
    private static final double MIN_DRAW_SIZE = 5;

    private final Canvas mapCanvas;
    private final List<Node> nodes;

    private final double xSubtract;
    private final double ySubtract;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;
    private final double minXSubtracted;
    private final double minYSubtracted;
    private final double xScaleFactor;
    private final double yScaleFactor;

    public MapDrawer(Canvas mapCanvas, Tsp tsp) {
        this.mapCanvas = mapCanvas;
        nodes = tsp
                .getNodes()
                .orElseThrow(() -> new IllegalArgumentException("TSP file " +
                        "can't be displayed"));

        minX = getMinX();
        maxX = getMaxX();
        maxY = getMaxY();
        minY = getMinY();

        xSubtract = minX > 1.0 ? minX : 0.0;
        ySubtract = minY > 1.0 ? minY : 0.0;

        minXSubtracted = minX - xSubtract;
        final double maxXSubtracted = maxX - xSubtract;
        minYSubtracted = minY - ySubtract;
        final double maxYSubtracted = maxY - ySubtract;

        final double xRange = maxXSubtracted - minXSubtracted;
        final double yRange = maxYSubtracted - minYSubtracted;

        xScaleFactor = xRange / mapCanvas.getWidth();
        yScaleFactor = yRange / mapCanvas.getHeight();

        logValues();
    }

    public abstract void draw();

    public Affine getTransform() {
        Affine transform = new Affine();
        transform.appendTranslation(minXSubtracted, minYSubtracted);
        transform.appendScale(xScaleFactor, yScaleFactor);
        try {
            transform.invert();
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return transform;
    }

    protected void cleanCanvas(GraphicsContext gc) {
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    protected double getY(Node node) {
        return (node.getY() - ySubtract);
    }

    protected double getX(Node node) {
        return (node.getX() - xSubtract);
    }

    public double getXScaleFactor() {
        return xScaleFactor;
    }

    public double getYScaleFactor() {
        return yScaleFactor;
    }

    private double getMaxY() {
        return nodes.stream()
                .mapToDouble(n -> n.getY())
                .max()
                .getAsDouble();
    }

    private double getMaxX() {
        return nodes.stream()
                .mapToDouble(n -> n.getX())
                .max()
                .getAsDouble();
    }

    private double getMinY() {
        return nodes.stream()
                .mapToDouble(n -> n.getY())
                .min()
                .getAsDouble();
    }

    private double getMinX() {
        return nodes.stream()
                .mapToDouble(n -> n.getX())
                .min()
                .getAsDouble();
    }

    protected Canvas getMapCanvas() {
        return mapCanvas;
    }

    protected List<Node> getNodes() {
        return nodes;
    }

    private void logValues() {
        // FIXME delete this method
        LOG.debug(toString());
    }

    @Override
    public String toString() {
        // FIXME this one too
        return "MapDrawer{" +
                "mapCanvas.getWidth()=" + mapCanvas.getWidth() +
                ", mapCanvas.getHeight()=" + mapCanvas.getHeight() +
                ", nodes=" + nodes +
                ", xSubtract=" + xSubtract +
                ", ySubtract=" + ySubtract +
                ", minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                '}';
    }
}
