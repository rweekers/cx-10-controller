package org.cyanotic.cx10.imagelisteners;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.patternrecognition.Square;
import org.cyanotic.cx10.utils.ImageConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Gerry on 9-3-2017.
 */
public class RectangeDetector extends ImageListener {

    public static int i = 0;

    public RectangeDetector(ExecutorService executorService) {
        super(executorService);
    }

    @Override
    public void imageReceived(BufferedImage image) {
        try {
            opencv_core.IplImage iplImage = ImageConverter.convertImage(image);
            Square square = new Square();
            opencv_core.CvSeq squares = square.findSquares4(iplImage);
            int total = squares.total();

            if (total >= 4 && square.hasCorrectColor(iplImage, squares)) {
                ImageIO.write((BufferedImage) image, "png", new File("image-" + i++ + ".png"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Er ging wat fout", e);
        }

    }
    @Override
    public void close() throws IOException {

    }
}
