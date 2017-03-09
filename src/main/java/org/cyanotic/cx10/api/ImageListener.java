package org.cyanotic.cx10.api;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by gerard on 5-3-17.
 */
public abstract class ImageListener implements FrameListener {

    private static final int DURATION_THRESHOLD = 1000 / 25;
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Java2DFrameConverter converter = new Java2DFrameConverter();
    private final ExecutorService executor;
    private Future future;

    protected ImageListener(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public boolean isAvailable() {
        return future == null || future.isDone();
    }

    @Override
    public void frameReceived(Frame frame) {
        if (isAvailable()) {
            final BufferedImage image = converter.convert(frame);
            future = executor.submit(() -> {
                Instant before = Instant.now();
                imageReceived(image);
                long duration = Duration.between(before, Instant.now()).toMillis();
                if (duration > DURATION_THRESHOLD) {
                    logger.warn("Processing took " + duration + " ms!");
                }
            });
        }
    }

    public abstract void imageReceived(BufferedImage image);
}
