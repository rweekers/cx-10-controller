package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacv.CanvasFrame;
import org.cyanotic.cx10.api.ImageListener;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;

/**
 * Created by gerard on 2-3-17.
 */
public class SwingVideoPlayer extends ImageListener {

    private final CanvasFrame canvasFrame;

    public SwingVideoPlayer(ExecutorService executor) {
        super(executor);
        canvasFrame = new CanvasFrame("Swing video");
        canvasFrame.setSize(720, 576);
    }

    @Override
    public void close() {
        canvasFrame.dispose();
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
