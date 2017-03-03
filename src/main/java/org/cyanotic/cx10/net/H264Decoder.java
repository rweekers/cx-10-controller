package org.cyanotic.cx10.net;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class H264Decoder {
    private final CX10NalDecoder cx10NalDecoder;
    private final PipedOutputStream outputStream;
    private final FrameGrabber grabber;
    private final AtomicBoolean grabberIntialized;

    public H264Decoder(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.cx10NalDecoder = cx10NalDecoder;
        this.outputStream = new PipedOutputStream();
        this.grabber = new FFmpegFrameGrabber(new PipedInputStream(outputStream));
        this.grabberIntialized = new AtomicBoolean();
    }

    public boolean isConnected() {
        return cx10NalDecoder.isConnected();
    }

    public void connect() throws IOException {
        cx10NalDecoder.connect();
        new Thread(() -> {
            boolean error = false;
            while (isConnected() && !error) {
                try {
                    final byte[] data = cx10NalDecoder.readNal();
                    if (data != null) {
                        outputStream.write(data);
                    }
                } catch (IOException e) {
                    error = true;
                    e.printStackTrace();
                }
            }
            disconnect();
        }).start();
    }

    public void disconnect() {
        cx10NalDecoder.disconnect();
        if (grabberIntialized.compareAndSet(true, false)) {
            try {
                grabber.stop();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Frame readFrame() throws FrameGrabber.Exception {
        if (!isConnected()) {
            return null;
        }
        if (grabberIntialized.compareAndSet(false, true)) {
            grabber.start();
        }
        return grabber.grab();
    }
}
