package org.cyanotic.cx10.team2;

import org.bytedeco.javacv.Frame;

import java.nio.ByteBuffer;

/**
 * Created by gerard on 9-3-17.
 */
public class FrameImageSource implements ImageSource {
    private final Frame frame;

    public FrameImageSource(Frame frame) {
        this.frame = frame;
    }

    @Override
    public int getWidth() {
        return frame.imageWidth;
    }

    @Override
    public int getHeight() {
        return frame.imageHeight;
    }

    @Override
    public int getStride() {
        return frame.imageStride;
    }

    @Override
    public int getBytesPerPixel() {
        return getStride() / getHeight();
    }

    @Override
    public int getPixel(int index) {
        ByteBuffer data = (ByteBuffer) frame.image[0];
        switch (getBytesPerPixel()) {
            case 1:
                return data.get(index);
            case 2:
                return data.getShort(index);
            case 4:
                return data.getInt(index);
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getPixel(int x, int y) {
        return getPixel(getPixelIndex(x, y));
    }

    @Override
    public int getRed(int pixel) {
        return (pixel >> 16) & 0xFF;
    }
}
