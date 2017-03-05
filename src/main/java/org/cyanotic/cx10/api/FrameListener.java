package org.cyanotic.cx10.api;

import org.bytedeco.javacv.Frame;

/**
 * Created by gerard on 5-3-17.
 */
public interface FrameListener extends AutoCloseable {
    void frameReceived(Frame frame);
}
