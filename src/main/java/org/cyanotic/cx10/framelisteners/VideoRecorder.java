package org.cyanotic.cx10.framelisteners;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.cyanotic.cx10.api.FrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gerard on 3-3-17.
 */
public class VideoRecorder implements FrameListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final FrameRecorder frameRecorder;
    private final AtomicBoolean initialized = new AtomicBoolean();

    public VideoRecorder() {
        this("output-" + Instant.now().toString().replace(':', '.') + ".avi");
    }

    public VideoRecorder(String fileName) {
        this(new File(fileName));
    }

    public VideoRecorder(File file) {
        frameRecorder = new FFmpegFrameRecorder(file, 720, 576);
    }

    @Override
    public void close() throws IOException {
        frameRecorder.close();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void frameReceived(Frame frame) {
        if (initialized.compareAndSet(false, true)) {
            try {
                frameRecorder.start();
            } catch (FrameRecorder.Exception e) {
                logger.error("Failed to start frame recorder", e);
            }
        }
        try {
            frameRecorder.record(frame);
        } catch (FrameRecorder.Exception e) {
            logger.error("Failed to record frame", e);
        }
    }

}
