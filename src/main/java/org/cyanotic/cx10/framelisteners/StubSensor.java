package org.cyanotic.cx10.framelisteners;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.controllers.HackatonController;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class StubSensor extends ImageListener {

    private HackatonController hackatonController;

    public StubSensor(HackatonController hackatonController, ExecutorService executor) {
        super(executor);
        this.hackatonController = hackatonController;
    }

    @Override public void imageReceived(final BufferedImage image) {
        System.out.println("image received!");
        opencv_core.IplImage iplImage = convert(image);
        opencv_core.Mat matImage = new opencv_core.Mat(iplImage);
        opencv_core.MatVector vector = new opencv_core.MatVector();
        opencv_core.Mat greyMat = new opencv_core.Mat();
        opencv_imgproc.cvtColor(matImage, greyMat, opencv_imgproc.CV_BGR2GRAY);
        opencv_core.Mat maskedMat = new opencv_core.Mat();
        opencv_imgproc.threshold(greyMat, maskedMat, 0, 255, opencv_imgproc.CV_THRESH_BINARY_INV | opencv_imgproc.CV_THRESH_OTSU);
        opencv_imgproc.findContours(maskedMat, vector, opencv_imgproc.CV_SHAPE_RECT, opencv_imgproc.CHAIN_APPROX_TC89_L1);
        System.out.println("sending image data to controller");
        //vertel iets aan controller
        hackatonController.onReceiveImageData(vector);

    }

    @Override public void close() throws IOException {

    }

    private opencv_core.IplImage convert(BufferedImage image) {
        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        return iplConverter.convert(java2dConverter.convert(image));
    }
}
