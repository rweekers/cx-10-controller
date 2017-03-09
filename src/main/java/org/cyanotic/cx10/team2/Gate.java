package org.cyanotic.cx10.team2;

import java.awt.image.BufferedImage;

/**
 * Created by gerard on 8-3-17.
 */
public class Gate {
    private final double size;
    private final int x, y, width, height;

    public Gate(int imageWidth, int imageHeight, double size) {
        double maximumSize = 0.75;
        double minimumSize = 1 / Math.min(imageWidth, imageHeight);
        if (size > maximumSize) size = maximumSize;
        else if (size < minimumSize) size = minimumSize;

        this.size = size;
        this.width = (int) (imageWidth * size);
        this.height = (int) (imageHeight * size);
        this.x = (imageWidth - width) / 2;
        this.y = (imageHeight - height) / 2;
    }

    private Gate(double size, int x, int y, int width, int height) {
        this.size = size;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

    public Gate resize(double factor) {
        double newSize = this.size * factor;
        int newWidth = (int) (this.width * factor);
        int newHeight = (int) (this.height * factor);
        int newX = this.x + (this.width - newWidth);
        int newY = this.y + (this.height - newHeight);
        return new Gate(newSize, newX, newY, newWidth, newHeight);
    }

    public void draw(BufferedImage image, int rgbPixel) {
        drawRectangle(image, getX(), getY(), getWidth(), getHeight(), rgbPixel);
    }

    public void drawRectangle(BufferedImage image, int x, int y, int width, int height, int rgbPixel) {
        drawHorizontalLine(image, x, y, width, rgbPixel);
        drawHorizontalLine(image, x, y + height, width, rgbPixel);
        drawVerticalLine(image, x, y, height, rgbPixel);
        drawVerticalLine(image, x + width, y, height, rgbPixel);
    }

    public void drawHorizontalLine(BufferedImage image, int x, int y, int width, int rgbPixel) {
        for (int column = x; column < width + x; column++) image.setRGB(column - 1, y, rgbPixel);
    }

    public void drawVerticalLine(BufferedImage image, int x, int y, int height, int rgbPixel) {
        for (int line = y; line < height + y; line++) image.setRGB(x, line - 1, rgbPixel);
    }
}
