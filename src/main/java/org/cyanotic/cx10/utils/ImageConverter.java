package org.cyanotic.cx10.utils;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Gerry on 9-3-2017.
 */
public class ImageConverter {

    public static opencv_core.IplImage convertImage(Image image) {
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        return iplConverter.convert(frameConverter.convert((BufferedImage)image));
    }

    public static BufferedImage convertImage(opencv_core.IplImage image) {
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        return frameConverter.convert(iplConverter.convert(image));
    }

}
