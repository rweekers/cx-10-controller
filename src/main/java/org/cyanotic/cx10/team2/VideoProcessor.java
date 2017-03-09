package org.cyanotic.cx10.team2;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

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
    private final SwingVideoPlayer videoPlayer;
    private ProcessedImage lastProcessedImage;
    private Gate gate;
    private int mean, threshold = 127;

    public VideoProcessor(ScheduledExecutorService executor) {
        super(executor);
        videoPlayer = new SwingVideoPlayer(executor);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void imageReceived(BufferedImage image) {
        videoPlayer.imageReceived(image);

        ImageSource imageSource = new BufferedImageSource(image);
        if (gate == null) {
            gate = new Gate(imageSource.getWidth(), imageSource.getHeight(), 1);
        }
        lastProcessedImage = new ProcessedImage(imageSource, gate, mean, threshold);
        updateMean(lastProcessedImage);
        updateThreshold(lastProcessedImage);
        updateGate(lastProcessedImage);

        gate.draw(image, 0xFF000000);
        lastProcessedImage.getBrightestPixelGroup().draw(image, 0xFFF000F0);
        videoPlayer.imageReceived(image);
    }

    @Override
    public Delta getDelta() {
        if (lastProcessedImage == null) {
            return null;
        }
        return new Delta(lastProcessedImage.getDeltaX(), lastProcessedImage.getDeltaY(), (int) (gate.getSize() * 100));
    }

    @Override
    public void setColor(String color) {

    }

    private void updateMean(ProcessedImage processedImage) {
        mean = (int) (processedImage.getSum() / gate.getPixelCount());
    }

    private void updateThreshold(ProcessedImage processedImage) {
        // Update the threshold.
        PixelGroup brightestGroup = processedImage.getBrightestPixelGroup();
        if (brightestGroup == null) threshold = (int) (threshold * 0.9);
        else if (threshold < brightestGroup.getMean() * 0.95)
            threshold = (brightestGroup.getMean() + brightestGroup.getMax()) / 2;
    }

    private void updateGate(ProcessedImage processedImage) {
        // Update the gate.
        PixelGroup brightestGroup = processedImage.getBrightestPixelGroup();
        if (brightestGroup == null ||
                brightestGroup.getTop() > gate.getTop() + 5 ||
                brightestGroup.getBottom() > gate.getBottom() - 5 ||
                brightestGroup.getLeft() > gate.getLeft() + 5 ||
                brightestGroup.getRight() > gate.getRight() - 5) this.gate = gate.resize(1.01);
        else if (brightestGroup.getCount() > 50 &&
                brightestGroup.getTop() + 10 >= gate.getTop() &&
                brightestGroup.getBottom() - 10 <= gate.getBottom() &&
                brightestGroup.getLeft() + 10 >= gate.getLeft() &&
                brightestGroup.getRight() - 10 <= gate.getRight()) this.gate = gate.resize(0.99);
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
