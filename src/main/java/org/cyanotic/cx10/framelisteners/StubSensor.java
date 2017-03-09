package org.cyanotic.cx10.framelisteners;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.api.ImageListener;
import org.cyanotic.cx10.controllers.HackatonController;

public class StubSensor extends ImageListener {

    private HackatonController hackatonController;

    public StubSensor(HackatonController hackatonController, ExecutorService executor) {
        super(executor);
        this.hackatonController = hackatonController;
    }

    @Override public void imageReceived(final BufferedImage image) {
        //vertel iets aan controller
        opencv_core.KeyPointVector keyPointVector = new opencv_core.KeyPointVector();
        hackatonController.onReceiveImageData(keyPointVector);

    }

    @Override public void close() throws IOException {

    }

}
