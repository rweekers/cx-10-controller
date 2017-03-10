package org.cyanotic.cx10.common;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.net.decoder.H264Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Created by gerard on 5-3-17.
 */
public class FrameDispatcher implements Runnable {
    private static final int DURATION_THRESHOLD = 1000 / 25;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final H264Decoder h264Decoder;
    private final FrameListener frameListener;

    public FrameDispatcher(H264Decoder h264Decoder, FrameListener frameListener) throws IOException {
        this.h264Decoder = h264Decoder;
        this.frameListener = frameListener;
    }

    @Override
    public void run() {
        try {
            while (true) {
                dispatchFrame();
            }
        } catch (Exception e) {
            logger.error("Failed to grab frame", e);
        }
        try {
            h264Decoder.close();
        } catch (IOException e) {
            logger.error("Failed to close", e);
        }
    }

    public void dispatchFrame() throws IOException {
        final Frame frame = h264Decoder.readFrame();
        if (frame != null && frameListener.isAvailable()) {
            Instant before = Instant.now();
            frameListener.frameReceived(frame);
            long duration = Duration.between(before, Instant.now()).toMillis();
            if (duration > DURATION_THRESHOLD) {
                logger.warn("Processing took " + duration + " ms!");
            }
        }
    }
}
