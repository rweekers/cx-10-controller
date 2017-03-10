package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by gerard on 10-3-17.
 */
public class CompositeFrameListener implements FrameListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FrameListener[] delegates;

    public CompositeFrameListener(FrameListener... delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean isAvailable() {
        return Stream.of(delegates).allMatch(FrameListener::isAvailable);
    }

    @Override
    public void frameReceived(Frame frame) {
        Stream.of(delegates).forEachOrdered(frameListener -> frameListener.frameReceived(frame));
    }

    @Override
    public void close() throws IOException {
        Stream.of(delegates).forEachOrdered(frameListener -> {
            try {
                frameListener.close();
            } catch (IOException e) {
                logger.error("Could not close {}", frameListener, e);
            }
        });
    }
}
