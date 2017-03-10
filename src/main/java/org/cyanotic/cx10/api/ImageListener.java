package org.cyanotic.cx10.api;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;

/**
 * Created by gerard on 5-3-17.
 */
public interface ImageListener extends FrameListener {

    Java2DFrameConverter converter = new Java2DFrameConverter();

    @Override
    default void frameReceived(Frame frame) {
        imageReceived(converter.convert(frame));
    }

    void imageReceived(BufferedImage image);
}
