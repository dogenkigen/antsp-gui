package com.mlaskows;

import com.mlaskows.tsplib.datamodel.Node;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapDrawer {

    private static final Logger LOG =
            LoggerFactory.getLogger(SolvedMapDrawer.class);

    private final Canvas mapCanvas;
    private final Tsp tsp;
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

    public abstract void draw(int drawSize);

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
