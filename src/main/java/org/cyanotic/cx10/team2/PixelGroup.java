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
//    private int sum, min, max;
    private int left, right, top, bottom;

    public PixelGroup(ImageSource imageSource) {
        this.imageSource = imageSource;
        this.indices = new HashSet<>();
        this.left = imageSource.getWidth();
        this.top = imageSource.getHeight();
    }

    public void add(int x, int y) {
        add(imageSource.getPixelIndex(x, y));
    }

    public void add(int index) {
        Point pixelPoint = imageSource.getPixelPoint(index);

        indices.add(index);
//        sum += pixel;
//        count++;
//        min = (byte) Math.min(min, pixel);
//        max = (byte) Math.max(max, pixel);

        left = (int) Math.min(left, pixelPoint.getX());
        right = (int) Math.max(right, pixelPoint.getX());
        top = (int) Math.min(top, pixelPoint.getY());
        bottom = (int) Math.max(bottom, pixelPoint.getY());
    }

    public boolean contains(int x, int y) {
        return contains(imageSource.getPixelIndex(x, y));
    }

    public boolean contains(int index) {
        return indices.contains(index);
    }

//    public Set<Integer> getIndices() {
//        return indices;
//    }

//    public int getSum() {
//        return sum;
//    }

    public int getCount() {
        return indices.size();
    }

//    public int getMin() {
//        return min;
//    }

//    public int getMax() {
//        return max;
//    }

//    public int getMean() {
//        return count == 0 ? 0 : sum / count;
//    }

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

    public int getHorizontalCenter() {
        return (getLeft() + getRight()) / 2;
    }

    public int getVerticalCenter() {
        return (getTop() + getBottom()) / 2;
    }

    public void draw(BufferedImage image, int rgbPixel) {
        for (int index : indices) {
            Point pixelPoint = imageSource.getPixelPoint(index);
            image.setRGB(pixelPoint.x, pixelPoint.y, rgbPixel);
        }
    }
}
