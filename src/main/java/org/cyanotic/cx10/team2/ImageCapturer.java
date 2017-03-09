package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.ImageListener;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

public class ImageCapturer extends ImageListener implements Capturer {

    public ImageCapturer(ExecutorService executor) {
        super(executor);
    }

    private BufferedImage lastImage;

    @Override
    public void close() throws IOException {

    }

    @Override
    public void imageReceived(BufferedImage image) {
        lastImage = image;
    }

    public void capture() {
        if (lastImage == null) {
            return;
        }

        // todo: save the last image
    }
}
