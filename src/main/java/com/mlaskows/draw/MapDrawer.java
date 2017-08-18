package com.mlaskows.draw;

import com.mlaskows.tsplib.datamodel.Node;
import com.mlaskows.tsplib.datamodel.Tsp;
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
    private final Tsp tsp;
    private final List<Node> nodes;
    private final double xSubtract;
    private final double ySubtract;
    private final double xDivider;
    private final double yDivider;
    private final double minX;
    private final double maxX;
    private final double minY;
    private final double maxY;

    public MapDrawer(Canvas mapCanvas, Tsp tsp) {
        this.mapCanvas = mapCanvas;
        this.tsp = tsp;
        nodes = tsp
                .getNodes()
                .orElseThrow(() -> new IllegalArgumentException("TSP file " +
                        "can't be displayed"));
        minX = getMinX();
        xSubtract = minX > 1.0 ? minX : 0.0;
        minY = getMinY();
        ySubtract = minY > 1.0 ? minY : 0.0;

        this.maxX = getMaxX();
        this.maxY = getMinY() ;
        xDivider = maxX / mapCanvas.getWidth();
        yDivider = maxY / mapCanvas.getHeight();
        logValues();
    }

    public abstract void draw();

    public Affine getTransform() {
        final double minXSubtracted = getMinX() - xSubtract;
        final double maxXSubtracted = getMaxX() - xSubtract;
        double xRange = maxXSubtracted - minXSubtracted;
        final double minYSubtracted = getMinY() - ySubtract;
        final double maxYSubtracted = getMaxY() - ySubtract;
        double yRange = maxYSubtracted - minYSubtracted;
        Affine transform = new Affine();
        double xRatio = xRange / mapCanvas.getWidth();
        double yRatio = yRange / mapCanvas.getHeight();
        transform.appendTranslation(minXSubtracted, minYSubtracted);
        transform.appendScale(xRatio, yRatio);
        try {
            transform.invert();
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return transform;
    }

    protected void cleanCanvas( GraphicsContext gc) {
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
    }

    protected double getY(Node node) {
        return (node.getY() - ySubtract);
    }

    protected double getX(Node node) {
        return (node.getX() - xSubtract);
    }

    protected double getDrawSize() {
        final double v = ((maxY / mapCanvas.getHeight())
                + (maxX / mapCanvas.getWidth()))
                / 2;
        return v > MIN_DRAW_SIZE ? v : MIN_DRAW_SIZE;
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

    protected Tsp getTsp() {
        return tsp;
    }

    protected List<Node> getNodes() {
        return nodes;
    }

    private void logValues() {
        // FIXME delete this method
        final double maxX = getMaxX();
        final double minX = getMinX();
        final double maxY = getMaxY();
        final double minY = getMinY();
        LOG.debug("min x " + minX + " max x " + maxX + " min y " + minY + " " +
                "max y " +
                maxY);
        LOG.debug(toString());
    }

    @Override
    public String toString() {
        // FIXME this too
        return "MapDrawer{" +
                "xSubtract=" + xSubtract +
                ", ySubtract=" + ySubtract +
                ", xDivider=" + xDivider +
                ", yDivider=" + yDivider +
                ", mapCanvas.getWidth()=" + mapCanvas.getWidth() +
                ", mapCanvas.getHeight()=" + mapCanvas.getHeight() +
                '}';
    }
}
