package org.cyanotic.cx10.net;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class H264Decoder {
    private final CX10NalDecoder cx10NalDecoder;
    private final PipedOutputStream outputStream;
    private final FrameGrabber grabber;
    private boolean initialized = false;
    private boolean closed = false;

    public H264Decoder(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.cx10NalDecoder = cx10NalDecoder;
        this.outputStream = new PipedOutputStream();
        this.grabber = new FFmpegFrameGrabber(new PipedInputStream(outputStream));
    }

    public boolean isConnected() {
        return cx10NalDecoder.isConnected();
    }

    public void connect() throws IOException {
        if (closed) {
            throw new IOException("Already closed!");
        }
        if (isConnected()) {
            throw new IOException("Already connected");
        }
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
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, cx10NalDecoder.toString()).start();
    }

    public void close() throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected");
        }
        if (closed) {
            throw new IOException("Already closed!");
        }
        closed = true;
        cx10NalDecoder.close();
        grabber.close();
    }

    public Frame readFrame() throws FrameGrabber.Exception {
        if (!isConnected()) {
            return null;
        }
        if (!initialized) {
            initialized = true;
            grabber.start();
        }
        return grabber.grab();
    }
}
