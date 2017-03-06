package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.cyanotic.cx10.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class H264Decoder implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CX10NalDecoder cx10NalDecoder;
    private final PipedOutputStream outputStream;
    private final FrameGrabber grabber;
    private boolean initialized = false;
    private boolean closed = false;

    public H264Decoder(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.cx10NalDecoder = cx10NalDecoder;
        this.outputStream = new PipedOutputStream();
        this.grabber = new FFmpegFrameGrabber(new PipedInputStream(outputStream));
        ExecutorUtils.scheduleVideoDecoder(() -> {
            try {
                while (isConnected()) {
                    final byte[] data = cx10NalDecoder.readNal();
                    if (data != null) {
                        outputStream.write(data);
                    }
                }
                close();
            } catch (IOException e) {
                logger.error("Failed to decode frame", e);
            }
        }).start();
    }

    @Override
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

    public boolean isConnected() {
        return cx10NalDecoder.isConnected();
    }

    public Frame readFrame() throws IOException {
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
