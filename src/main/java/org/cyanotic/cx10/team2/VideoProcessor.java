package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.ImageListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by gerard on 8-3-17.
 */
public class VideoProcessor extends ImageListener implements Processor {

    protected VideoProcessor(ExecutorService executor) {
        super(executor);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void imageReceived(BufferedImage image) {

    }

    @Override
    public Point getDelta() {
        return null;
    }
}
