package com.mlaskows.save;

import com.mlaskows.antsp.datamodel.Solution;
import com.mlaskows.tsplib.datamodel.Tsp;

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
        try(BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write(tsp.getName());
            writer.write(System.getProperty("line.separator"));
            writer.write(tsp.getComment());
            writer.write(System.getProperty("line.separator"));
            writer.write(solution.toString());
        }
    }
}
