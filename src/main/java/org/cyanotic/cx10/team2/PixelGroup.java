package org.cyanotic.cx10.team2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by gerard on 8-3-17.
 */
public class PixelGroup {
    private final ImageSource imageSource;
    private final Set<Integer> indices;
    private int sum, count, min, max;
    private int left, right, top, bottom;

    public PixelGroup(ImageSource imageSource) {
        this.imageSource = imageSource;
        this.indices = new HashSet<>();
    }

    public void add(int x, int y, int diff) {
        add(imageSource.getPixelIndex(x, y), diff);
    }

    public void add(int index, int diff) {
        int column = index % imageSource.getStride();
        int line = index / imageSource.getStride();

        indices.add(index);
        sum += diff;
        count++;
        min = (byte) Math.min(min, diff);
        max = (byte) Math.max(max, diff);

        left = Math.min(left, column);
        right = Math.max(right, column);
        top = Math.min(top, line);
        bottom = Math.max(bottom, line);
    }

    public boolean contains(int x, int y) {
        return contains(imageSource.getPixelIndex(x, y));
    }

    public boolean contains(int index) {
        return indices.contains(index);
    }

    public Set<Integer> getIndices() {
        return indices;
    }

    public int getSum() {
        return sum;
    }

    public int getCount() {
        return count;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMean() {
        return count == 0 ? 0 : sum / count;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public int getTop() {
        return top;
    }

    public int getBottom() {
        return bottom;
    }

    public void draw(BufferedImage image, int rgbPixel) {
        for (int index : indices) {
            Point pixelPoint = imageSource.getPixelPoint(index);
            image.setRGB(pixelPoint.x, pixelPoint.y, rgbPixel);
        }
    }
}
