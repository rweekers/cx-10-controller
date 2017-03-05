package org.cyanotic.cx10.api;

import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by gerard on 5-3-17.
 */
public abstract class ImageListener implements FrameListener {

    private final Java2DFrameConverter converter = new Java2DFrameConverter();

    @Override
    public void frameReceived(Frame frame) {
        BufferedImage image = converter.convert(frame);
        imageReceived(image);
    }

    public abstract void imageReceived(Image image);
}
