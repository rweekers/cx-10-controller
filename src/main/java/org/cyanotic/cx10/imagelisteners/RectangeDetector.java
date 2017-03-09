
package org.cyanotic.cx10.imagelisteners;

import nl.craftsmen.cx10.measure.MeasuredValuesCache;
import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.patternrecognition.Square;
import org.cyanotic.cx10.utils.ImageConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Gerry on 9-3-2017.
 */
public class RectangeDetector extends ImageListener {

    public static int i = 0;

    private MeasuredValuesCache measuredValues ;

    public RectangeDetector(ExecutorService executorService, MeasuredValuesCache measuredValues) {
        super(executorService);
        this.measuredValues = measuredValues;
        this.measuredValues.measurementAvailable=false;

    }

    @Override
    public void imageReceived(BufferedImage image) {
        try {
            ImageIO.write(image, "png", new File("temp.png"));
            opencv_core.IplImage iplImage = ImageConverter.convertImage(image);
            Square square = new Square();
            opencv_core.CvSeq squares = square.findSquares4(iplImage);
            int total = squares.total();

            if (total >= 4 && square.hasCorrectColor(iplImage, squares, measuredValues)) {
                ImageIO.write(image, "png", new File("image2-" + i++ + ".png"));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Er ging wat fout", e);
        }

    }

    @Override
    public void close() throws IOException {

    }
}
