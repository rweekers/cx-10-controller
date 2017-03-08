package org.cyanotic.cx10.api;

import org.bytedeco.javacv.Frame;

import java.io.Closeable;

/**
 * Created by gerard on 5-3-17.
 */
public interface FrameListener extends Closeable {
    boolean isAvailable();
    
    void frameReceived(Frame frame);
}
