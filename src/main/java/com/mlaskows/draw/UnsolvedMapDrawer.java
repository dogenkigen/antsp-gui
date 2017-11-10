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

package com.mlaskows.draw;

import com.mlaskows.tsplib.datamodel.item.Tsp;
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
