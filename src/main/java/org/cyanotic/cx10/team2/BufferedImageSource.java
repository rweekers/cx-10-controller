package org.cyanotic.cx10.team2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

/**
 * Created by gerard on 9-3-17.
 */
public class BufferedImageSource implements ImageSource {
    private final BufferedImage bufferedImage;

    public BufferedImageSource(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public int getWidth() {
        return bufferedImage.getWidth();
    }

    public int getHeight() {
        return bufferedImage.getHeight();
    }

    @Override
    public int getStride() {
        return bufferedImage.getWidth() * getBytesPerPixel();
    }

    @Override
    public int getBytesPerPixel() {
        return bufferedImage.getColorModel().getPixelSize() / 8;
    }

    @Override
    public int getPixel(int index) {
        Point pixelPoint = getPixelPoint(index);
        return getPixel(pixelPoint.x, pixelPoint.y);
    }

    @Override
    public int getPixel(int x, int y) {
        return bufferedImage.getRGB(x, y);
    }
}
