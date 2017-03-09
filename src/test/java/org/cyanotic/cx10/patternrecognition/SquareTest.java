package org.cyanotic.cx10.patternrecognition;

import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.utils.ImageConverter;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Gerry on 9-3-2017.
 */
public class SquareTest {

    @Test
    public void detectColor() throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new File("image2-4.png"));

        opencv_core.IplImage iplImage = ImageConverter.convertImage(bufferedImage);
        Square square = new Square();
        opencv_core.CvSeq squares = square.findSquares4(iplImage);

        Assert.assertTrue(square.hasCorrectColor(iplImage, squares));
    }
}
