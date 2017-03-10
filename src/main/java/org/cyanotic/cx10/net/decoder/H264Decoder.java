package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class H264Decoder implements Closeable {
    private final FrameGrabber frameGrabber;
    private boolean initialized = false;

    public H264Decoder(InputStream inputStream) throws IOException {
        this(new FFmpegFrameGrabber(inputStream));
    }

    protected H264Decoder(FrameGrabber frameGrabber) {
        this.frameGrabber = frameGrabber;
    }

    @Override
    public void close() throws IOException {
        frameGrabber.close(); // FIXME: causes a SIGTERM in the JVM
    }

    public Frame readFrame() throws IOException {
        if (!initialized) {
            initialized = true;
            frameGrabber.start();
        }
        return frameGrabber.grab();
    }
}
