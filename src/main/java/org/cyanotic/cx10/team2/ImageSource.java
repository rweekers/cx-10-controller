package org.cyanotic.cx10.team2;

import java.awt.*;

/**
 * Created by gerard on 9-3-17.
 */
public interface ImageSource {
    int getWidth();

    int getHeight();

    int getStride();

    default int getPixelCount() {
        return getWidth() * getHeight();
    }

    int getBytesPerPixel();

    default int getPixelIndex(int x, int y) {
        return y * getHeight() + x;
    }

    default Point getPixelPoint(int index) {
        return new Point(index % getHeight(), index / getHeight());
    }

    int getPixel(int index);

    int getPixel(int x, int y);

    int getRed(int pixel);
}
