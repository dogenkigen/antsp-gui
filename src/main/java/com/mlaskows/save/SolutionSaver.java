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

package com.mlaskows.save;

import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.tsplib.datamodel.item.Tsp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SolutionSaver extends Saver {

    private final Tsp tsp;
    private final Solution solution;

    public SolutionSaver(Tsp tsp, Solution solution) {
        this.tsp = tsp;
        this.solution = solution;
    }

    @Override
    public void save() throws IOException {
        final File file = getFile("TXT files (*.txt)", "*.txt");
        if (file != null) {
            try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
                writer.write(tsp.getName());
                writer.write(System.getProperty("line.separator"));
                writer.write(tsp.getComment());
                writer.write(System.getProperty("line.separator"));
                writer.write(solution.toString());
            }
        }
    }
}
