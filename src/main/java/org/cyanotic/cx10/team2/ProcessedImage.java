package org.cyanotic.cx10.team2;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessedImage {

    private final int deltaX, deltaY;

    private final List<PixelGroup> pixelGroups = new ArrayList<>();
    private final PixelGroup detectedPixelGroup;

    public ProcessedImage(ImageSource imageSource, Gate gate, Color color, int threshold) {
        // Initialize the variables.
        Set<Integer> markedPixels = new HashSet<>();

        // Process the frame.
        int step = (int) Math.nextUp(gate.getWidth() / 100);
        for (int y = gate.getTop(); y <= gate.getBottom(); y += step) {
            for (int x = gate.getLeft(); x <= gate.getRight(); x += step) {
                int index = imageSource.getPixelIndex(x, y);
                int pixel = imageSource.getPixel(index);

                int alpha = (pixel >> 24) & 0xFF;
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;

                boolean alphaMatches = alpha > color.getAlpha() - threshold && alpha < color.getAlpha() + threshold;
                boolean redMatches = red > color.getRed() - threshold && red < color.getRed() + threshold;
                boolean greenMatches = green > color.getGreen() - threshold && green < color.getGreen() + threshold;
                boolean blueMatches = blue > color.getBlue() - threshold && blue < color.getBlue() + threshold;

                if (alphaMatches && redMatches && greenMatches && blueMatches) {
//                    System.out.println(index + " = " + ByteUtils.bytesToHex(ByteUtils.asUnsigned(alpha, red, green, blue)));
                    markedPixels.add(index);
                }
            }
        }

        // Store each group of pixels in a separate PixelGroup object.
        while (markedPixels.size() > 0) {
            pixelGroups.add(createPixelGroup(imageSource, markedPixels));
        }

        // Find the best matching PixelGroup.
        detectedPixelGroup = determineBiggestPixelGroup(pixelGroups);
        if (detectedPixelGroup == null) {
            deltaX = 0;
            deltaY = 0;
        } else {
            // Get the deltaX and deltaY relatively to the middle of the image.
            deltaX = detectedPixelGroup.getHorizontalCenter() - (imageSource.getWidth() / 2);
            deltaY = detectedPixelGroup.getVerticalCenter() - (imageSource.getHeight() / 2);
        }
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public List<PixelGroup> getPixelGroups() {
        return pixelGroups;
    }

    public PixelGroup getDetectedPixelGroup() {
        return detectedPixelGroup;
    }

    private static PixelGroup determineBiggestPixelGroup(List<PixelGroup> pixelGroups) {
        PixelGroup biggest = null;
        for (PixelGroup pixelGroup : pixelGroups) {
            if (pixelGroup == null) continue;
            if (biggest != null && pixelGroup.getCount() < biggest.getCount()) continue;
            biggest = pixelGroup;
        }
        return biggest;
    }

//    private static PixelGroup determineBrightestPixelGroup(List<PixelGroup> pixelGroups) {
//        PixelGroup brightest = null;
//        for (PixelGroup pixelGroup : pixelGroups) {
//            if (pixelGroup == null) continue;
//            if (brightest != null && pixelGroup.getMean() < brightest.getMean()) continue;
//            brightest = pixelGroup;
//        }
//        return brightest;
//    }

    private static PixelGroup createPixelGroup(ImageSource imageSource, Set<Integer> markedPixels) {
        PixelGroup pixelGroup = new PixelGroup(imageSource);
        int width = imageSource.getWidth();
        Stack<Integer> stack = new Stack<>();
        int index = markedPixels.iterator().next();
        stack.push(index);
        while (stack.size() > 0) {
            index = stack.pop();

            if (markedPixels.contains(index) && !pixelGroup.contains(index)) {
                markedPixels.remove(index);
                pixelGroup.add(index);
                stack.push(index - width - 1);
                stack.push(index - width);
                stack.push(index - width + 1);
                stack.push(index - 1);
                stack.push(index + 1);
                stack.push(index + width - 1);
                stack.push(index + width);
                stack.push(index + width + 1);
            }
        }
        return pixelGroup;
    }
}
