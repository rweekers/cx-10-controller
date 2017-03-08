package org.cyanotic.cx10.common;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gerard on 8-3-17.
 */
public class PrintStatsFrameListener implements FrameListener, Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FrameListener delegate;
    private final AtomicLong framesGrabbed = new AtomicLong();
    private final AtomicLong framesProcessed = new AtomicLong();
    private final AtomicLong framesDropped = new AtomicLong();

    public PrintStatsFrameListener(FrameListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void frameReceived(Frame frame) {
        framesGrabbed.incrementAndGet();
        if (delegate.isAvailable()) {
            delegate.frameReceived(frame);
            framesProcessed.incrementAndGet();
        } else {
            framesDropped.incrementAndGet();
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void run() {
        logger.info("FPS: {} (grabbed),  {} (processed}, {} (dropped}",
                framesGrabbed.getAndSet(0),
                framesProcessed.getAndSet(0),
                framesDropped.getAndSet(0));
    }
}
