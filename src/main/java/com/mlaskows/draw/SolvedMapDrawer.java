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

import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.tsplib.datamodel.item.Node;
import com.mlaskows.tsplib.datamodel.item.Tsp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class SolvedMapDrawer extends MapDrawer {

    private static final Logger LOG = LoggerFactory.getLogger(SolvedMapDrawer.class);

    private final Solution solution;

    public SolvedMapDrawer(Canvas mapCanvas, Tsp tsp, Solution solution) {
        super(mapCanvas, tsp);
        this.solution = solution;
    }

    @Override
    public void draw() {
        LOG.debug("Drawing solution");

        final GraphicsContext gc = getMapCanvas().getGraphicsContext2D();
        cleanCanvas(gc);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(Math.max(getXScaleFactor(), getYScaleFactor()));
        gc.setTransform(getTransform());
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
        for (Node node : getNodes()) {
            if (previous.equals(node.getId() - 1)) {
                return node;
            }
        }
        return null;
    }

}
