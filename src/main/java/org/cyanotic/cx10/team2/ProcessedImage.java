package org.cyanotic.cx10.team2;

import java.util.*;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessedImage {

    private final int deltaX, deltaY;

    private final PixelGroup brightestPixelGroup;
    private long sum;

    public ProcessedImage(ImageSource imageSource, Gate gate, int mean, int threshold) {
        // Initialize the variables.
        SortedSet<Integer> markedPixels = new TreeSet<>();
        int[] diffs = new int[imageSource.getPixelCount()];

        // Process the frame.
        for (int x = gate.getLeft(); x < gate.getRight(); x++) {
            for (int y = gate.getTop(); y < gate.getBottom(); y++) {
                int index = imageSource.getPixelIndex(x, y);
                int pixel = imageSource.getPixel(index);
                int red = imageSource.getRed(pixel);

                int diff = Math.abs(mean - red);

                sum += red;
                if (diff < threshold) continue;

                diffs[index] = diff;
                markedPixels.add(index);
            }
        }

        // Store each group of pixels in a separate PixelGroup object.
        List<PixelGroup> pixelGroups = new ArrayList<>();
        while (markedPixels.size() > 0) {
            pixelGroups.add(createPixelGroup(imageSource, diffs, markedPixels));
        }

        // Find the brightest PixelGroup.
        brightestPixelGroup = determineBrightestPixelGroup(pixelGroups);
        if (brightestPixelGroup == null) {
            deltaX = 0;
            deltaY = 0;
        } else {
            // Get the deltaX and deltaY relatively to the middle of the image.
            deltaX = imageSource.getWidth() / 2 - (brightestPixelGroup.getLeft() + brightestPixelGroup.getRight()) / 2;
            deltaY = imageSource.getHeight() / 2 - (brightestPixelGroup.getTop() + brightestPixelGroup.getBottom()) / 2;
        }
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public PixelGroup getBrightestPixelGroup() {
        return brightestPixelGroup;
    }

    public long getSum() {
        return sum;
    }

    private static PixelGroup determineBrightestPixelGroup(List<PixelGroup> pixelGroups) {
        PixelGroup brightest = null;
        int lastMean = 0;
        for (PixelGroup pixelGroup : pixelGroups) {
            if (pixelGroup == null || pixelGroup.getCount() < 10) continue;

            int mean = pixelGroup.getMean();
            if (mean <= lastMean) continue;

            brightest = pixelGroup;
            lastMean = mean;
        }
        return brightest;
    }

    private static PixelGroup createPixelGroup(ImageSource imageSource, int[] diffs, SortedSet<Integer> markedPixels) {
        PixelGroup pixelGroup = new PixelGroup(imageSource);
        int stride = imageSource.getStride();
        Stack<Integer> stack = new Stack<>();
        int index = markedPixels.first();
        stack.push(index);

        while (stack.size() > 0) {
            index = stack.pop();

            if (markedPixels.contains(index) && !pixelGroup.contains(index)) {
                markedPixels.remove(index);
                pixelGroup.add(index, diffs[index]);
                stack.push(index - stride - 1);
                stack.push(index - stride);
                stack.push(index - stride + 1);
                stack.push(index - 1);
                stack.push(index + 1);
                stack.push(index + stride - 1);
                stack.push(index + stride);
                stack.push(index + stride + 1);
            }
        }
        return pixelGroup;
    }
}
