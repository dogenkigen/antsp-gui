package com.mlaskows.draw;

import com.mlaskows.tsplib.datamodel.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnsolvedMapDrawer extends MapDrawer {


    private static final Logger LOG = LoggerFactory.getLogger(UnsolvedMapDrawer.class);

    public UnsolvedMapDrawer(Canvas mapCanvas, Tsp tsp) {
        super(mapCanvas, tsp);
    }

    @Override
    public void draw() {
        GraphicsContext gc = getMapCanvas().getGraphicsContext2D();
        cleanCanvas(gc);
        gc.setFill(Color.BLACK);
        gc.setTransform(getTransform());
        getNodes()
                .forEach(node -> gc.fillRect(getX(node), getY(node),
                        getXScaleFactor() * 2, getYScaleFactor() * 2));
        LOG.debug("Done drawing");
    }

}
