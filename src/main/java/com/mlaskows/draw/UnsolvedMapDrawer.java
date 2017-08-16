package com.mlaskows.draw;

import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnsolvedMapDrawer extends MapDrawer {


    private static final Logger LOG = LoggerFactory.getLogger(UnsolvedMapDrawer.class);

    private final Canvas mapCanvas;
    private final Tsp tsp;

    public UnsolvedMapDrawer(Canvas mapCanvas, Tsp tsp) {
        super(mapCanvas, tsp);
        this.mapCanvas = mapCanvas;
        this.tsp = tsp;
    }

    @Override
    public void draw(int drawSize) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        cleanCanvas(gc);
        gc.setFill(Color.BLACK);
        gc.setTransform(getTransform());
        tsp.getNodes()
                .forEach(node -> gc.fillRect(getX(node), getY(node),
                        drawSize, drawSize));
        LOG.debug("Done drawing");
    }

}