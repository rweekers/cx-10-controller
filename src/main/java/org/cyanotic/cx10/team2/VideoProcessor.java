package org.cyanotic.cx10.team2;

import org.bytedeco.javacv.CanvasFrame;
import org.cyanotic.cx10.api.ImageListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;

/**
 * Created by gerard on 8-3-17.
 */
public class VideoProcessor implements ImageListener, Processor {

    private static final ColorModel RED_COLOR_MODEL = new DirectColorModel(8, 0x000000ff, 0, 0, 0);
    private static final ColorModel GREEN_COLOR_MODEL = new DirectColorModel(8, 0, 0x000000ff, 0, 0);
    private static final ColorModel BLUE_COLOR_MODEL = new DirectColorModel(8, 0, 0, 0x000000ff, 0);

    private final CanvasFrame canvasFrame;
    private Color color = Color.RED;
    private int threshold = 100;
    private Gate gate;
    private BufferedImage lastImage;
    private ProcessedImage lastProcessedImage;

    public VideoProcessor() {
        canvasFrame = new CanvasFrame("Detected video");
        canvasFrame.setSize(720, 576);
        canvasFrame.setLocation(800, 0);
        canvasFrame.setAlwaysOnTop(true);
    }

    @Override
    public void close() throws IOException {
        canvasFrame.dispose();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void imageReceived(BufferedImage image) {
        ImageSource imageSource = new BufferedImageSource(image);
        if (gate == null) {
            gate = new Gate(imageSource.getWidth(), imageSource.getHeight(), 1);
        }
        lastImage = image;
        lastProcessedImage = new ProcessedImage(imageSource, gate, color, threshold);

        PixelGroup detectedPixelGroup = lastProcessedImage.getDetectedPixelGroup();
//        autoThreshold(pixelGroup);
        autoGate(imageSource.getWidth(), imageSource.getHeight(), detectedPixelGroup);

        BufferedImage render = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        for (PixelGroup pixelGroup : lastProcessedImage.getPixelGroups()) {
            pixelGroup.draw(render, 0xFFFFFFFF);
        }
        if (detectedPixelGroup != null) {
            detectedPixelGroup.draw(render, 0xFFFFFF00);
        }
        drawDelta(render, image.getWidth(), image.getHeight(), 0xFFF000F0);
        drawGate(render, 0xFFF000F0);
        canvasFrame.showImage(render);
    }

    @Override
    public Delta getDelta() {
        if (lastProcessedImage == null) {
            return null;
        }
        double scale = gate.getSize() * 100 - 50;
        return new Delta(lastProcessedImage.getDeltaX(), lastProcessedImage.getDeltaY(), (int) scale);
    }

    @Override
    public void capture() {
        ImageUtil.saveAsPng(lastImage, "PHOTO.png");
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

//    private void autoThreshold(PixelGroup pixelGroup) {
//        // Update the threshold.
//        if (pixelGroup == null) {
//            threshold = (int) (threshold * 0.9);
//        } else if (threshold < pixelGroup.getMean() * 0.95) {
//            threshold = (pixelGroup.getMean() + pixelGroup.getMax()) / 2;
//        }
//    }

    private void autoGate(int width, int height, PixelGroup pixelGroup) {
        if (pixelGroup != null) {
            if (pixelGroup.getTop() == gate.getTop() ||
                    pixelGroup.getBottom() == gate.getBottom() ||
                    pixelGroup.getLeft() == gate.getLeft() ||
                    pixelGroup.getRight() == gate.getRight()) {
                // Expand the gate.
                this.gate = new Gate(width, height, gate.getSize() + 0.05);
            } else {
                // Shrink the gate.
                double topDiff = pixelGroup.getTop() - gate.getTop();
                double bottomDiff = gate.getBottom() - pixelGroup.getBottom();
                double leftDiff = pixelGroup.getLeft() - gate.getLeft();
                double rightDiff = gate.getRight() - pixelGroup.getRight();

                double topFactor = topDiff / height;
                double bottomFactor = bottomDiff / height;
                double leftFactor = leftDiff / width;
                double rightFactor = rightDiff / width;

                double smallestVerticalFactor = Math.min(topFactor, bottomFactor);
                double smallestHorizontalFactor = Math.min(leftFactor, rightFactor);
                double smallestFactor = Math.min(smallestVerticalFactor, smallestHorizontalFactor);
                double resizeFactor = 1 - smallestFactor;
                double size = gate.getSize() * resizeFactor;
                this.gate = new Gate(width, height, size);
            }
        } else if (gate.getSize() < 1) {
            // Expand the gate.
            this.gate = new Gate(width, height, gate.getSize() + 0.05);
        }
    }

    private void drawDelta(BufferedImage render, int width, int height, int rgbPixel) {
        Delta delta = getDelta();
        int x = (int) ((width / 2) + delta.getX());
        int y = (int) ((height / 2) + delta.getY());
        drawRectangle(render, x - 1, y - 1, 2, 2, rgbPixel);
    }

    public void drawGate(BufferedImage image, int rgbPixel) {
        drawRectangle(image, gate.getX(), gate.getY(), gate.getWidth(), gate.getHeight(), rgbPixel);
    }

    public void drawRectangle(BufferedImage image, int x, int y, int width, int height, int rgbPixel) {
        drawHorizontalLine(image, x, y, width, rgbPixel);
        drawHorizontalLine(image, x, y + height - 1, width, rgbPixel);
        drawVerticalLine(image, x, y, height, rgbPixel);
        drawVerticalLine(image, x + width - 1, y, height, rgbPixel);
    }

    public void drawHorizontalLine(BufferedImage image, int x, int y, int width, int rgbPixel) {
        for (int column = x; column < width + x; column++) image.setRGB(column, y, rgbPixel);
    }

    public void drawVerticalLine(BufferedImage image, int x, int y, int height, int rgbPixel) {
        for (int line = y; line < height + y; line++) image.setRGB(x, line, rgbPixel);
    }

    private static BufferedImage gray(BufferedImage image) {
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(image, 0, 0, null);
        return result;
    }

    private static BufferedImage singleColor(BufferedImage image, ColorModel colorModel) {
        WritableRaster raster = colorModel.createCompatibleWritableRaster(image.getWidth(), image.getHeight());
        BufferedImage newImage = new BufferedImage(colorModel, raster, false, null);
        newImage.getGraphics().drawImage(image, 0, 0, null);
        return newImage;
    }
}
