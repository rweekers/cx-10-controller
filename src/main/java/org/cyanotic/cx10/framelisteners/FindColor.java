package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.concurrent.ScheduledExecutorService;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class FindColor extends SwingVideoPlayer {
    final int CAMERA_NUM = 0; // Default camera for this time

    static opencv_core.CvScalar rgba_min = cvScalar(0, 0, 130, 0);// RED wide dabur birko
    static opencv_core.CvScalar rgba_max = cvScalar(80, 80, 255, 0);

    private final OpenCVFrameConverter.ToIplImage iplConverter;
    private final Java2DFrameConverter java2dConverter;

    private static final int CENTER_X = 720 / 2;
    private static final int CENTER_Y = 576 / 2;

    private static final opencv_core.CvPoint CENTER = new opencv_core.CvPoint(CENTER_X, CENTER_Y);

    private int posX;
    private int posY;

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
        posX = (int) (mom10 / area);
        posY = (int) (mom01 / area);

        // only if its a valid position
        if (posX > 0 && posY > 0) {
            paint(iplImage, posX, posY);
        }

        super.imageReceived(java2dConverter.getBufferedImage(iplConverter.convert(iplImage), 1));
    }

    public opencv_core.CvPoint getDistanceToCenter() {
        if (posX <= 0 || posY <= 0) {
            return null;
        }

        return new opencv_core.CvPoint(posX - CENTER_X, posY - CENTER_Y);
    }

    private opencv_core.IplImage getThresholdImage(opencv_core.IplImage orgImg) {
        opencv_core.IplImage imgThreshold = cvCreateImage(cvGetSize(orgImg), 8, 1);
        cvInRangeS(orgImg, rgba_min, rgba_max, imgThreshold);// red

        cvSmooth(imgThreshold, imgThreshold, CV_MEDIAN, 15, 0, 0, 0);
        return imgThreshold;
    }

    private void paint(opencv_core.IplImage image, int posX, int posY) {
        opencv_core.CvPoint point = toCvPoint(getPointForDetectedColour(posX, posY));

        final String text = ">>>>>>> Detected spot => " + getDirections(posX, posY);
        System.out.println(text);

        //TODO Doet niet?
        cvPutText(image, text, new opencv_core.CvPoint(0, 0), new CvFont().font_face(CV_FONT_HERSHEY_PLAIN).thickness(10).color(opencv_core.CvScalar.GREEN), opencv_core.CvScalar.GREEN);

        cvDrawCircle(image, point, 20, opencv_core.CvScalar.GREEN, 3, 8, 0);
        cvDrawLine(image, point, CENTER, opencv_core.CvScalar.BLUE, 1, 8, 0);

        System.out.println("x:" + getDistanceToCenter().x() + " y: " + getDistanceToCenter().y() + ", distance from center: " + euclideanDist(
                getPointForDetectedColour(posX, posY),
                getPointForImageCenter()));

    }

    private CvPoint toCvPoint(Point point) {
        return new opencv_core.CvPoint(point.x, point.y);
    }

    private Point getPointForImageCenter() {
        return new Point(CENTER.x(), CENTER.y());
    }

    private Point getPointForDetectedColour(int posX, int posY) {
        return new Point(posX, posY);
    }

    double euclideanDist(Point p, Point q) {
        return p.distance(q);
    }

    private String getDirections(int posX, int posY) {
        final float angle = getAngle(new Point(posX, posY));
        final StringBuilder builder = new StringBuilder();
        builder.append("Angle is : " + angle + ", go => ");

        if (angle == 0) {
            return builder.append("UP").toString();
        } else if (angle > 0 && angle < 90) {
            return builder.append("UP and RIGHT").toString();
        } else if (angle == 90) {
            return builder.append("RIGHT").toString();
        } else if (angle > 90 && angle < 180) {
            return builder.append("DOWN and RIGHT").toString();
        } else if (angle == 180) {
            return builder.append("DOWN").toString();
        } else if (angle > 180 && angle < 270) {
            return builder.append("DOWN and LEFT").toString();
        } else if (angle == 270) {
            return builder.append("LEFT").toString();
        } else if (angle > 270 && angle < 360) {
            return builder.append("UP and LEFT").toString();
        } else if (angle == 360) {
            return builder.append("UP").toString();
        } else {
            return builder.append("DOWN?").toString();
        }
    }

    public float getAngle(Point target) {
        int originX = 0;
        int originY = 0;
        float angle = (float) Math.toDegrees(Math.atan2(target.getY() - originY, target.getX() - originX));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
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
