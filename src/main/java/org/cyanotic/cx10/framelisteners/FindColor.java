package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ScheduledExecutorService;

import static org.bytedeco.javacpp.opencv_core.IPL_DEPTH_8U;
import static org.bytedeco.javacpp.opencv_core.cvCreateImage;
import static org.bytedeco.javacpp.opencv_core.cvFlip;
import static org.bytedeco.javacpp.opencv_core.cvGetSize;
import static org.bytedeco.javacpp.opencv_core.cvInRangeS;
import static org.bytedeco.javacpp.opencv_core.cvScalar;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class FindColor extends SwingVideoPlayer {
    final int CAMERA_NUM = 0; // Default camera for this time

    static opencv_core.CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
    static opencv_core.CvScalar rgba_max = cvScalar(80, 80, 255, 0);
    private final OpenCVFrameConverter.ToIplImage iplConverter;
    private final Java2DFrameConverter java2dConverter;

    public FindColor(ScheduledExecutorService executor) {
        super(executor);

        iplConverter = new OpenCVFrameConverter.ToIplImage();
        java2dConverter = new Java2DFrameConverter();
    }

    @Override
    public void imageReceived(BufferedImage image) {

        opencv_core.IplImage iplImage = iplConverter.convert(java2dConverter.convert(image));

        // show image on window
        opencv_core.IplImage detectThrs = getThresholdImage(iplImage);

        opencv_imgproc.CvMoments moments = new opencv_imgproc.CvMoments();
        cvMoments(detectThrs, moments, 1);
        double mom10 = cvGetSpatialMoment(moments, 1, 0);
        double mom01 = cvGetSpatialMoment(moments, 0, 1);
        double area = cvGetCentralMoment(moments, 0, 0);
        int posX = (int) (mom10 / area);
        int posY = (int) (mom01 / area);

        // only if its a valid position
        if (posX > 0 && posY > 0) {
            paint(iplImage, posX, posY);
        }

        super.imageReceived(java2dConverter.getBufferedImage(iplConverter.convert(iplImage), 1));
    }

    private opencv_core.IplImage getThresholdImage(opencv_core.IplImage orgImg) {
        opencv_core.IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        //
        cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red

        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15,0,0,0);
        //cvSaveImage(++ii + "dsmthreshold.jpg", imgThreshold);
        return imgThreshold;
    }

    private void paint(opencv_core.IplImage image, int posX, int posY) {

        cvDrawCircle(image, new opencv_core.CvPoint(posX, posY), 20, opencv_core.CvScalar.GREEN, 3, CV_AA, 0);
        System.out.println(posX + " , " + posY);

    }

    public opencv_core.IplImage Equalize(BufferedImage bufferedimg) {
        Java2DFrameConverter converter1 = new Java2DFrameConverter();
        OpenCVFrameConverter.ToIplImage converter2 = new OpenCVFrameConverter.ToIplImage();
        opencv_core.IplImage iploriginal = converter2.convert(converter1.convert(bufferedimg));
        opencv_core.IplImage srcimg = opencv_core.IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        opencv_core.IplImage destimg = opencv_core.IplImage.create(iploriginal.width(), iploriginal.height(), IPL_DEPTH_8U, 1);
        cvCvtColor(iploriginal, srcimg, CV_BGR2GRAY);
        cvEqualizeHist(srcimg, destimg);
        return destimg;
    }
}
