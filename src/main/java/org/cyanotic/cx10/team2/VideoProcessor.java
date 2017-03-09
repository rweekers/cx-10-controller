package org.cyanotic.cx10.team2;

import org.bytedeco.javacv.CanvasFrame;
import org.cyanotic.cx10.api.ImageListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gerard on 8-3-17.
 */
public class VideoProcessor extends ImageListener implements Processor {

    private static final ColorModel RED_COLOR_MODEL = new DirectColorModel(8, 0x000000ff, 0, 0, 0);
    private static final ColorModel GREEN_COLOR_MODEL = new DirectColorModel(8, 0, 0x000000ff, 0, 0);
    private static final ColorModel BLUE_COLOR_MODEL = new DirectColorModel(8, 0, 0, 0x000000ff, 0);

    private final CanvasFrame canvasFrame;
    private Color color = Color.RED;
    private int threshold = 100;
    private Gate gate;
    private BufferedImage lastImage;
    private ProcessedImage lastProcessedImage;

    public VideoProcessor(ScheduledExecutorService executor) {
        super(executor);
        canvasFrame = new CanvasFrame("Detected video");
        canvasFrame.setSize(720, 576);
    }

    @Override
    public void close() throws IOException {
        canvasFrame.dispose();
    }

    @Override
    public void imageReceived(BufferedImage image) {
        ImageSource imageSource = new BufferedImageSource(image);
        if (gate == null) {
            gate = new Gate(imageSource.getWidth(), imageSource.getHeight(), 1);
        }
        lastImage = image;
        lastProcessedImage = new ProcessedImage(imageSource, gate, color, threshold);
//        updateMean(lastProcessedImage);
//        updateThreshold(lastProcessedImage);
        autoGate(imageSource.getWidth(), imageSource.getHeight(), lastProcessedImage);

        PixelGroup brightestPixelGroup = lastProcessedImage.getDetectedPixelGroup();
        if (brightestPixelGroup != null) {
            BufferedImage render = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            brightestPixelGroup.draw(render, 0xFFFFFFFF);
            gate.draw(render, 0xFFF000F0);
            canvasFrame.showImage(render);
        }
    }

    @Override
    public Delta getDelta() {
        if (lastProcessedImage == null) {
            return null;
        }
        return new Delta(lastProcessedImage.getDeltaX(), lastProcessedImage.getDeltaY(), (int) (gate.getSize() * 100));
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

    private void autoThreshold(ProcessedImage processedImage) {
        // Update the threshold.
        PixelGroup pixelGroup = processedImage.getDetectedPixelGroup();
        if (pixelGroup == null) {
            threshold = (int) (threshold * 0.9);
        } else if (threshold < pixelGroup.getMean() * 0.95) {
            threshold = (pixelGroup.getMean() + pixelGroup.getMax()) / 2;
        }
    }

    private void autoGate(int width, int height, ProcessedImage processedImage) {
        // Update the gate.
        PixelGroup pixelGroup = processedImage.getDetectedPixelGroup();
        if (pixelGroup == null && gate.getSize() < 1) {
            this.gate = new Gate(width, height, gate.getSize() + 0.05);
        } else {
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
