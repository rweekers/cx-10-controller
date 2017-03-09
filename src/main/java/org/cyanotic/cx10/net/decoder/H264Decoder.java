package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.Closeable;
import java.io.IOException;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class H264Decoder implements Closeable {
    private final FrameGrabber grabber;
    private boolean initialized = false;

    public H264Decoder(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.grabber = new FFmpegFrameGrabber(cx10NalDecoder);
    }

    @Override
    public void close() throws IOException {
        grabber.close(); // FIXME: causes a SIGTERM in the JVM
    }

    public Frame readFrame() throws IOException {
        if (!initialized) {
            initialized = true;
            grabber.start();
        }
        return grabber.grab();
    }
}
