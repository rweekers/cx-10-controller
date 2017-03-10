package org.cyanotic.cx10.team2;

/**
 * Created by gerard on 8-3-17.
 */
public class Gate {
    public static final double MAXIMUM_SIZE = 1;

    private final double minimumSize, size;
    private final int x, y, width, height;

    public Gate(int imageWidth, int imageHeight, double size) {
        minimumSize = 1 / Math.min(imageWidth, imageHeight);
        if (size > MAXIMUM_SIZE) size = MAXIMUM_SIZE;
        else if (size < minimumSize) size = minimumSize;

        this.size = size;
        this.width = (int) (imageWidth * size);
        this.height = (int) (imageHeight * size);
        this.x = (imageWidth - width) / 2;
        this.y = (imageHeight - height) / 2;
    }

    public double getSize() {
        return size;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelCount() {
        return getWidth() * getHeight();
    }

    public int getLeft() {
        return x;
    }

    public int getRight() {
        return x + width - 1;
    }

    public int getTop() {
        return y;
    }

    public int getBottom() {
        return y + height - 1;
    }
}
