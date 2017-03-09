package org.cyanotic.cx10.team2;

import java.util.*;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessedImage {

    private final int deltaX, deltaY;

    private final PixelGroup detectedPixelGroup;

    public ProcessedImage(ImageSource imageSource, Gate gate, Color color, int threshold) {
        // Initialize the variables.
        Map<Integer, Integer> markedPixels = new HashMap<>();

        // Process the frame.
        for (int y = gate.getTop(); y <= gate.getBottom(); y++) {
            for (int x = gate.getLeft(); x <= gate.getRight(); x++) {
                int index = imageSource.getPixelIndex(x, y);
                int pixel = imageSource.getPixel(index);

                int alpha = (pixel >> 24) & 0xFF;
                if (alpha < 200) continue;

                int component;
                switch (color) {
                    case RED:
                        int red = (pixel >> 16) & 0xFF;
                        component = red;
                        break;
                    case GREEN:
                        int green = (pixel >> 8) & 0xFF;
                        component = green;
                        break;
                    case BLUE:
                        int blue = pixel & 0xFF;
                        component = blue;
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown color " + color);
                }

//                System.out.println(index + " = " + ByteUtils.bytesToHex(new byte[]{(byte) (pixel >> 24), (byte) (pixel >> 16), (byte) (pixel >> 8), (byte) pixel}));

                if (component < threshold) continue;

                markedPixels.put(index, component);
            }
        }

        // Store each group of pixels in a separate PixelGroup object.
        List<PixelGroup> pixelGroups = new ArrayList<>();
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
            deltaX = imageSource.getWidth() / 2 - (detectedPixelGroup.getLeft() + detectedPixelGroup.getRight()) / 2;
            deltaY = imageSource.getHeight() / 2 - (detectedPixelGroup.getTop() + detectedPixelGroup.getBottom()) / 2;
        }
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
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

    private static PixelGroup determineBrightestPixelGroup(List<PixelGroup> pixelGroups) {
        PixelGroup brightest = null;
        for (PixelGroup pixelGroup : pixelGroups) {
            if (pixelGroup == null) continue;
            if (brightest != null && pixelGroup.getMean() < brightest.getMean()) continue;
            brightest = pixelGroup;
        }
        return brightest;
    }

    private static PixelGroup createPixelGroup(ImageSource imageSource, Map<Integer, Integer> markedPixels) {
        PixelGroup pixelGroup = new PixelGroup(imageSource);
        int width = imageSource.getWidth();
        Stack<Integer> stack = new Stack<>();
        int index = markedPixels.keySet().iterator().next();
        stack.push(index);
        while (stack.size() > 0) {
            index = stack.pop();

            if (markedPixels.containsKey(index) && !pixelGroup.contains(index)) {
                pixelGroup.add(index, markedPixels.remove(index));
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
