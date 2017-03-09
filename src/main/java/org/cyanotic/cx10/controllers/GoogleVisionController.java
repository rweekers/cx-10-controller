package org.cyanotic.cx10.controllers;

import com.google.cloud.vision.spi.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class GoogleVisionController implements Controller {

    public GoogleVisionController() {

    }

    @Override
    public Command getCommand() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    public static class GoogleVisionListener extends SwingVideoPlayer {
        private final ImageAnnotatorClient vision;

        public GoogleVisionListener(ScheduledExecutorService executor) throws IOException {
            super(executor);

            // Instantiates a client
            vision = ImageAnnotatorClient.create();
        }

        private final static int SAMPLE_RATE = 100;
        private final AtomicInteger currentSample = new AtomicInteger();

        @Override
        public void imageReceived(BufferedImage image) {
            super.imageReceived(image);

            if (currentSample.getAndIncrement() % SAMPLE_RATE != 0) {
                return;
            }

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                ImageIO.write(image, "bmp", baos);
                baos.flush();

                ByteString imgBytes = ByteString.copyFrom(baos.toByteArray());


                Image img = Image.newBuilder().setContent(imgBytes).build();
                Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
                AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .setImage(img)
                        .build();
                requests.add(request);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                    annotation.getAllFields().forEach((k, v)->System.out.printf("%s : %s\n", k, v.toString()));
                }
            }
        }

        @Override
        public void close() {
            super.close();

            try {
                vision.close();
            } catch (Exception e) {
                // Ignore for now
                e.printStackTrace();
            }
        }
    }
}
