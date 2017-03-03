package org.cyanotic.cx10.io.video;

import org.bytedeco.javacv.Frame;

/**
 * Created by cyanotic on 25/11/2016.
 */
public interface IVideoPlayer {
    void start();

    void stop();

    boolean isActive();

    void imageReceived(Frame frame);
}
