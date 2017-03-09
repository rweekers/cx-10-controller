package org.cyanotic.cx10.team2;

import org.bytedeco.javacv.CanvasFrame;
import org.cyanotic.cx10.api.ImageListener;

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

    private final CanvasFrame detectedGroup = new CanvasFrame("Detected");
    private Color color = Color.RED;
    private Gate gate;
    private ProcessedImage lastProcessedImage;
    private int threshold = 100;

    public VideoProcessor(ScheduledExecutorService executor) {
        super(executor);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void imageReceived(BufferedImage image) {
        ImageSource imageSource = new BufferedImageSource(image);
        if (gate == null) {
            gate = new Gate(imageSource.getWidth(), imageSource.getHeight(), 1);
        }
        lastProcessedImage = new ProcessedImage(imageSource, gate, color, threshold);
//        updateMean(lastProcessedImage);
//        updateThreshold(lastProcessedImage);
        updateGate(imageSource.getWidth(), imageSource.getHeight(), lastProcessedImage);

        PixelGroup brightestPixelGroup = lastProcessedImage.getDetectedPixelGroup();
        if (brightestPixelGroup != null) {
            BufferedImage render = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
            brightestPixelGroup.draw(render, 0xFF000000);
            gate.draw(render, 0xFFF000F0);
            detectedGroup.showImage(render);
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
    public boolean isReadyForCapture() {
        // todo!

        return false;
    }

    @Override
    public void capture() {
        // todo: save last image
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    private void updateThreshold(ProcessedImage processedImage) {
        // Update the threshold.
        PixelGroup pixelGroup = processedImage.getDetectedPixelGroup();
        if (pixelGroup == null) {
            threshold = (int) (threshold * 0.9);
        } else if (threshold < pixelGroup.getMean() * 0.95) {
            threshold = (pixelGroup.getMean() + pixelGroup.getMax()) / 2;
        }
    }

    private void updateGate(int width, int height, ProcessedImage processedImage) {
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
