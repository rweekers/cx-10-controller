package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gerard on 5-3-17.
 */
public class FrameDecoder implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final H264Decoder h264Decoder;
    private final FrameListener frameListener;
    private final AtomicLong framesGrabbed = new AtomicLong();
    private final AtomicLong framesProcessed = new AtomicLong();
    private final AtomicLong framesDropped = new AtomicLong();
    private final ScheduledFuture<?> printStatsFuture;

    public FrameDecoder(H264Decoder h264Decoder, FrameListener frameListener) throws IOException {
        this.h264Decoder = h264Decoder;
        this.frameListener = frameListener;
        this.printStatsFuture = ExecutorUtils.schedulePrintVideoStats(this::printStats);
        ExecutorUtils.scheduleVideoDecoder(this::decodeFrame).start();
    }

    @Override
    public void close() throws Exception {
        printStatsFuture.cancel(false);
        h264Decoder.close();
        frameListener.close();
    }

    private void decodeFrame() {
        try {
            while (h264Decoder.isConnected()) {
                final Frame frame = h264Decoder.readFrame();
                if (frame != null) {
                    framesGrabbed.incrementAndGet();
                    if (frameListener.isAvailable()) {
                        frameListener.frameReceived(frame);
                        framesProcessed.incrementAndGet();
                    } else {
                        framesDropped.incrementAndGet();
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to grab frame", e);
        }
    }

    private void printStats() {
        logger.info("FPS: {} (grabbed)", framesGrabbed.getAndSet(0));
        logger.info("FPS: {} (processed}", framesProcessed.getAndSet(0));
        logger.info("Frames not processed: {}", framesDropped.get());
    }
}
