package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacv.CanvasFrame;
import org.cyanotic.cx10.api.ImageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by gerard on 2-3-17.
 */
public class SwingVideoPlayer implements ImageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CanvasFrame canvasFrame;

    public SwingVideoPlayer() {
        canvasFrame = new CanvasFrame("Swing video");
        canvasFrame.setSize(720, 576);
        canvasFrame.setAlwaysOnTop(true);
    }

    @Override
    public void close() {
        canvasFrame.dispose();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void imageReceived(BufferedImage image) {
        try {
            SwingUtilities.invokeAndWait(() -> canvasFrame.showImage(image));
        } catch (InterruptedException | InvocationTargetException e) {
            logger.error("Failed to draw image", e);
        }
    }
}
