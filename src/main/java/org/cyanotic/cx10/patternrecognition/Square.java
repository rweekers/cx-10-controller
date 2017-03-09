package org.cyanotic.cx10.patternrecognition;

import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.utils.ImageConverter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Created by Gerry on 9-3-2017.
 */
public class Square {

    int thresh = 50;
    opencv_core.CvMemStorage storage;

    public Square() {
        storage = opencv_core.cvCreateMemStorage(0);
    }

    public CvSeq findSquares4(IplImage img) {
        // Java translation: moved into loop
        // CvSeq contours = new CvSeq();
        int i, c, l, N = 11;
        opencv_core.CvSize sz = cvSize(img.width() & -2, img.height() & -2);
        IplImage timg = cvCloneImage(img); // make a copy of input image
        IplImage gray = cvCreateImage(sz, 8, 1);
        IplImage pyr = cvCreateImage(cvSize(sz.width() / 2, sz.height() / 2), 8, 3);
        IplImage tgray = null;
        // Java translation: moved into loop
        // CvSeq result = null;
        // double s = 0.0, t = 0.0;

        // create empty sequence that will contain points -
        // 4 points per square (the square's vertices)
        CvSeq squares = cvCreateSeq(0, Loader.sizeof(CvSeq.class), Loader.sizeof(CvPoint.class), storage);

        // select the maximum ROI in the image
        // with the width and height divisible by 2
        cvSetImageROI(timg, cvRect(0, 0, sz.width(), sz.height()));

        // down-scale and upscale the image to filter out the noise
        cvPyrDown(timg, pyr, 7);
        cvPyrUp(pyr, timg, 7);
        tgray = cvCreateImage(sz, 8, 1);

        // find squares in every color plane of the image
        for (c = 0; c < 3; c++) {
            // extract the c-th color plane
            cvSetImageCOI(timg, c + 1);
            cvCopy(timg, tgray);

            // try several threshold levels
            for (l = 0; l < N; l++) {
                // hack: use Canny instead of zero threshold level.
                // Canny helps to catch squares with gradient shading
                if (l == 0) {
                    // apply Canny. Take the upper threshold from slider
                    // and set the lower to 0 (which forces edges merging)
                    cvCanny(tgray, gray, 0, thresh, 5);
                    // dilate canny output to remove potential
                    // holes between edge segments
                    cvDilate(gray, gray, null, 1);
                } else {
                    // apply threshold if l!=0:
                    //     tgray(x,y) = gray(x,y) < (l+1)*255/N ? 255 : 0
                    cvThreshold(tgray, gray, (l + 1) * 255 / N, 255, CV_THRESH_BINARY);
                }

                // find contours and store them all as a list
                // Java translation: moved into the loop
                CvSeq contours = new CvSeq();
                cvFindContours(gray, storage, contours, Loader.sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));

                // test each contour
                while (contours != null && !contours.isNull()) {
                    // approximate contour with accuracy proportional
                    // to the contour perimeter
                    // Java translation: moved into the loop
                    CvSeq result = cvApproxPoly(contours, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, cvContourPerimeter(contours) * 0.02, 0);
                    // square contours should have 4 vertices after approximation
                    // relatively large area (to filter out noisy contours)
                    // and be convex.
                    // Note: absolute value of an area is used because
                    // area may be positive or negative - in accordance with the
                    // contour orientation
                    if (result.total() == 4 && Math.abs(cvContourArea(result, CV_WHOLE_SEQ, 0)) > 1000 && cvCheckContourConvexity(result) != 0) {

                        // Java translation: moved into loop
                        double s = 0.0, t = 0.0;

                        for (i = 0; i < 5; i++) {
                            // find minimum angle between joint
                            // edges (maximum of cosine)
                            if (i >= 2) {
                                //      Java translation:
                                //          Comment from the HoughLines.java sample code:
                                //          "    Based on JavaCPP, the equivalent of the C code:
                                //                  CvPoint* line = (CvPoint*)cvGetSeqElem(lines,i);
                                //                  CvPoint first=line[0];
                                //                  CvPoint second=line[1];
                                //          is:
                                //                  Pointer line = cvGetSeqElem(lines, i);
                                //                  CvPoint first = new CvPoint(line).position(0);
                                //                  CvPoint second = new CvPoint(line).position(1);
                                //          "
                                //          ... so after some trial and error this seem to work
//                                t = fabs(angle(
//                                        (CvPoint*)cvGetSeqElem( result, i ),
//                                        (CvPoint*)cvGetSeqElem( result, i-2 ),
//                                        (CvPoint*)cvGetSeqElem( result, i-1 )));
                                t = Math.abs(angle(new CvPoint(cvGetSeqElem(result, i)),
                                        new CvPoint(cvGetSeqElem(result, i - 2)),
                                        new CvPoint(cvGetSeqElem(result, i - 1))));
                                s = s > t ? s : t;
                            }
                        }

                        // if cosines of all angles are small
                        // (all angles are ~90 degree) then write quandrange
                        // vertices to resultant sequence
                        if (s < 0.3)
                            for (i = 0; i < 4; i++) {
                                cvSeqPush(squares, cvGetSeqElem(result, i));
                            }
                    }

                    // take the next contour
                    contours = contours.h_next();
                }
            }
        }

