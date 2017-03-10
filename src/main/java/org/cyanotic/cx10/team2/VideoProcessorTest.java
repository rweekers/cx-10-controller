package org.cyanotic.cx10.team2;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by gerard on 8-3-17.
 */
public class VideoProcessorTest {
    public static void main(String[] args) throws IOException {
        try {
            BufferedImage image = ImageIO.read(new File("PHOTO.png"));
            VideoProcessor videoProcessor = new VideoProcessor();
            for (int i = 0; i < 1000; i++) {
                videoProcessor.imageReceived(image);
                Thread.sleep(100);
            }
            // todo: figure out filtering

            // todo: create an image with only red pixel data

            // todo: create a separate CanvasFrame which shows the detected pixel groups

            // todo: detect pixel groups by sampling pixels and then growing from there

            // todo: create a stats overview with the amount of pixels sampled, duration, groups found, etc

            // todo: create something to dynamically adjust gate size / sample count / etc
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
