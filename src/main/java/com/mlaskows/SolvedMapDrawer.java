package com.mlaskows;

import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.tsplib.datamodel.Node;
import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class SolvedMapDrawer extends MapDrawer {

    private static final Logger LOG = LoggerFactory.getLogger(SolvedMapDrawer.class);

    private final Canvas mapCanvas;
    private final Tsp tsp;
    private final Solution solution;

    public SolvedMapDrawer(Canvas mapCanvas, Tsp tsp, Solution solution) {
        super(mapCanvas, tsp);
        this.mapCanvas = mapCanvas;
        this.tsp = tsp;
        this.solution = solution;
    }

    public void draw() {
        LOG.debug("Drawing solution");

        final GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        cleanCanvas(gc);
        gc.setStroke(Color.BLACK);
        Node previous = null;
        final Iterator<Integer> iterator = solution.getTour().iterator();
        while (iterator.hasNext()) {
            if (previous == null) {
                previous = getNode(iterator.next());
                continue;
            }
            final Node actual = getNode(iterator.next());
            gc.strokeLine(getX(previous), getY(previous), getX(actual), getY
                    (actual));
            previous = actual;
        }
    }

    private Node getNode(Integer previous) {
        for (Node node : tsp.getNodes()) {
            if (previous.equals(node.getId() - 1)) {
                return node;
            }
        }
        return null;
    }

}