        // release all the temporary images
        cvReleaseImage(gray);
        cvReleaseImage(pyr);
        cvReleaseImage(tgray);
        cvReleaseImage(timg);

        return squares;
    }

    // helper function:
    // finds a cosine of angle between vectors
    // from pt0->pt1 and from pt0->pt2
    double angle(CvPoint pt1, CvPoint pt2, CvPoint pt0) {
        double dx1 = pt1.x() - pt0.x();
        double dy1 = pt1.y() - pt0.y();
        double dx2 = pt2.x() - pt0.x();
        double dy2 = pt2.y() - pt0.y();

        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    public boolean hasCorrectColor(IplImage iplImage, CvSeq squares) throws IOException {
        IplImage cpy = cvCloneImage(iplImage);
        CvSlice slice = new CvSlice(squares);
        System.out.println("total vertices: " + squares.total());

        Set<String> algehad = new HashSet<>();

        boolean gevonden = false;
        for (int i = 0; !gevonden && i < squares.total(); i += 4) {

            CvPoint rect = new CvPoint(4);

            IntPointer count = new IntPointer(1).put(4);
            // get the 4 corner slice from the "super"-slice
            cvCvtSeqToArray(squares, rect, slice.start_index(i).end_index(i + 4));

            //  IplImage cpy = cvCreateImage(, 8,3)
            String position = rect.position(0).toString();
            if (!algehad.contains(position)) {
                algehad.add(position);
                System.out.println("Rect 0: " + position);

                gevonden = hasColor(iplImage, rect.position(0));
                if (gevonden) {
                    cvPolyLine(cpy, rect.position(0), count, 1, 1, CV_RGB(0, 255, 0), 3, CV_AA, 0);
                }
            }
        }
        ImageIO.write(ImageConverter.convertImage(cpy), "png", new File("image-copy.png"));

        return gevonden;
    }

    private boolean hasColor(IplImage iplImage, CvPoint position) {
        int min_x = position.x();
        int min_y = position.y();
        int max_x = position.x();
        int max_y = position.y();

        for (int i= 1; i < 4; i++) {
            CvPoint point = position.position(i);
            int x = point.x();
            int y = point.y();

            min_x = Math.min(min_x, x);
            max_x = Math.max(max_x, x);

            min_y = Math.min(min_y, y);
            max_y = Math.max(max_y, y);
        }

        CvScalar s, c;

        boolean gevonden = true;
        for (int i = min_x; gevonden && i < max_x; i++) {
            for (int j = min_y; gevonden && j < max_y; j++) {
                s = cvGet2D(iplImage, i,j);
                if (s.val(0) < 50 && s.val(1) > 100 && s.val(2) < 100) {
                    gevonden = true;
                } else {
                    gevonden = false;
                }
            }
        }
        return gevonden;
    }

}
