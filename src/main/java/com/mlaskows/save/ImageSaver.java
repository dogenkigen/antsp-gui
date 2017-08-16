package com.mlaskows.save;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver extends Saver {

    private final WritableImage writableImage;

    public ImageSaver(WritableImage writableImage) {
        this.writableImage = writableImage;
    }

    @Override
    public void save() throws IOException {
        File file = getFile("PNG files (*.png)", "*.png");
        if (file != null) {
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        }
    }

}
