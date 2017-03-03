package org.cyanotic.cx10.io.video;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by gerard on 3-3-17.
 */
public class VideoRecorder implements IVideoPlayer {

    private final File file;
    private FrameRecorder frameRecorder;
    private AtomicBoolean recorderIntialized;

    public VideoRecorder() {
        this("output-" + Instant.now().toString().replace(':', '.') + ".avi");
    }

    public VideoRecorder(String fileName) {
        this(new File(fileName));
    }

    public VideoRecorder(File file) {
        this.file = file;
    }

    @Override
    public void start() {
        frameRecorder = new FFmpegFrameRecorder(file, 720, 576);
        recorderIntialized = new AtomicBoolean();
    }

    @Override
    public void stop() {
        if (recorderIntialized.compareAndSet(true, false)) {
            try {
                frameRecorder.close();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        frameRecorder = null;
    }

    @Override
    public boolean isActive() {
        return frameRecorder != null;
    }

    @Override
    public void imageReceived(Frame frame) {
        if (!isActive()) {
            return;
        }
        if (recorderIntialized.compareAndSet(false, true)) {
            try {
                frameRecorder.start();
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        try {
            frameRecorder.record(frame);
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "VideoRecorder";
    }
}
