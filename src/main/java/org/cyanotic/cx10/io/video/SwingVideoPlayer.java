package org.cyanotic.cx10.io.video;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gerard on 2-3-17.
 */
public class SwingVideoPlayer implements IVideoPlayer {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private final Java2DFrameConverter converter = new Java2DFrameConverter();
    private final AtomicLong framesGrabbed = new AtomicLong();
    private final AtomicLong framesProcessed = new AtomicLong();
    private final AtomicBoolean isProcessing = new AtomicBoolean();
    private CanvasFrame canvasFrame;
    private ScheduledFuture fpsPrintFuture;

    public SwingVideoPlayer() throws IOException {
    }

    @Override
    public void start() {
        fpsPrintFuture = SCHEDULER.scheduleAtFixedRate(this::printFPS, 1, 1, TimeUnit.SECONDS);
        canvasFrame = new CanvasFrame("Swing video");
        canvasFrame.setSize(720, 576);
    }

    @Override
    public void stop() {
        fpsPrintFuture.cancel(false);
        fpsPrintFuture = null;
        canvasFrame.dispose();
        canvasFrame = null;
    }

    @Override
    public boolean isActive() {
        return canvasFrame != null;
    }

    @Override
    public void imageReceived(Frame frame) {
        framesGrabbed.incrementAndGet();
        if (isProcessing.compareAndSet(false, true)) {
            BufferedImage image = converter.convert(frame);
            SwingUtilities.invokeLater(() -> {
                canvasFrame.showImage(image);
                framesProcessed.incrementAndGet();
                isProcessing.set(false);
            });
        }
    }

    @Override
    public String toString() {
        return "SwingVideoPlayer";
    }

    private void printFPS() {
        System.out.println("FPS: " + framesGrabbed.getAndSet(0) + " (grabbed), " + framesProcessed.getAndSet(0) + " (processed)");
    }
}
